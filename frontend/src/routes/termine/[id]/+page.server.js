import { env } from '$env/dynamic/private';
import { error, fail, redirect } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ params, locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    try {
        const jwt_token = locals.jwt_token;
        const terminId = params.id;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');
        const userRole = locals.user.user_roles?.[0] || 'Patient';

        // 1. Fetch termin details
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

        // Verify access rights based on role
        const terminPatientId = termin.patientId || termin.patient_id;
        const terminZahnarztId = termin.zahnarztId || termin.zahnarzt_id;
        
        if (userRole === 'Patient' && terminPatientId !== userId) {
            throw error(403, 'Zugriff verweigert');
        }
        
        if (userRole === 'Zahnarzt' && terminZahnarztId !== userId) {
            throw error(403, 'Zugriff verweigert');
        }

        // 2. Fetch behandlungsart details
        let behandlungsart = null;
        const behandlungsartId = termin.behandlungsartId || termin.behandlungsart_id;
        if (behandlungsartId) {
            try {
                const behandlungsartResponse = await fetch(
                    `${API_BASE_URL}/behandlungsarten/${behandlungsartId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );
                if (behandlungsartResponse.ok) {
                    behandlungsart = await behandlungsartResponse.json();
                }
            } catch (err) {
                console.error('Error fetching behandlungsart:', err);
            }
        }

        // 3. Fetch zahnarzt details
        let zahnarzt = null;
        const zahnarztId = termin.zahnarztId || termin.zahnarzt_id;
        if (zahnarztId) {
            try {
                const zahnarztResponse = await fetch(
                    `${API_BASE_URL}/zahnaerzte/${zahnarztId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );
                if (zahnarztResponse.ok) {
                    zahnarzt = await zahnarztResponse.json();
                }
            } catch (err) {
                console.error('Error fetching zahnarzt:', err);
            }
        }

        // 4. Fetch adresse (praxis) if zahnarzt has adresseId
        let adresse = null;
        const adresseId = zahnarzt?.adresseId || zahnarzt?.adresse_id || zahnarzt?.praxisAdresseId;
        if (adresseId) {
            try {
                const adresseResponse = await fetch(
                    `${API_BASE_URL}/adressen/${adresseId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );
                if (adresseResponse.ok) {
                    adresse = await adresseResponse.json();
                }
            } catch (err) {
                console.error('Error fetching adresse:', err);
            }
        }

        // 5. Fetch patient details if user is Zahnarzt
        let patient = null;
        if (userRole === 'Zahnarzt' && terminPatientId) {
            try {
                const patientResponse = await fetch(
                    `${API_BASE_URL}/patienten/${terminPatientId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );
                if (patientResponse.ok) {
                    patient = await patientResponse.json();
                }
            } catch (err) {
                console.error('Error fetching patient:', err);
            }
        }

        return {
            termin,
            behandlungsart,
            zahnarzt,
            adresse,
            patient,
            userRole
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading termin details:', err);
        throw error(500, 'Fehler beim Laden der Termindetails');
    }
};

export const actions = {
    cancelTermin: async ({ params, locals }) => {
        if (!locals.isAuthenticated || !locals.user) {
            return fail(401, { error: 'Nicht autorisiert' });
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Patient') {
            return fail(403, { error: 'Nur Patienten können Termine stornieren' });
        }

        const terminId = params.id;
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');

        try {
            // Verify ownership before canceling
            const terminResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!terminResponse.ok) {
                return fail(404, { error: 'Termin nicht gefunden' });
            }

            const termin = await terminResponse.json();
            const terminPatientId = termin.patientId || termin.patient_id;
            
            // Normalize both IDs for comparison (remove auth0| prefix if present)
            const normalizedTerminPatientId = terminPatientId?.replace('auth0|', '');
            const normalizedUserId = userId;

            if (normalizedTerminPatientId !== normalizedUserId) {
                return fail(403, { error: 'Sie können nur Ihre eigenen Termine stornieren' });
            }

            // Cancel the appointment
            const cancelResponse = await fetch(`${API_BASE_URL}/termine/${terminId}/abbrechen`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!cancelResponse.ok) {
                return fail(cancelResponse.status, { error: 'Termin konnte nicht storniert werden' });
            }

            return { success: true };
        } catch (err) {
            console.error('Error canceling termin:', err);
            return fail(500, { error: 'Ein Fehler ist aufgetreten' });
        }
    }
};
