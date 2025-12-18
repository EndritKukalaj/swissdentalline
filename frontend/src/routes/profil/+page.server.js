import axios from 'axios';
import { error } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';

export async function load({ locals }) {
	const jwt_token = locals.jwt_token;
	const user = locals.user;
	const isAuthenticated = locals.isAuthenticated;

	if (!isAuthenticated || !user || !jwt_token) {
		throw error(401, 'Nicht authentifiziert');
	}

	const roles = user.user_roles || [];
	const isPatient = roles.includes('Patient');
	const isZahnarzt = roles.includes('Zahnarzt');
	const userRole = isPatient ? 'Patient' : 'Zahnarzt';

	if (!isPatient && !isZahnarzt) {
		throw error(403, 'Keine gültige Rolle gefunden');
	}

	// Wähle passenden Profil-Endpoint
	const endpoint = isPatient
		? `${API_BASE_URL}/patienten/profil`
		: `${API_BASE_URL}/zahnaerzte/profil`;

	// Query-Parameter zusammenstellen
	const params = new URLSearchParams({
		name: user.name || '',
		email: user.email || '',
		role: userRole
	});

	try {
		const response = await axios({
			method: 'get',
			url: `${endpoint}?${params.toString()}`,
			headers: { Authorization: 'Bearer ' + jwt_token }
		});

		return {
			profile: response.data,
			userRole: userRole
		};
	} catch (e) {
		console.error('Profil-API Fehler:', e.response?.status, e.response?.data || e.message);
		const status = e.response?.status || 500;
		throw error(status, 'Profildaten konnten nicht geladen werden');
	}
}
