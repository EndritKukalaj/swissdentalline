import { redirect } from '@sveltejs/kit';
import axios from 'axios';

// Helper function to decode JWT token
function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error('Failed to decode JWT:', error);
        return null;
    }
}

export async function handle({ event, resolve }) {
    // Get JWT token and user info from cookies
    const jwt_token = event.cookies.get('jwt_token');
    const userInfoCookie = event.cookies.get('user_info');
    
    // Add the token to the event locals so it's available in load functions
    event.locals.jwt_token = jwt_token;
    
    // Get user info from cookie (always set on login alongside JWT)
    // Parsing is necessary because user_info is stored as URL-encoded JSON string, not plain text like jwt_token
    if (userInfoCookie) {
        try {
            event.locals.user = JSON.parse(decodeURIComponent(userInfoCookie));
            
            // Decode JWT to extract roles
            if (jwt_token) {
                const decodedToken = decodeJWT(jwt_token);
                if (decodedToken && decodedToken.user_roles) {
                    const roles = decodedToken.user_roles;
                    event.locals.user.user_Role = roles.length > 0 ? roles[0] : 'Undefined';
                } else {
                    event.locals.user.user_Role = 'Undefined';
                }
            }
        } catch (error) {
            console.error('Failed to parse user info cookie:', error);
            event.locals.user = {};
        }
    } else {
        // No user info cookie means user is not logged in
        event.locals.user = {};
    }
    
    // isAuthenticated: we assume that users are authenticated if the property "user.name" exists
    if (jwt_token && event.locals.user && event.locals.user.name) {
        event.locals.isAuthenticated = true;
    } else {
        event.locals.isAuthenticated = false;
    }
    
    // Handle preflight requests if needed for API calls
    if (event.request.method === 'OPTIONS') {
        return new Response(null, {
            status: 200,
            headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
                'Access-Control-Allow-Headers': 'Content-Type, Authorization',
            },
        });
    }
    
    return resolve(event);
}
