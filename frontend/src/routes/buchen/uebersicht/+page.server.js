import { redirect, fail } from '@sveltejs/kit';

const API_BASE_URL = 'http://localhost:8080/api';

export async function load({ url, locals }) {
    // Check if user is authenticated
    if (!locals.user) {
        throw redirect(302, '/login');
    }
    
    // Check if user is a patient
    const userRole = locals.user.user_roles?.[0];
    if (userRole !== 'Patient') {
        throw redirect(302, '/');
    }
    
    const terminId = url.searchParams.get('terminId');
    const warteliste = url.searchParams.get('warteliste') === 'true';
    
    if (!terminId) {
        throw redirect(302, '/buchen/behandlung');
    }
    
    try {
        // Fetch termin details
        const terminRes = await fetch(`${API_BASE_URL}/termine/${terminId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!terminRes.ok) {
            throw redirect(302, '/buchen/behandlung');
        }
        
        const termin = await terminRes.json();
        
        // Fetch zahnarzt details
        const zahnarztRes = await fetch(`${API_BASE_URL}/zahnaerzte/${termin.zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const zahnarzt = zahnarztRes.ok ? await zahnarztRes.json() : null;
        
        // Fetch behandlungsart details
        const behandlungRes = await fetch(`${API_BASE_URL}/behandlungsarten/${termin.behandlungsartId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const behandlungsart = behandlungRes.ok ? await behandlungRes.json() : null;
        
        // Fetch praxis details if zahnarzt has praxisAdresseId
        let praxis = null;
        if (zahnarzt?.praxisAdresseId) {
            const praxisRes = await fetch(`${API_BASE_URL}/adressen/${zahnarzt.praxisAdresseId}`, {
                headers: {
                    'Authorization': `Bearer ${locals.jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (praxisRes.ok) {
                praxis = await praxisRes.json();
            }
        }
        
        return {
            termin,
            zahnarzt,
            behandlungsart,
            praxis,
            warteliste
        };
    } catch (error) {
        console.error('Error loading overview page:', error);
        throw redirect(302, '/buchen/behandlung');
    }
}

export const actions = {
    bookAppointment: async ({ request, locals }) => {
        const formData = await request.formData();
        const terminId = formData.get('terminId');
        const warteliste = formData.get('warteliste') === 'true';
        
        if (!terminId) {
            return fail(400, { error: 'Termin ID fehlt' });
        }
        
        try {
            // Get patient ID from user
            const patientId = locals.user.sub.replace('auth0|', '');
            
            // Build URL with query parameters
            const url = new URL(`${API_BASE_URL}/termine/${terminId}/buchen`);
            url.searchParams.append('patientId', patientId);
            url.searchParams.append('isFlex', warteliste);
            
            // Book appointment using the dedicated endpoint with query parameters
            const bookingRes = await fetch(url.toString(), {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${locals.jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (!bookingRes.ok) {
                const errorText = await bookingRes.text();
                console.error('Booking failed:', errorText);
                return fail(500, { error: 'Termin konnte nicht gebucht werden' });
            }
            
            return { success: true, terminId };
        } catch (error) {
            console.error('Error booking appointment:', error);
            return fail(500, { error: 'Ein Fehler ist aufgetreten' });
        }
    }
};
