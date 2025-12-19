import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL || 'http://localhost:8080/api';
const API_PREFIX = API_BASE_URL.endsWith('/api')
    ? API_BASE_URL
    : API_BASE_URL.replace(/\/$/, '') + '/api';

export const load = async ({ locals }) => {
	let flexTermineCount = 0;
	
	// Fetch flex termine count for patients
	if (locals.isAuthenticated && locals.user?.user_roles?.[0] === 'Patient') {
		try {
			const patientId = locals.user.sub.replace('auth0|', '');
			const response = await fetch(
				`${API_PREFIX}/termine/patient/${patientId}/flex`,
				{
					headers: {
						'Authorization': `Bearer ${locals.jwt_token}`,
						'Content-Type': 'application/json'
					}
				}
			);
			
			if (response.ok) {
				const flexTermine = await response.json();
				flexTermineCount = flexTermine.length;
			}
		} catch (error) {
			console.error('Error loading flex termine count for navbar:', error);
		}
	}
	
	return {
		user: locals.user || {},
		isAuthenticated: locals.isAuthenticated || false,
		userRole: locals.user?.user_roles?.[0] || 'Patient',
		flexTermineCount
	};
};
