import { env } from '$env/dynamic/private';
import { error, fail, redirect } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ params, locals, url }) => {
    const rebookSuccess = url.searchParams.get('rebookSuccess') === 'true';
    const reviewSuccess = url.searchParams.get('reviewSuccess') === 'true';
    const bookingSuccess = url.searchParams.get('bookingSuccess') === 'true';
    const slotCreated = url.searchParams.get('slotCreated') === 'true';
    const slotUpdated = url.searchParams.get('slotUpdated') === 'true';

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
            userRole,
            rebookSuccess,
            reviewSuccess,
            bookingSuccess,
            slotCreated,
            slotUpdated
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
        if (userRole !== 'Patient' && userRole !== 'Zahnarzt') {
            return fail(403, { error: 'Nicht autorisiert, Termine zu stornieren' });
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

            // Verify ownership based on role
            if (userRole === 'Patient') {
                const terminPatientId = termin.patientId || termin.patient_id;
                const normalizedTerminPatientId = terminPatientId?.replace('auth0|', '');
                const normalizedUserId = userId;

                if (normalizedTerminPatientId !== normalizedUserId) {
                    return fail(403, { error: 'Sie können nur Ihre eigenen Termine stornieren' });
                }
            } else if (userRole === 'Zahnarzt') {
                const terminZahnarztId = termin.zahnarztId || termin.zahnarzt_id;
                const normalizedTerminZahnarztId = terminZahnarztId?.replace('auth0|', '');
                const normalizedUserId = userId;

                if (normalizedTerminZahnarztId !== normalizedUserId) {
                    return fail(403, { error: 'Sie können nur Ihre eigenen Termine stornieren' });
                }
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
                return fail(cancelResponse.status, { error: 'Termin konnte nicht storniert werden', action: 'cancel' });
            }

            return { success: true, action: 'cancel' };
        } catch (err) {
            console.error('Error canceling termin:', err);
            return fail(500, { error: 'Ein Fehler ist aufgetreten', action: 'cancel' });
        }
    },

    completeTermin: async ({ params, locals }) => {
        if (!locals.isAuthenticated || !locals.user) {
            return fail(401, { error: 'Nicht autorisiert', action: 'complete' });
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Zahnarzt') {
            return fail(403, { error: 'Nur Zahnärzte können Termine abschliessen', action: 'complete' });
        }

        const terminId = params.id;
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');

        try {
            // Verify ownership before completing
            const terminResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!terminResponse.ok) {
                return fail(404, { error: 'Termin nicht gefunden', action: 'complete' });
            }

            const termin = await terminResponse.json();
            const terminZahnarztId = termin.zahnarztId || termin.zahnarzt_id;

            // Normalize both IDs for comparison (remove auth0| prefix if present)
            const normalizedTerminZahnarztId = terminZahnarztId?.replace('auth0|', '');
            const normalizedUserId = userId;

            if (normalizedTerminZahnarztId !== normalizedUserId) {
                return fail(403, { error: 'Sie können nur Ihre eigenen Termine abschliessen', action: 'complete' });
            }

            // Only allow completing GEBUCHT appointments
            if (termin.status !== 'GEBUCHT') {
                return fail(400, { error: 'Nur gebuchte Termine können abgeschlossen werden', action: 'complete' });
            }

            // Complete the appointment
            const completeResponse = await fetch(`${API_BASE_URL}/termine/${terminId}/abschliessen`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!completeResponse.ok) {
                const errorText = await completeResponse.text();
                console.error('Complete response error:', errorText);
                return fail(completeResponse.status, { error: 'Termin konnte nicht abgeschlossen werden', action: 'complete' });
            }

            return { success: true, action: 'complete' };
        } catch (err) {
            console.error('Error completing termin:', err);
            return fail(500, { error: 'Ein Fehler ist aufgetreten', action: 'complete' });
        }
    },

    releaseToFlex: async ({ params, locals }) => {
        if (!locals.isAuthenticated || !locals.user) {
            return fail(401, { error: 'Nicht autorisiert', action: 'releaseFlex' });
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Zahnarzt') {
            return fail(403, { error: 'Nur Zahnärzte können Termine als Flex freigeben', action: 'releaseFlex' });
        }

        const terminId = params.id;
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');

        try {
            // Verify ownership before releasing
            const terminResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!terminResponse.ok) {
                return fail(404, { error: 'Termin nicht gefunden', action: 'releaseFlex' });
            }

            const termin = await terminResponse.json();
            const terminZahnarztId = termin.zahnarztId || termin.zahnarzt_id;

            // Normalize both IDs for comparison (remove auth0| prefix if present)
            const normalizedTerminZahnarztId = terminZahnarztId?.replace('auth0|', '');
            const normalizedUserId = userId;

            if (normalizedTerminZahnarztId !== normalizedUserId) {
                return fail(403, { error: 'Sie können nur Ihre eigenen Termine freigeben', action: 'releaseFlex' });
            }

            // Only allow releasing ABGESAGT appointments
            if (termin.status !== 'ABGESAGT') {
                return fail(400, { error: 'Nur abgesagte Termine können als Flex freigegeben werden', action: 'releaseFlex' });
            }

            // Release the appointment to FLEX
            const releaseResponse = await fetch(`${API_BASE_URL}/termine/${terminId}/freigeben`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!releaseResponse.ok) {
                const errorText = await releaseResponse.text();
                console.error('Release flex response error:', errorText);
                return fail(releaseResponse.status, { error: 'Termin konnte nicht als Flex freigegeben werden', action: 'releaseFlex' });
            }

            return { success: true, action: 'releaseFlex' };
        } catch (err) {
            console.error('Error releasing termin to flex:', err);
            return fail(500, { error: 'Ein Fehler ist aufgetreten', action: 'releaseFlex' });
        }
    },

    deleteSlot: async ({ params, locals }) => {
        if (!locals.isAuthenticated || !locals.user) {
            return fail(401, { error: 'Nicht autorisiert', action: 'delete' });
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Zahnarzt') {
            return fail(403, { error: 'Nur Zahnärzte können Slots löschen', action: 'delete' });
        }

        const terminId = params.id;
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');

        try {
            // Get current termin to verify ownership and status
            const terminResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!terminResponse.ok) {
                return fail(404, { error: 'Termin nicht gefunden', action: 'delete' });
            }

            const termin = await terminResponse.json();

            // Verify termin is FREI or FLEX
            if (termin.status !== 'FREI' && termin.status !== 'FLEX') {
                return fail(400, { error: 'Nur freie oder Flex Slots können gelöscht werden', action: 'delete' });
            }

            // Verify ownership (Zahnarzt)
            const terminZahnarztId = termin.zahnarztId || termin.zahnarzt_id;
            const normalizedTerminZahnarztId = terminZahnarztId?.replace('auth0|', '');

            if (normalizedTerminZahnarztId !== userId) {
                return fail(403, { error: 'Sie sind nicht berechtigt, diesen Slot zu löschen', action: 'delete' });
            }

            // Delete the slot
            const deleteResponse = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!deleteResponse.ok) {
                return fail(500, { error: 'Fehler beim Löschen des Slots', action: 'delete' });
            }

            throw redirect(303, '/termine');
        } catch (err) {
            if (err.status === 303) throw err;
            console.error('Error deleting slot:', err);
            return fail(500, { error: 'Ein Fehler ist aufgetreten', action: 'delete' });
        }
    }
}
