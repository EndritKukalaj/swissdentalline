import { env } from '$env/dynamic/private';
import { error } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

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
        
        const page = parseInt(url.searchParams.get('page') || '0');
        const size = parseInt(url.searchParams.get('size') || '10');

        let rezensionenData;
        let rezensionenWithDetails;

        if (userRole === 'Patient') {
            // Fetch patient's reviews
            const rezensionenResponse = await fetch(
                `${API_BASE_URL}/rezensionen/patient/${userId}?page=${page}&size=${size}`,
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
                            `${API_BASE_URL}/zahnaerzte/${rezension.zahnarztId || rezension.zahnarzt_id}`,
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
            // Zahnarzt: Fetch reviews for this dentist
            const rezensionenResponse = await fetch(
                `${API_BASE_URL}/rezensionen/zahnarzt/${userId}?page=${page}&size=${size}`,
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
                            `${API_BASE_URL}/patienten/${rezension.patientId || rezension.patient_id}`,
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
            pagination: {
                currentPage: rezensionenData.number,
                totalPages: rezensionenData.totalPages,
                totalElements: rezensionenData.totalElements,
                pageSize: rezensionenData.size
            },
            userRole,
            userId
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading reviews:', err);
        throw error(500, 'Fehler beim Laden der Rezensionen');
    }
};
