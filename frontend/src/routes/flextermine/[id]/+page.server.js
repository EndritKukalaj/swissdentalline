import { redirect, fail } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export async function load({ params, url, locals }) {
    // Check if user is authenticated
    if (!locals.user) {
        throw redirect(302, '/login');
    }
    
    // Check if user is a patient
    const userRole = locals.user.user_roles?.[0];
    if (userRole !== 'Patient') {
        throw redirect(302, '/');
    }
    
    const flexTerminId = params.id;
    const oldTerminId = url.searchParams.get('oldTerminId');
    
    if (!oldTerminId) {
        throw redirect(302, '/flextermine');
    }
    
    try {
        const patientId = locals.user.sub.replace('auth0|', '');
        
        // Fetch flex termin
        const flexTerminRes = await fetch(`${API_BASE_URL}/termine/${flexTerminId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!flexTerminRes.ok) {
            throw redirect(302, '/flextermine');
        }
        
        const flexTermin = await flexTerminRes.json();
        
        // Fetch old termin
        const oldTerminRes = await fetch(`${API_BASE_URL}/termine/${oldTerminId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!oldTerminRes.ok) {
            throw redirect(302, '/flextermine');
        }
        
        const oldTermin = await oldTerminRes.json();
        
        // Validate patient owns the old termin
        if (oldTermin.patientId !== patientId) {
            throw redirect(302, '/flextermine');
        }
        
        // Fetch behandlungsart
        const behandlungsartRes = await fetch(`${API_BASE_URL}/behandlungsarten/${flexTermin.behandlungsartId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const behandlungsart = behandlungsartRes.ok ? await behandlungsartRes.json() : null;
        
        // Fetch zahnarzt for flex termin
        const zahnarztRes = await fetch(`${API_BASE_URL}/zahnaerzte/${flexTermin.zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const flexZahnarzt = zahnarztRes.ok ? await zahnarztRes.json() : null;
        
        // Fetch zahnarzt for old termin
        const oldZahnarztRes = await fetch(`${API_BASE_URL}/zahnaerzte/${oldTermin.zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const oldZahnarzt = oldZahnarztRes.ok ? await oldZahnarztRes.json() : null;
        
        return {
            flexTermin,
            oldTermin,
            behandlungsart,
            flexZahnarzt,
            oldZahnarzt,
            patientId
        };
    } catch (error) {
        console.error('Error loading flex termin detail:', error);
        throw redirect(302, '/flextermine');
    }
}

export const actions = {
    confirmRebooking: async ({ params, url, request, locals }) => {
        const formData = await request.formData();
        const oldTerminId = formData.get('oldTerminId');
        const flexTerminId = params.id;
        const patientId = locals.user.sub.replace('auth0|', '');
        
        if (!oldTerminId || !flexTerminId) {
            return fail(400, { error: 'Termin IDs fehlen' });
        }
        
        try {
            const response = await fetch(
                `${API_BASE_URL}/termine/${oldTerminId}/umbuchen/${flexTerminId}?patientId=${patientId}`,
                {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${locals.jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Rebook error:', errorText);
                return fail(response.status, { error: 'Umbuchung fehlgeschlagen' });
            }
            
            const rebookedTermin = await response.json();
            
            // Success - redirect to termin detail page with success message
            throw redirect(303, `/termine/${rebookedTermin.id}?rebookSuccess=true`);
        } catch (error) {
            if (error?.status === 303) {
                throw error;
            }
            console.error('Error rebooking:', error);
            return fail(500, { error: 'Fehler bei der Umbuchung' });
        }
    }
};
