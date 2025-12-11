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
            },
            userRole: null
        };
    }

    try {
        const jwt_token = locals.jwt_token;
        const auth0UserId = locals.user.sub;
        const userRole = locals.user.user_roles?.[0] || 'Patient';
        
        // Extract ID without 'auth0|' prefix
        const userId = auth0UserId.replace('auth0|', '');
        
        console.log('Auth0 User ID:', auth0UserId);
        console.log('User ID (without prefix):', userId);
        console.log('User Role:', userRole);
        
        // Handle Zahnarzt role
        if (userRole === 'Zahnarzt') {
            return await loadZahnarztDashboard(userId, jwt_token);
        }
        
        // Handle Patient role (existing code)
        return await loadPatientDashboard(userId, jwt_token);
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        return {
            termine: [],
            nextTermin: null,
            stats: {
                geplanteTermine: 0,
                offeneWartelisten: 0
            },
            userRole: null
        };
    }
};

async function loadPatientDashboard(patientId, jwt_token) {
        
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
        
        // Get next termin and enrich with zahnarzt and praxis data
        let nextTermin = upcomingTermine.length > 0 ? upcomingTermine[0] : null;
        
        if (nextTermin) {
            try {
                // Fetch zahnarzt data
                const zahnarztId = nextTermin.zahnarztId || nextTermin.zahnarzt_id;
                if (zahnarztId) {
                    const zahnarztResponse = await fetch(`${API_BASE_URL}/zahnaerzte/${zahnarztId}`, {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                    if (zahnarztResponse.ok) {
                        nextTermin.zahnarzt = await zahnarztResponse.json();
                    }
                }
                
                // Fetch praxis adresse data
                const adresseId = nextTermin.praxisAdresseId;
                if (adresseId && nextTermin.zahnarzt) {
                    const adresseResponse = await fetch(`${API_BASE_URL}/adressen/${adresseId}`, {
                        headers: {
                            'Authorization': `Bearer ${jwt_token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                    if (adresseResponse.ok) {
                        nextTermin.adresse = await adresseResponse.json();
                    }
                }
            } catch (error) {
                console.error('Error enriching next termin:', error);
                // Continue without enriched data
            }
        }
        
        // Calculate stats
        const geplanteTermine = upcomingTermine.length;
        const offeneWartelisten = sortedTermine.filter(t => 
            t.wartelisteAktiv && t.status === 'GEBUCHT'
        ).length;
        
        // Count completed termine for reviews
        const abgeschlosseneTermine = sortedTermine.filter(t => t.status === 'ABGESCHLOSSEN').length;
        
        // Fetch flex termine count for this patient
        let verfuegbareFlexTermine = 0;
        try {
            const flexTermineResponse = await fetch(
                `${API_BASE_URL}/termine/patient/${patientId}/flex`,
                {
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            if (flexTermineResponse.ok) {
                const flexTermine = await flexTermineResponse.json();
                verfuegbareFlexTermine = flexTermine.length;
            }
        } catch (error) {
            console.error('Error loading flex termine count:', error);
        }
        
        console.log('Dashboard stats - Geplante Termine:', geplanteTermine, 'Offene Wartelisten:', offeneWartelisten, 'Flex-Termine:', verfuegbareFlexTermine, 'Abgeschlossene Termine:', abgeschlosseneTermine);
        
        return {
            termine: sortedTermine,
            nextTermin,
            stats: {
                geplanteTermine,
                offeneWartelisten,
                verfuegbareFlexTermine,
                abgeschlosseneTermine
            },
            userRole: 'Patient'
        };
}

async function loadZahnarztDashboard(zahnarztId, jwt_token) {
    try {
        // 1. Load zahnarzt from database
        const zahnarztResponse = await fetch(`${API_BASE_URL}/zahnaerzte/${zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!zahnarztResponse.ok) {
            console.error('Zahnarzt not found for ID:', zahnarztId);
            throw new Error(`Zahnarzt not found: ${zahnarztResponse.status}`);
        }
        
        const zahnarzt = await zahnarztResponse.json();
        console.log('Loaded zahnarzt:', zahnarzt);
        
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

        // 3. Filter termine by zahnarzt ID
        const allTermine = await termineResponse.json();
        const zahnarztTermine = allTermine.filter(termin => 
            (termin.zahnarztId === zahnarztId || termin.zahnarzt_id === zahnarztId)
        );
        console.log('Fetched and filtered zahnarzt termine count:', zahnarztTermine.length);

        // Enrich termine with behandlungsart names
        const enrichedTermine = await Promise.all(
            zahnarztTermine.map(async (termin) => {
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
        
        // Sort by status and date
        const sortedTermine = enrichedTermine.sort((a, b) => {
            const statusPriority = {
                'GEBUCHT': 1,
                'FREI': 2,
                'ABGESAGT': 3,
                'ABGESCHLOSSEN': 4
            };
            const priorityA = statusPriority[a.status] || 5;
            const priorityB = statusPriority[b.status] || 5;
            
            if (priorityA !== priorityB) {
                return priorityA - priorityB;
            }
            
            return new Date(a.datum) - new Date(b.datum);
        });
        
        // Get upcoming termine (future only, GEBUCHT)
        const now = new Date();
        const upcomingTermine = sortedTermine.filter(t => 
            new Date(t.datum) >= now && t.status === 'GEBUCHT'
        );
        
        // Get next termin and enrich with patient data if available
        let nextTermin = upcomingTermine.length > 0 ? upcomingTermine[0] : null;
        
        if (nextTermin && nextTermin.patientId) {
            try {
                const patientResponse = await fetch(`${API_BASE_URL}/patienten/${nextTermin.patientId}`, {
                    headers: {
                        'Authorization': `Bearer ${jwt_token}`,
                        'Content-Type': 'application/json'
                    }
                });
                if (patientResponse.ok) {
                    nextTermin.patient = await patientResponse.json();
                }
            } catch (error) {
                console.error('Error enriching next termin with patient:', error);
            }
        }
        
        // Calculate stats
        const geplanteTermine = upcomingTermine.length;
        const freieSlots = sortedTermine.filter(t => 
            t.status === 'FREI' && !t.patientId && new Date(t.datum) >= now
        ).length;
        const abgesagteTermine = sortedTermine.filter(t => 
            t.status === 'ABGESAGT'
        ).length;
        const wartelisteVerfuegbar = sortedTermine.filter(t => 
            t.wartelisteAktiv && t.status === 'GEBUCHT' && new Date(t.datum) >= now
        ).length;
        
        // Calculate monthly revenue (current month, ABGESCHLOSSEN)
        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();
        const monatlicheEinnahmen = sortedTermine
            .filter(t => {
                const terminDate = new Date(t.datum);
                return t.status === 'ABGESCHLOSSEN' && 
                       terminDate.getMonth() === currentMonth && 
                       terminDate.getFullYear() === currentYear;
            })
            .reduce((sum, t) => sum + (t.preis || 0), 0);
        
        // Fetch rezensionen to calculate average rating
        let durchschnittsBewertung = 0;
        try {
            const rezensionenResponse = await fetch(`${API_BASE_URL}/rezensionen`, {
                headers: {
                    'Authorization': `Bearer ${jwt_token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            if (rezensionenResponse.ok) {
                const allRezensionen = await rezensionenResponse.json();
                // Only include approved reviews for rating calculation
                const zahnarztRezensionen = allRezensionen.filter(r => 
                    r.zahnarztId === zahnarztId && r.approved === true
                );
                
                if (zahnarztRezensionen.length > 0) {
                    const summe = zahnarztRezensionen.reduce((sum, r) => sum + (r.bewertung || 0), 0);
                    durchschnittsBewertung = (summe / zahnarztRezensionen.length).toFixed(1);
                }
            }
        } catch (error) {
            console.error('Error fetching rezensionen for rating:', error);
        }
        
        console.log('Zahnarzt stats - Geplante Termine:', geplanteTermine, 
                    'Freie Slots:', freieSlots, 
                    'Abgesagte:', abgesagteTermine,
                    'Warteliste verfügbar:', wartelisteVerfuegbar,
                    'Monatliche Einnahmen:', monatlicheEinnahmen,
                    'Durchschnittsbewertung:', durchschnittsBewertung);
        
        return {
            termine: sortedTermine,
            nextTermin,
            stats: {
                geplanteTermine,
                freieSlots,
                abgesagteTermine,
                wartelisteVerfuegbar,
                monatlicheEinnahmen,
                durchschnittsBewertung
            },
            userRole: 'Zahnarzt'
        };
    } catch (error) {
        console.error('Error loading zahnarzt dashboard data:', error);
        return {
            termine: [],
            nextTermin: null,
            stats: {
                geplanteTermine: 0,
                freieSlots: 0,
                abgesagteTermine: 0,
                wartelisteVerfuegbar: 0,
                monatlicheEinnahmen: 0,
                durchschnittsBewertung: 0
            },
            userRole: 'Zahnarzt'
        };
    }
}
