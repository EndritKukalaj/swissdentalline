import { env } from '$env/dynamic/private';
import { redirect } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export async function load({ locals, url, fetch }) {
    if (!locals.isAuthenticated || !locals.user) {
        throw redirect(303, '/login');
    }
    
    const userRole = locals.user.user_roles?.[0] || 'Patient';
    
    // Only patients can book appointments
    if (userRole !== 'Patient') {
        throw redirect(303, '/');
    }
    
    const behandlungsartId = url.searchParams.get('behandlungsartId');
    
    if (!behandlungsartId) {
        throw redirect(303, '/buchen/behandlung');
    }
    
    try {
        const jwt_token = locals.jwt_token;
        
        // Fetch behandlungsart details
        const behandlungsartRes = await fetch(
            `${API_BASE_URL}/behandlungsarten/${behandlungsartId}`,
            {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        
        if (!behandlungsartRes.ok) {
            throw redirect(303, '/buchen/behandlung');
        }
        
        const behandlungsart = await behandlungsartRes.json();
        
        // Fetch all free termine
        const termineRes = await fetch(`${API_BASE_URL}/termine`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!termineRes.ok) {
            throw new Error('Failed to fetch termine');
        }
        
        const alleTermine = await termineRes.json();
        
        // Filter for free termine matching the behandlungsart
        const freieTermine = alleTermine.filter(termin => {
            const statusMatch = termin.status === 'FREI' || termin.status === 'VERFUEGBAR';
            // Check both behandlungsartId (string from API) and behandlungsart.id (nested object)
            const terminBehandlungId = termin.behandlungsartId || termin.behandlungsart?.id;
            const behandlungMatch = terminBehandlungId === behandlungsartId || 
                                   terminBehandlungId === parseInt(behandlungsartId);
            
            return statusMatch && behandlungMatch;
        });
        
        // Load Zahnarzt details for each termin
        const termineWithZahnarzt = await Promise.all(
            freieTermine.map(async (termin) => {
                if (termin.zahnarztId) {
                    try {
                        const zahnarztRes = await fetch(
                            `${API_BASE_URL}/zahnaerzte/${termin.zahnarztId}`,
                            {
                                headers: {
                                    'Authorization': `Bearer ${jwt_token}`,
                                    'Content-Type': 'application/json'
                                }
                            }
                        );
                        
                        if (zahnarztRes.ok) {
                            const zahnarzt = await zahnarztRes.json();
                            return { ...termin, zahnarzt };
                        }
                    } catch (error) {
                        console.error('Failed to load zahnarzt:', error);
                    }
                }
                return termin;
            })
        );
        
        // Sort by date and time
        termineWithZahnarzt.sort((a, b) => {
            const dateCompare = new Date(a.datum) - new Date(b.datum);
            if (dateCompare !== 0) return dateCompare;
            return (a.startZeit || '').localeCompare(b.startZeit || '');
        });
        
        return {
            behandlungsart,
            termine: termineWithZahnarzt,
            userRole
        };
    } catch (error) {
        console.error('Error loading termine:', error);
        throw redirect(303, '/buchen/behandlung');
    }
}
