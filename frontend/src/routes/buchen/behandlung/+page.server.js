import { env } from '$env/dynamic/private';
import { redirect } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw redirect(302, '/login');
    }
    
    const userRole = locals.user.user_roles?.[0] || 'Patient';
    
    // Only patients can book appointments
    if (userRole !== 'Patient') {
        throw redirect(302, '/');
    }
    
    try {
        const jwt_token = locals.jwt_token;
        
        // Fetch all available Behandlungsarten
        const response = await fetch(`${API_BASE_URL}/behandlungsarten`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`Failed to fetch behandlungsarten: ${response.status}`);
        }
        
        const behandlungsarten = await response.json();
        
        return {
            behandlungsarten,
            userRole
        };
    } catch (error) {
        console.error('Error loading behandlungsarten:', error);
        return {
            behandlungsarten: [],
            userRole
        };
    }
};
