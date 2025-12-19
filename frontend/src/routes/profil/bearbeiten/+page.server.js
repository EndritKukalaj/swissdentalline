import axios from 'axios';
import { error, redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';
const API_PREFIX = API_BASE_URL.endsWith('/api')
  ? API_BASE_URL
  : API_BASE_URL.replace(/\/$/, '') + '/api';

export async function load({ locals }) {
  if (!locals.isAuthenticated) {
    throw redirect(302, '/login');
  }

  const user = locals.user;
  const roles = user.user_roles || [];
  const isPatient = roles.includes('Patient');
  const userRole = isPatient ? 'Patient' : 'Zahnarzt';

  try {
    const endpoint = isPatient ? '/patienten/profil' : '/zahnaerzte/profil';
    
    const response = await axios.get(`${API_PREFIX}${endpoint}`, {
      params: {
        name: user.name,
        email: user.email,
        role: userRole
      },
      headers: {
        Authorization: `Bearer ${locals.jwt_token}`
      }
    });

    // ID und Adressdaten über Model-Klasse finden
    let entityId = null;
    let currentAdresse = null;
    let entityData = null;
    const userId = user.sub.replace('auth0|', '');
    
    try {
      if (isPatient) {
        // Primär: Suche über Auth0 ID
        try {
          const patientByIdResponse = await axios.get(`${API_PREFIX}/patienten/${userId}`, {
            headers: { Authorization: `Bearer ${locals.jwt_token}` }
          });
          if (patientByIdResponse.data) {
            entityData = patientByIdResponse.data;
            entityId = entityData.id;
          }
        } catch (idError) {
          // Fallback: Suche über Name
          const patientsResponse = await axios.get(`${API_PREFIX}/patienten/name/${user.name}`, {
            headers: { Authorization: `Bearer ${locals.jwt_token}` }
          });
          if (patientsResponse.data && patientsResponse.data.length > 0) {
            entityData = patientsResponse.data[0];
            entityId = entityData.id;
          }
        }
        
        // Adresse-Details laden (falls vorhanden)
        if (entityData && entityData.adresseId) {
          try {
            const adresseResponse = await axios.get(`${API_PREFIX}/adressen/${entityData.adresseId}`, {
              headers: { Authorization: `Bearer ${locals.jwt_token}` }
            });
            currentAdresse = adresseResponse.data;
          } catch (e) {
            throw error(500, 'Adresse konnte nicht geladen werden');
          }
        }
      } else {
        // Primär: Suche über Auth0 ID
        try {
          const zahnarztByIdResponse = await axios.get(`${API_PREFIX}/zahnaerzte/${userId}`, {
            headers: { Authorization: `Bearer ${locals.jwt_token}` }
          });
          if (zahnarztByIdResponse.data) {
            entityData = zahnarztByIdResponse.data;
            entityId = entityData.id;
          }
        } catch (idError) {
          // Fallback: Suche über Name
          const zahnarztnameResponse = await axios.get(`${API_PREFIX}/zahnaerzte/name/${user.name}`, {
            headers: { Authorization: `Bearer ${locals.jwt_token}` }
          });
          if (zahnarztnameResponse.data && zahnarztnameResponse.data.length > 0) {
            entityData = zahnarztnameResponse.data[0];
            entityId = entityData.id;
          }
        }
      }
    } catch (e) {
      throw error(500, 'Benutzerdaten konnten nicht geladen werden');
    }

    // Adressen laden für Dropdown
    let adressen = [];
    try {
      if (isPatient) {
        // Patient: Lade alle Adressen (HOME)
        const adressenResponse = await axios.get(`${API_PREFIX}/adressen`, {
          headers: {
            Authorization: `Bearer ${locals.jwt_token}`
          }
        });
        adressen = adressenResponse.data;
      } else {
        // Zahnarzt: Lade nur PRAXIS-Adressen über Typ-Filter
        const adressenResponse = await axios.get(`${API_PREFIX}/adressen/typ/PRAXIS`, {
          params: {
            page: 0,
            size: 100  // Alle Praxis-Adressen laden
          },
          headers: {
            Authorization: `Bearer ${locals.jwt_token}`
          }
        });
        adressen = adressenResponse.data.content || adressenResponse.data;
      }
    } catch (e) {
      throw error(500, 'Adressenliste konnte nicht geladen werden');
    }

    return {
      profile: response.data,
      userRole: userRole,
      entityId: entityId,
      entityData: entityData,
      currentAdresse: currentAdresse,
      adressen: adressen
    };
  } catch (e) {
    throw error(e.response?.status || 500, 'Profil konnte nicht geladen werden');
  }
}

export const actions = {
  updatePatient: async ({ request, locals }) => {
    if (!locals.isAuthenticated) {
      throw error(401, 'Nicht authentifiziert');
    }

    const formData = await request.formData();
    const patientId = formData.get('id');
    let adresseId = formData.get('adresseId');
    
    try {
      // Adresse aktualisieren oder erstellen
      const adresseDTO = {
        strasse: formData.get('strasse'),
        plz: formData.get('plz'),
        ort: formData.get('ort'),
        typ: 'HOME',
        bezeichnung: null
      };

      if (adresseId && adresseId !== '' && adresseId !== 'null') {
        // Adresse existiert bereits - aktualisieren
        await axios.put(`${API_PREFIX}/adressen/${adresseId}`, adresseDTO, {
          headers: {
            Authorization: `Bearer ${locals.jwt_token}`,
            'Content-Type': 'application/json'
          }
        });
      } else {
        // Keine Adresse vorhanden - neue erstellen
        const createResponse = await axios.post(`${API_PREFIX}/adressen`, adresseDTO, {
          headers: {
            Authorization: `Bearer ${locals.jwt_token}`,
            'Content-Type': 'application/json'
          }
        });
        adresseId = createResponse.data.id;
      }

      // Patient aktualisieren
      const geburtsdatumString = formData.get('geburtsdatum');
      // Konvertiere Datum (YYYY-MM-DD) zu Instant (ISO-8601 mit Zeitstempel)
      const geburtsdatumInstant = geburtsdatumString ? new Date(geburtsdatumString + 'T00:00:00.000Z').toISOString() : null;
      const patientDTO = {
        name: formData.get('name'),
        geburtsdatum: geburtsdatumInstant,
        krankenkasse: formData.get('krankenkasse'),
        adresseId: adresseId
      };

      await axios.put(`${API_PREFIX}/patienten/${patientId}`, patientDTO, {
        headers: {
          Authorization: `Bearer ${locals.jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      throw redirect(303, '/profil');
    } catch (e) {
      if (e.status === 303) throw e;
      return {
        success: false,
        error: e.response?.data?.message || 'Fehler beim Aktualisieren'
      };
    }
  },

  updateZahnarzt: async ({ request, locals }) => {
    if (!locals.isAuthenticated) {
      throw error(401, 'Nicht authentifiziert');
    }

    const formData = await request.formData();
    const zahnarztId = formData.get('id');
    
    // Validierung: zahnarztId muss vorhanden sein
    if (!zahnarztId) {
      return {
        success: false,
        error: 'Zahnarzt ID konnte nicht gefunden werden. Bitte laden Sie die Seite neu.'
      };
    }
    
    const zahnarztDTO = {
      name: formData.get('name'),
      praxisAdresseId: formData.get('praxisAdresseId')
    };

    try {
      await axios.put(`${API_PREFIX}/zahnaerzte/${zahnarztId}`, zahnarztDTO, {
        headers: {
          Authorization: `Bearer ${locals.jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      throw redirect(303, '/profil');
    } catch (e) {
      if (e.status === 303) throw e;
      return {
        success: false,
        error: e.response?.data?.message || 'Fehler beim Aktualisieren'
      };
    }
  },

  createAdresse: async ({ request, locals }) => {
    if (!locals.isAuthenticated) {
      throw error(401, 'Nicht authentifiziert');
    }

    const formData = await request.formData();
    const user = locals.user;
    const roles = user.user_roles || [];
    const isPatient = roles.includes('Patient');
    
    const adresseDTO = {
      strasse: formData.get('strasse'),
      plz: formData.get('plz'),
      ort: formData.get('ort'),
      typ: formData.get('typ'),
      bezeichnung: formData.get('bezeichnung') || null
    };

    try {
      const response = await axios.post(`${API_PREFIX}/adressen`, adresseDTO, {
        headers: {
          Authorization: `Bearer ${locals.jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      const createdAdresse = response.data;

      // Wenn Zahnarzt und Praxis-Adresse, automatisch dem Zahnarzt zuweisen
      if (!isPatient && adresseDTO.typ === 'PRAXIS') {
        const userId = user.sub.replace('auth0|', '');
        
        try {
          // Lade Zahnarzt-Daten
          const zahnarztResponse = await axios.get(`${API_PREFIX}/zahnaerzte/${userId}`, {
            headers: { Authorization: `Bearer ${locals.jwt_token}` }
          });
          
          if (zahnarztResponse.data) {
            const zahnarztId = zahnarztResponse.data.id;
            
            // Update Zahnarzt mit neuer Praxis-Adresse
            await axios.put(`${API_PREFIX}/zahnaerzte/${zahnarztId}`, {
              name: zahnarztResponse.data.name,
              praxisAdresseId: createdAdresse.id
            }, {
              headers: {
                Authorization: `Bearer ${locals.jwt_token}`,
                'Content-Type': 'application/json'
              }
            });
          }
        } catch (updateError) {
          console.error('Fehler beim Zuweisen der Praxis zum Zahnarzt:', updateError);
          // Adresse wurde erstellt, aber Zuweisung fehlgeschlagen - trotzdem als Erfolg werten
        }
      }

      return {
        success: true,
        adresse: createdAdresse
      };
    } catch (e) {
      return {
        success: false,
        error: e.response?.data?.message || 'Fehler beim Erstellen der Adresse'
      };
    }
  },

  updateAdresse: async ({ request, locals }) => {
    if (!locals.isAuthenticated) {
      throw error(401, 'Nicht authentifiziert');
    }

    const formData = await request.formData();
    const adresseId = formData.get('id');
    
    if (!adresseId) {
      return {
        success: false,
        error: 'Adresse ID fehlt'
      };
    }
    
    const adresseDTO = {
      strasse: formData.get('strasse'),
      plz: formData.get('plz'),
      ort: formData.get('ort'),
      typ: formData.get('typ'),
      bezeichnung: formData.get('bezeichnung') || null
    };

    try {
      const response = await axios.put(`${API_PREFIX}/adressen/${adresseId}`, adresseDTO, {
        headers: {
          Authorization: `Bearer ${locals.jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      return {
        success: true,
        updated: true,
        adresse: response.data
      };
    } catch (e) {
      return {
        success: false,
        error: e.response?.data?.message || 'Fehler beim Aktualisieren der Adresse'
      };
    }
  }
};
