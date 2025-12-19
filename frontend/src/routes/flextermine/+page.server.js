import { redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';
const API_PREFIX = API_BASE_URL.endsWith('/api')
    ? API_BASE_URL
    : API_BASE_URL.replace(/\/$/, '') + '/api';

export async function load({ locals }) {
    // Check if user is authenticated
    if (!locals.user) {
        throw redirect(302, '/login');
    }
    
    // Check if user is a patient
    const userRole = locals.user.user_roles?.[0];
    if (userRole !== 'Patient') {
        throw redirect(302, '/');
    }
    
    try {
        const patientId = locals.user.sub.replace('auth0|', '');
        
        // Fetch relevant flex termine for patient
        const flexTermineRes = await fetch(`${API_PREFIX}/termine/patient/${patientId}/flex`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!flexTermineRes.ok) {
            console.error('Failed to fetch flex termine');
            return {
                flexTermine: [],
                patientTermine: []
            };
        }
        
        const flexTermine = await flexTermineRes.json();
        
        // Fetch patient's booked appointments with waitlist active
        const allTermineRes = await fetch(`${API_PREFIX}/termine`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!allTermineRes.ok) {
            console.error('Failed to fetch patient termine');
            return {
                flexTermine,
                patientTermine: []
            };
        }
        
        const allTermine = await allTermineRes.json();
        
        // Filter patient's appointments with waitlist active
        const patientTermine = allTermine.filter(t => 
            t.patientId === patientId && 
            t.status === 'GEBUCHT' && 
            t.wartelisteAktiv === true
        );
        
        // Fetch behandlungsarten for all termine
        const behandlungsartIds = [...new Set([
            ...flexTermine.map(t => t.behandlungsartId),
            ...patientTermine.map(t => t.behandlungsartId)
        ])];
        
        const behandlungsarten = {};
        for (const id of behandlungsartIds) {
            const res = await fetch(`${API_PREFIX}/behandlungsarten/${id}`, {
                headers: {
                    'Authorization': `Bearer ${locals.jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (res.ok) {
                const behandlungsart = await res.json();
                behandlungsarten[id] = behandlungsart;
            }
        }
        
        // Fetch zahnarzt info for flex termine
        const zahnarztIds = [...new Set(flexTermine.map(t => t.zahnarztId))];
        const zahnaerzte = {};
        for (const id of zahnarztIds) {
            const res = await fetch(`${API_PREFIX}/zahnaerzte/${id}`, {
                headers: {
                    'Authorization': `Bearer ${locals.jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            if (res.ok) {
                const zahnarzt = await res.json();
                zahnaerzte[id] = zahnarzt;
            }
        }
        
        return {
            flexTermine,
            patientTermine,
            behandlungsarten,
            zahnaerzte,
            patientId
        };
    } catch (error) {
        console.error('Error loading flex termine page:', error);
        return {
            flexTermine: [],
            patientTermine: [],
            behandlungsarten: {},
            zahnaerzte: {},
            patientId: locals.user.sub.replace('auth0|', '')
        };
    }
}
