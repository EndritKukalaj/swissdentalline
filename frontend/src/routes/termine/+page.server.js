import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';

export const load = async ({ locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        return {
            termine: [],
            stats: {
                geplanteTermine: 0,
                offeneWartelisten: 0,
                freieSlots: 0,
                abgeschlosseneTermine: 0
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
        
        // Handle Zahnarzt role
        if (userRole === 'Zahnarzt') {
            return await loadZahnarztTermine(userId, jwt_token);
        }
        
        // Handle Patient role
        return await loadPatientTermine(userId, jwt_token);
    } catch (error) {
        console.error('Error loading termine data:', error);
        return {
            termine: [],
            stats: {
                geplanteTermine: 0,
                offeneWartelisten: 0,
                freieSlots: 0,
                abgeschlosseneTermine: 0
            },
            userRole: null
        };
    }
};

async function loadPatientTermine(patientId, jwt_token) {
    // 1. Load patient from database
    const patientResponse = await fetch(`${API_BASE_URL}/patienten/${patientId}`, {
        headers: {
            'Authorization': `Bearer ${jwt_token}`,
            'Content-Type': 'application/json'
        }
    });
    
    if (!patientResponse.ok) {
        throw new Error(`Patient not found: ${patientResponse.status}`);
    }
    
    const patient = await patientResponse.json();
    
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
    
    // Sort by status and date
    const sortedTermine = enrichedTermine.sort((a, b) => {
        const statusPriority = {
            'GEBUCHT': 1,
            'ABGESAGT': 2,
            'ABGESCHLOSSEN': 3
        };
        const priorityA = statusPriority[a.status] || 4;
        const priorityB = statusPriority[b.status] || 4;
        
        if (priorityA !== priorityB) {
            return priorityA - priorityB;
        }
        
        return new Date(a.datum) - new Date(b.datum);
    });
    
    // Calculate stats
    const now = new Date();
    const upcomingTermine = sortedTermine.filter(t => 
        new Date(t.datum) >= now && t.status === 'GEBUCHT'
    );
    const geplanteTermine = upcomingTermine.length;
    const offeneWartelisten = sortedTermine.filter(t => 
        t.wartelisteAktiv && t.status === 'GEBUCHT'
    ).length;
    const abgeschlosseneTermine = sortedTermine.filter(t => 
        t.status === 'ABGESCHLOSSEN'
    ).length;
    
    return {
        termine: sortedTermine,
        stats: {
            geplanteTermine,
            offeneWartelisten,
            abgeschlosseneTermine
        },
        userRole: 'Patient'
    };
}

async function loadZahnarztTermine(zahnarztId, jwt_token) {
    try {
        // 1. Load zahnarzt from database
        const zahnarztResponse = await fetch(`${API_BASE_URL}/zahnaerzte/${zahnarztId}`, {
            headers: {
                'Authorization': `Bearer ${jwt_token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!zahnarztResponse.ok) {
            throw new Error(`Zahnarzt not found: ${zahnarztResponse.status}`);
        }
        
        const zahnarzt = await zahnarztResponse.json();
        
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
        
        // Calculate stats
        const now = new Date();
        const upcomingTermine = sortedTermine.filter(t => 
            new Date(t.datum) >= now && t.status === 'GEBUCHT'
        );
        const geplanteTermine = upcomingTermine.length;
        const freieSlots = sortedTermine.filter(t => 
            t.status === 'FREI' && !t.patientId && new Date(t.datum) >= now
        ).length;
        const abgeschlosseneTermine = sortedTermine.filter(t => 
            t.status === 'ABGESCHLOSSEN'
        ).length;
        
        return {
            termine: sortedTermine,
            stats: {
                geplanteTermine,
                freieSlots,
                abgeschlosseneTermine
            },
            userRole: 'Zahnarzt'
        };
    } catch (error) {
        console.error('Error loading zahnarzt termine data:', error);
        return {
            termine: [],
            stats: {
                geplanteTermine: 0,
                freieSlots: 0,
                abgeschlosseneTermine: 0
            },
            userRole: 'Zahnarzt'
        };
    }
}
