import { redirect } from '@sveltejs/kit';

export const load = async ({ locals }) => {
    if (!locals.isAuthenticated || !locals.user) {
        throw redirect(302, '/login');
    }
    
    // Redirect to first step
    throw redirect(302, '/buchen/behandlung');
};
