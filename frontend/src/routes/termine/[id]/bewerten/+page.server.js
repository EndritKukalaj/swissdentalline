import { env } from '$env/dynamic/private';
import { error, fail, redirect } from '@sveltejs/kit';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';

export const load = async ({ params, locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    if (userRole !== 'Patient') {
        throw error(403, 'Nur Patienten können Bewertungen erstellen');
    }

    try {
        const jwt_token = locals.jwt_token;
        const terminId = params.id;
        const auth0UserId = locals.user.sub;
        const patientId = auth0UserId.replace('auth0|', '');

        // Fetch termin details
        const terminResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!terminResponse.ok) {
            throw error(404, 'Termin nicht gefunden');
        }

        const termin = await terminResponse.json();
        const terminPatientId = termin.patientId || termin.patient_id;

        // Verify patient owns the termin
        if (terminPatientId !== patientId) {
            throw error(403, 'Sie können nur eigene Termine bewerten');
        }

        // Verify termin is ABGESCHLOSSEN
        if (termin.status !== 'ABGESCHLOSSEN') {
            throw error(400, 'Nur abgeschlossene Termine können bewertet werden');
        }

        // Fetch zahnarzt details
        const zahnarztId = termin.zahnarztId || termin.zahnarzt_id;
        const zahnarztResponse = await fetch(`${API_BASE_URL}/zahnaerzte/${zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });

        const zahnarzt = zahnarztResponse.ok ? await zahnarztResponse.json() : null;

        // Fetch behandlungsart details
        const behandlungsartId = termin.behandlungsartId || termin.behandlungsart_id;
        const behandlungsartResponse = await fetch(`${API_BASE_URL}/behandlungsarten/${behandlungsartId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });

        const behandlungsart = behandlungsartResponse.ok ? await behandlungsartResponse.json() : null;

        // Fetch praxis details if zahnarzt has praxisAdresseId
        let praxis = null;
        if (zahnarzt?.praxisAdresseId) {
            const praxisResponse = await fetch(`${API_BASE_URL}/adressen/${zahnarzt.praxisAdresseId}`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (praxisResponse.ok) {
                praxis = await praxisResponse.json();
            }
        }

        return {
            termin,
            zahnarzt,
            behandlungsart,
            praxis,
            patientId
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading termin for review:', err);
        throw error(500, 'Fehler beim Laden des Termins');
    }
};

export const actions = {
    createReview: async ({ request, params, locals }) => {
        const formData = await request.formData();
        const rating = parseInt(formData.get('rating'));
        const text = formData.get('text');
        const zahnarztId = formData.get('zahnarztId');
        const patientId = formData.get('patientId');

        if (!rating || rating < 1 || rating > 5) {
            return fail(400, { error: 'Ungültige Bewertung' });
        }

        if (!text || text.trim().length < 10) {
            return fail(400, { error: 'Bewertungstext muss mindestens 10 Zeichen lang sein' });
        }

        try {
            const response = await fetch(`${API_BASE_URL}/rezensionen`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${locals.jwt_token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    zahnarztId,
                    patientId,
                    bewertung: rating,
                    text: text.trim(),
                    datum: new Date().toISOString()
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Create review error:', errorText);
                return fail(response.status, { error: 'Bewertung konnte nicht erstellt werden' });
            }

            // Redirect to termin detail with success message
            throw redirect(303, `/termine/${params.id}?reviewSuccess=true`);
        } catch (error) {
            if (error?.status === 303) {
                throw error;
            }
            console.error('Error creating review:', error);
            return fail(500, { error: 'Fehler beim Erstellen der Bewertung' });
        }
    }
};
