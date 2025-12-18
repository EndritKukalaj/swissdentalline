import { env } from '$env/dynamic/private';
import { error, redirect } from '@sveltejs/kit';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ locals, params }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw error(401, 'Nicht autorisiert');
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    if (userRole !== 'Patient') {
        throw error(403, 'Nur Patienten können Rezensionen bearbeiten');
    }

    const reviewId = params.id;

    try {
        const jwt_token = locals.jwt_token;
        
        // Load review
        const reviewResponse = await fetch(
            `${API_BASE_URL}/rezensionen/${reviewId}`,
            {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            }
        );

        if (!reviewResponse.ok) {
            throw error(reviewResponse.status, 'Rezension nicht gefunden');
        }

        const review = await reviewResponse.json();
        
        // Load zahnarzt info
        const zahnarztResponse = await fetch(
            `${API_BASE_URL}/zahnaerzte/${review.zahnarztId || review.zahnarzt_id}`,
            {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        
        const zahnarzt = zahnarztResponse.ok ? await zahnarztResponse.json() : null;

        return {
            review,
            zahnarzt
        };
    } catch (err) {
        if (err.status) throw err;
        console.error('Error loading review:', err);
        throw error(500, 'Fehler beim Laden der Rezension');
    }
};

export const actions = {
    updateReview: async ({ request, locals, params }) => {
        if (!locals.isAuthenticated || !locals.user) {
            throw error(401, 'Nicht autorisiert');
        }

        const userRole = locals.user.user_roles?.[0] || 'Patient';
        if (userRole !== 'Patient') {
            throw error(403, 'Nur Patienten können Rezensionen bearbeiten');
        }

        const reviewId = params.id;
        const formData = await request.formData();
        const bewertung = parseInt(formData.get('bewertung'));
        const text = formData.get('text');

        if (!bewertung || bewertung < 1 || bewertung > 5) {
            return {
                success: false,
                error: 'Bitte wählen Sie eine Bewertung zwischen 1 und 5 Sternen'
            };
        }

        if (!text || text.trim().length < 10) {
            return {
                success: false,
                error: 'Der Rezensionstext muss mindestens 10 Zeichen lang sein'
            };
        }

        try {
            const jwt_token = locals.jwt_token;
            const auth0UserId = locals.user.sub;
            const patientId = auth0UserId.replace('auth0|', '');
            
            // Load original review to get zahnarztId
            const reviewResponse = await fetch(
                `${API_BASE_URL}/rezensionen/${reviewId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (!reviewResponse.ok) {
                throw error(reviewResponse.status, 'Rezension nicht gefunden');
            }

            const originalReview = await reviewResponse.json();
            
            const response = await fetch(
                `${API_BASE_URL}/rezensionen/${reviewId}`,
                {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        patientId,
                        zahnarztId: originalReview.zahnarztId || originalReview.zahnarzt_id,
                        bewertung,
                        text: text.trim(),
                        datum: new Date().toISOString()
                    })
                }
            );

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Update failed:', errorText);
                return {
                    success: false,
                    error: 'Fehler beim Aktualisieren der Rezension'
                };
            }

            throw redirect(303, '/rezensionen');
        } catch (err) {
            if (err.status === 303) throw err;
            console.error('Error updating review:', err);
            return {
                success: false,
                error: 'Fehler beim Aktualisieren der Rezension'
            };
        }
    }
};
