import { redirect, error } from '@sveltejs/kit';
import axios from 'axios';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

export async function load({ params, locals }) {
  if (!locals.isAuthenticated) {
    throw redirect(302, '/login');
  }

  const userRole = locals.user.user_roles?.[0] || 'Patient';
  
  if (userRole !== 'Zahnarzt') {
    throw redirect(302, '/');
  }

  const jwt_token = locals.jwt_token;
  const terminId = params.id;

  try {
    // Lade Termin-Details
    const terminResponse = await axios.get(`${API_BASE_URL}/api/termine/${terminId}`, {
      headers: {
        'Authorization': `Bearer ${jwt_token}`,
        'Content-Type': 'application/json'
      }
    });

    const termin = terminResponse.data;

    // Nur freie Slots und Flex-Termine können bearbeitet werden
    if (termin.status !== 'FREI' && termin.status !== 'FLEX') {
      throw redirect(302, `/termine/${terminId}`);
    }

    // Lade Behandlungsarten
    const behandlungsartenResponse = await axios.get(`${API_BASE_URL}/api/behandlungsarten`, {
      headers: {
        'Authorization': `Bearer ${jwt_token}`
      }
    });

    return {
      termin,
      behandlungsarten: behandlungsartenResponse.data,
      userRole
    };
  } catch (err) {
    console.error('Error loading slot:', err);
    throw error(404, 'Slot nicht gefunden');
  }
}

export const actions = {
  updateSlot: async ({ request, params, locals }) => {
    if (!locals.isAuthenticated) {
      return { success: false, error: 'Nicht authentifiziert' };
    }

    const userRole = locals.user.user_roles?.[0] || 'Patient';
    if (userRole !== 'Zahnarzt') {
      return { success: false, error: 'Nur Zahnärzte können Slots bearbeiten' };
    }

    const formData = await request.formData();
    const jwt_token = locals.jwt_token;
    const terminId = params.id;

    try {
      // Lade aktuellen Termin um zahnarztId zu erhalten
      const terminResponse = await axios.get(`${API_BASE_URL}/api/termine/${terminId}`, {
        headers: {
          'Authorization': `Bearer ${jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      const termin = terminResponse.data;

      // Erstelle Datum-Zeit-String
      const datum = formData.get('datum');
      const uhrzeit = formData.get('uhrzeit');
      const dateTimeString = `${datum}T${uhrzeit}:00`;

      const terminDTO = {
        zahnarztId: termin.zahnarztId,
        behandlungsartId: formData.get('behandlungsartId'),
        datum: new Date(dateTimeString).toISOString(),
        dauerMinuten: parseInt(formData.get('dauerMinuten')),
        preis: parseFloat(formData.get('preis')),
        status: termin.status
      };

      await axios.put(`${API_BASE_URL}/api/termine/${terminId}`, terminDTO, {
        headers: {
          'Authorization': `Bearer ${jwt_token}`,
          'Content-Type': 'application/json'
        }
      });

      throw redirect(303, `/termine/${terminId}?slotUpdated=true`);
    } catch (err) {
      if (err.status === 303) throw err;
      console.error('Error updating slot:', err);
      return {
        success: false,
        error: err.response?.data?.message || 'Fehler beim Aktualisieren des Slots'
      };
    }
  }
};
