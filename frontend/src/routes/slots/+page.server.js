import { redirect } from '@sveltejs/kit';
import axios from 'axios';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

export async function load({ locals }) {
  if (!locals.isAuthenticated) {
    throw redirect(302, '/login');
  }

  const userRole = locals.user.user_roles?.[0] || 'Patient';
  
  if (userRole !== 'Zahnarzt') {
    throw redirect(302, '/');
  }

  const jwt_token = locals.jwt_token;
  const auth0UserId = locals.user.sub;
  const userId = auth0UserId.replace('auth0|', '');

  try {
    // Lade Zahnarzt-Daten
    const zahnarztResponse = await axios.get(`${API_BASE_URL}/api/zahnaerzte/${userId}`, {
      headers: {
        'Authorization': `Bearer ${jwt_token}`,
        'Content-Type': 'application/json'
      }
    });

    const zahnarzt = zahnarztResponse.data;

    // Lade Behandlungsarten für Dropdown
    const behandlungsartenResponse = await axios.get(`${API_BASE_URL}/api/behandlungsarten`, {
      headers: {
        'Authorization': `Bearer ${jwt_token}`
      }
    });

    return {
      zahnarzt,
      behandlungsarten: behandlungsartenResponse.data,
      userRole
    };
  } catch (error) {
    console.error('Error loading slot data:', error);
    throw redirect(302, '/');
  }
}

export const actions = {
  createSlot: async ({ request, locals }) => {
    if (!locals.isAuthenticated) {
      return { success: false, error: 'Nicht authentifiziert' };
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    if (userRole !== 'Zahnarzt') {
      return { success: false, error: 'Nur Zahnärzte können Slots erstellen' };
    }

    const formData = await request.formData();
    const jwt_token = locals.jwt_token;
    const auth0UserId = locals.user.sub;
    const userId = auth0UserId.replace('auth0|', '');

    try {
      // Lade Zahnarzt-ID
      const zahnarztResponse = await axios.get(`${API_BASE_URL}/api/zahnaerzte/${userId}`, {
        headers: { 'Authorization': `Bearer ${jwt_token}` }
      });

      const zahnarztId = zahnarztResponse.data.id;

      // Erstelle Datum-Zeit-String aus separaten Feldern
      const datum = formData.get('datum');
      const uhrzeit = formData.get('uhrzeit');
      const dateTimeString = `${datum}T${uhrzeit}:00`;
      const dauerMinuten = parseInt(formData.get('dauerMinuten'));

      const slotStartTime = new Date(dateTimeString);
      const slotEndTime = new Date(slotStartTime.getTime() + dauerMinuten * 60000);

      // Prüfe auf überlappende Termine
      try {
        const existingTermineResponse = await axios.get(`${API_BASE_URL}/api/termine`, {
          headers: { 'Authorization': `Bearer ${jwt_token}` }
        });

        // Filtere Termine nach Zahnarzt
        const existingTermine = existingTermineResponse.data.filter(
          termin => termin.zahnarztId === zahnarztId
        );

        // Prüfe Überlappungen
        const hasOverlap = existingTermine.some(termin => {
          const existingStart = new Date(termin.datum);
          const existingEnd = new Date(existingStart.getTime() + termin.dauerMinuten * 60000);

          return slotStartTime < existingEnd && slotEndTime > existingStart;
        });

        if (hasOverlap) {
          return {
            success: false,
            error: 'Zu diesem Zeitpunkt existiert bereits ein Termin. Bitte wählen Sie eine andere Zeit.'
          };
        }
      } catch (validationError) {
        console.error('Error validating slot overlap:', validationError);
        // Bei Fehler bei der Validierung, fahre trotzdem fort
        // Das Backend sollte die finale Validierung machen
      }

      const terminDTO = {
        zahnarztId: zahnarztId,
        behandlungsartId: formData.get('behandlungsartId'),
        datum: slotStartTime.toISOString(),
        dauerMinuten: dauerMinuten,
        preis: parseFloat(formData.get('preis')),
        status: 'FREI'
      };

      const response = await axios.post(`${API_BASE_URL}/api/termine`, terminDTO, {
        headers: {
          'Authorization': `Bearer ${jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      const createdTerminId = response.data.id;
      throw redirect(303, `/termine/${createdTerminId}?slotCreated=true`);
    } catch (err) {
      // Re-throw redirect errors
      if (err.status === 303) {
        throw err;
      }
      console.error('Error creating slot:', err);
      
      // Handle 409 Conflict from backend
      if (err.response?.status === 409) {
        return {
          success: false,
          error: 'Zu diesem Zeitpunkt existiert bereits ein Termin. Bitte wählen Sie eine andere Zeit.'
        };
      }
      
      return {
        success: false,
        error: err.response?.data?.message || 'Fehler beim Erstellen des Slots'
      };
    }
  }
};
