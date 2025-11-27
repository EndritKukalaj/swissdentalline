import { env } from '$env/dynamic/private';
import { error } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ params, locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    try {
        const jwt_token = locals.jwt_token;
        const terminId = params.id;
        const auth0UserId = locals.user.sub;
        const patientId = auth0UserId.replace('auth0|', '');

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

        // Verify that this termin belongs to the current patient
        const terminPatientId = termin.patientId || termin.patient_id;
        if (terminPatientId !== patientId) {
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

        return {
            termin,
            behandlungsart,
            zahnarzt,
            adresse
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading termin details:', err);
        throw error(500, 'Fehler beim Laden der Termindetails');
    }
};
