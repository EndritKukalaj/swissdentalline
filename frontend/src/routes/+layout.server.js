export const load = async ({ locals }) => {
	return {
		user: locals.user || {},
		isAuthenticated: locals.isAuthenticated || false,
		userRole: locals.user?.user_roles?.[0] || 'Patient'
	};
};
