import { redirect } from '@sveltejs/kit';

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
        
        // Fetch behandlungsart details
        const behandlungRes = await fetch(`${API_BASE_URL}/behandlungsarten/${termin.behandlungsartId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const behandlungsart = behandlungRes.ok ? await behandlungRes.json() : null;
        
        return {
            termin,
            behandlungsart
        };
    } catch (error) {
        console.error('Error loading warteliste page:', error);
        throw redirect(302, '/buchen/behandlung');
    }
}
