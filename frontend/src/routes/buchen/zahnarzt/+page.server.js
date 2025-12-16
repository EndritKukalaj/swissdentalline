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
        
        // Fetch zahnarzt details
        const zahnarztRes = await fetch(`${API_BASE_URL}/zahnaerzte/${termin.zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!zahnarztRes.ok) {
            throw redirect(302, '/buchen/behandlung');
        }
        
        const zahnarzt = await zahnarztRes.json();
        
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
        if (zahnarzt.praxisAdresseId) {
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
        
        // Fetch rezensionen for this zahnarzt (only approved)
        let rezensionen = [];
        let gesamtBewertung = null;
        let reviewPagination = null;
        
        const reviewPage = parseInt(url.searchParams.get('reviewPage') || '1');
        const reviewSize = 3;
        
        try {
            const rezensionenRes = await fetch(
                `${API_BASE_URL}/rezensionen/zahnarzt/${zahnarzt.id}?page=${reviewPage - 1}&size=${reviewSize}&approved=true`,
                {
                    headers: {
                        'Authorization': `Bearer ${locals.jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            if (rezensionenRes.ok) {
                const rezensionenData = await rezensionenRes.json();
                
                reviewPagination = {
                    currentPage: rezensionenData.number,
                    totalPages: rezensionenData.totalPages,
                    totalElements: rezensionenData.totalElements,
                    size: rezensionenData.size
                };
                
                // Fetch patient names for each review
                rezensionen = await Promise.all(
                    rezensionenData.content.map(async (review) => {
                        try {
                            const patientRes = await fetch(
                                `${API_BASE_URL}/patienten/${review.patientId}`,
                                {
                                    headers: {
                                        'Authorization': `Bearer ${locals.jwt_token}`,
                                        'Content-Type': 'application/json'
                                    }
                                }
                            );
                            
                            if (patientRes.ok) {
                                const patient = await patientRes.json();
                                return {
                                    ...review,
                                    patientName: `${patient.name}`
                                };
                            }
                        } catch (err) {
                            console.error('Error fetching patient:', err);
                        }
                        
                        return {
                            ...review,
                            patientName: 'Patient'
                        };
                    })
                );
            }
            
            // Fetch gesamtbewertung
            const bewertungRes = await fetch(
                `${API_BASE_URL}/rezensionen/zahnarzt/${zahnarzt.id}/bewertung`,
                {
                    headers: {
                        'Authorization': `Bearer ${locals.jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            if (bewertungRes.ok) {
                gesamtBewertung = await bewertungRes.json();
            }
        } catch (err) {
            console.error('Error fetching reviews:', err);
        }
        
        return {
            termin,
            zahnarzt,
            behandlungsart,
            praxis,
            rezensionen,
            gesamtBewertung,
            reviewPagination
        };
    } catch (error) {
        console.error('Error loading zahnarzt details:', error);
        throw redirect(302, '/buchen/behandlung');
    }
}
