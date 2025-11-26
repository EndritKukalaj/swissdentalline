import { env } from '$env/dynamic/private';

const API_BASE_URL = env.BACKEND_URL || 'http://localhost:8080/api';

export const load = async ({ locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        return {
            termine: [],
            nextTermin: null,
            stats: {
                geplanteTermine: 0,
                offeneWartelisten: 0
            }
        };
    }

    try {
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        
        // Extract ID without 'auth0|' prefix
        const patientId = auth0UserId.replace('auth0|', '');
        
        console.log('Auth0 User ID:', auth0UserId);
        console.log('Patient ID (without prefix):', patientId);
        
        // 1. Load patient from database using patient ID (without auth0| prefix)
        const patientResponse = await fetch(`${API_BASE_URL}/patienten/${patientId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!patientResponse.ok) {
            console.error('Patient not found for Patient ID:', patientId);
            throw new Error(`Patient not found: ${patientResponse.status}`);
        }
        
        const patient = await patientResponse.json();
        console.log('Loaded patient:', patient);
        
        // 2. Fetch all termine
        const termineResponse = await fetch(`${API_BASE_URL}/termine`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!termineResponse.ok) {
            throw new Error(`Failed to fetch termine: ${termineResponse.status}`);
        }

        // 3. Filter termine by patient ID
        const allTermine = await termineResponse.json();
        const patientTermine = allTermine.filter(termin => termin.patientId === patientId);
        console.log('Fetched and filtered termine count:', patientTermine.length);

        // Enrich termine with behandlungsart names
        const enrichedTermine = await Promise.all(
            patientTermine.map(async (termin) => {
                try {
                    const behandlungsartResponse = await fetch(
                        `${API_BASE_URL}/behandlungsarten/${termin.behandlungsartId}`,
                        {
                            headers: {
                                'Authorization': `Bearer ${jwt_token}`,
                                'Content-Type': 'application/json'
                            }
                        }
                    );
                    
                    if (behandlungsartResponse.ok) {
                        const behandlungsart = await behandlungsartResponse.json();
                        return {
                            ...termin,
                            behandlungsartName: behandlungsart.name || 'Unbekannt'
                        };
                    }
                } catch (error) {
                    console.error('Error fetching behandlungsart:', error);
                }
                
                return {
                    ...termin,
                    behandlungsartName: 'Unbekannt'
                };
            })
        );
        
        // Sort by status (GEBUCHT first, then ABGESCHLOSSEN) and then by date
        const sortedTermine = enrichedTermine.sort((a, b) => {
            // Status priority: GEBUCHT = 1, ABGESAGT = 2, ABGESCHLOSSEN = 3, others = 4
            const statusPriority = {
                'GEBUCHT': 1,
                'ABGESAGT': 2,
                'ABGESCHLOSSEN': 3
            };
            const priorityA = statusPriority[a.status] || 4;
            const priorityB = statusPriority[b.status] || 4;
            
            // First sort by status priority
            if (priorityA !== priorityB) {
                return priorityA - priorityB;
            }
            
            // Then sort by date
            return new Date(a.datum) - new Date(b.datum);
        });
        
        // Get upcoming termine (future only)
        const now = new Date();
        const upcomingTermine = sortedTermine.filter(t => 
            new Date(t.datum) >= now && t.status === 'GEBUCHT'
        );
        
        // Get next termin
        const nextTermin = upcomingTermine.length > 0 ? upcomingTermine[0] : null;
        
        // Calculate stats
        const geplanteTermine = upcomingTermine.length;
        const offeneWartelisten = sortedTermine.filter(t => 
            t.wartelisteAktiv && t.status === 'GEBUCHT'
        ).length;
        console.log('Dashboard stats - Geplante Termine:', geplanteTermine, 'Offene Wartelisten:', offeneWartelisten);
        
        return {
            termine: sortedTermine,
            nextTermin,
            stats: {
                geplanteTermine,
                offeneWartelisten
            }
        };
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        return {
            termine: [],
            nextTermin: null,
            stats: {
                geplanteTermine: 0,
                offeneWartelisten: 0
            }
        };
    }
};
