import axios from 'axios';
import { env } from '$env/dynamic/private';

/**
 * Central API helper function for backend communication
 * 
 * @param {string} method - HTTP method (GET, POST, PUT, DELETE, etc.)
 * @param {string} resource - API endpoint path (e.g., 'articles', 'users/me')
 * @param {Object} locals - SvelteKit locals object containing jwt_token
 * @param {Object} data - Request body data (optional)
 * @returns {Promise<any>} Response data from backend
 */
export async function api(method, resource, locals, data) {
    // Get JWT token from locals (set by hooks.server.js from cookies)
    const jwt_token = locals.jwt_token;

    const headers = {
        'Content-Type': 'application/json'
    };

    // Add Authorization header if token exists
    if (jwt_token) {
        headers['Authorization'] = `Bearer ${jwt_token}`;
    }

    try {
        const response = await axios({
            method,
            url: `${env.API_BASE_URL}/${resource}`,
            headers,
            data
        });
        return response.data;
    } catch (error) {
        // Enhanced error logging for debugging
        console.error(`API Error [${method} ${resource}]:`, error.response?.status);
        if (error.response?.data) {
            console.error('Error Details:', error.response.data);
        }
        
        // Re-throw error so frontend can handle it
        throw error;
    }
}

export default api;
