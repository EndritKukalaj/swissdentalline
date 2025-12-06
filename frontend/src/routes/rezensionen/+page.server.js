import { env } from '$env/dynamic/private';
import { error } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ locals, url }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    if (userRole !== 'Patient') {
        throw error(403, 'Nur Patienten haben Zugriff auf diese Seite');
    }

    try {
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const patientId = auth0UserId.replace('auth0|', '');
        
        const page = parseInt(url.searchParams.get('page') || '0');
        const size = parseInt(url.searchParams.get('size') || '10');

        // Fetch patient's reviews
        const rezensionenResponse = await fetch(
            `${API_BASE_URL}/rezensionen/patient/${patientId}?page=${page}&size=${size}`,
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

        const rezensionenData = await rezensionenResponse.json();
        
        // Fetch zahnarzt names for each review
        const rezensionenWithDetails = await Promise.all(
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

        return {
            rezensionen: rezensionenWithDetails,
            pagination: {
                currentPage: rezensionenData.number,
                totalPages: rezensionenData.totalPages,
                totalElements: rezensionenData.totalElements,
                pageSize: rezensionenData.size
            },
            patientId
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading reviews:', err);
        throw error(500, 'Fehler beim Laden der Rezensionen');
    }
};
