import { env } from '$env/dynamic/private';
import { error } from '@sveltejs/kit';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';
const API_PREFIX = API_BASE_URL.endsWith('/api')
    ? API_BASE_URL
    : API_BASE_URL.replace(/\/$/, '') + '/api';

export const load = async ({ locals, url }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    
    // Both Patient and Zahnarzt can access this page
    if (userRole !== 'Patient' && userRole !== 'Zahnarzt') {
        throw error(403, 'Zugriff verweigert');
    }

    try {
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userId = auth0UserId.replace('auth0|', '');
        
        const page = parseInt(url.searchParams.get('pageNumber') || '1');
        const size = parseInt(url.searchParams.get('pageSize') || '5');

        let rezensionenData;
        let rezensionenWithDetails;

        if (userRole === 'Patient') {
            // Fetch patient's reviews (page is 1-indexed from URL, API expects 0-indexed)
            const rezensionenResponse = await fetch(
                `${API_PREFIX}/rezensionen/patient/${userId}?page=${page - 1}&size=${size}`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!rezensionenResponse.ok) {
                throw error(rezensionenResponse.status, 'Fehler beim Laden der Rezensionen');
            }

            rezensionenData = await rezensionenResponse.json();
            
            // Fetch zahnarzt names for each review
            rezensionenWithDetails = await Promise.all(
                rezensionenData.content.map(async (rezension) => {
                    try {
                        const zahnarztResponse = await fetch(
                            `${API_PREFIX}/zahnaerzte/${rezension.zahnarztId || rezension.zahnarzt_id}`,
                            {
                                headers: {
                                    'Authorization': `Bearer ${jwt_token}`,
                                    'Content-Type': 'application/json'
                                }
                            }
                        );
                        
                        const zahnarzt = zahnarztResponse.ok ? await zahnarztResponse.json() : null;
                        
                        return {
                            ...rezension,
                            zahnarzt
                        };
                    } catch (err) {
                        console.error('Error fetching zahnarzt:', err);
                        return { ...rezension, zahnarzt: null };
                    }
                })
            );
        } else {
            // Zahnarzt: Fetch reviews for this dentist (page is 1-indexed from URL, API expects 0-indexed)
            const rezensionenResponse = await fetch(
                `${API_PREFIX}/rezensionen/zahnarzt/${userId}?page=${page - 1}&size=${size}`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!rezensionenResponse.ok) {
                throw error(rezensionenResponse.status, 'Fehler beim Laden der Rezensionen');
            }

            rezensionenData = await rezensionenResponse.json();
            
            // Fetch patient names for each review
            rezensionenWithDetails = await Promise.all(
                rezensionenData.content.map(async (rezension) => {
                    try {
                        const patientResponse = await fetch(
                            `${API_PREFIX}/patienten/${rezension.patientId || rezension.patient_id}`,
                            {
                                headers: {
                                    'Authorization': `Bearer ${jwt_token}`,
                                    'Content-Type': 'application/json'
                                }
                            }
                        );
                        
                        const patient = patientResponse.ok ? await patientResponse.json() : null;
                        
                        return {
                            ...rezension,
                            patient,
                            patientName: patient ? `${patient.name}` : 'Unbekannt'
                        };
                    } catch (err) {
                        console.error('Error fetching patient:', err);
                        return { ...rezension, patient: null, patientName: 'Unbekannt' };
                    }
                })
            );
        }

        return {
            rezensionen: rezensionenWithDetails,
            currentPage: page,
            nrOfPages: rezensionenData.totalPages,
            userRole,
            userId
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading reviews:', err);
        throw error(500, 'Fehler beim Laden der Rezensionen');
    }
};

export const actions = {
    deleteReview: async ({ request, locals, url }) => {
        if (!locals.isAuthenticated || !locals.user) {
            throw error(401, 'Nicht autorisiert');
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Patient') {
            throw error(403, 'Nur Patienten können ihre eigenen Rezensionen löschen');
        }

        const reviewId = url.searchParams.get('id');
        if (!reviewId) {
            throw error(400, 'Rezensions-ID fehlt');
        }

        try {
            const jwt_token = locals.jwt_token;
            
            const response = await fetch(
                `${API_PREFIX}/rezensionen/${reviewId}`,
                {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!response.ok) {
                throw error(response.status, 'Fehler beim Löschen der Rezension');
            }

            return { success: true, message: 'Rezension wurde erfolgreich gelöscht' };
        } catch (err) {
            console.error('Error deleting review:', err);
            throw error(500, 'Fehler beim Löschen der Rezension');
        }
    }
};
