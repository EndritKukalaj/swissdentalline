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

      const terminDTO = {
        zahnarztId: zahnarztId,
        behandlungsartId: formData.get('behandlungsartId'),
        datum: new Date(dateTimeString).toISOString(),
        dauerMinuten: parseInt(formData.get('dauerMinuten')),
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
      return {
        success: false,
        error: err.response?.data?.message || 'Fehler beim Erstellen des Slots'
      };
    }
  }
};
