import { redirect } from '@sveltejs/kit';
import auth from '$lib/server/auth.service.js';
import axios from 'axios';
import 'dotenv/config';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

export const actions = {
  signup: async ({ request, cookies }) => {
    const data = await request.formData();
    const email = data.get('email');
    const password = data.get('password');
    const firstName = data.get('firstName');
    const lastName = data.get('lastName');

    try {
      // 1. Create user in Auth0 and login
      await auth.signup(email, password, firstName, lastName, cookies);
      
      // 2. Get the JWT token and user info from cookies
      const jwt_token = cookies.get('jwt_token');
      const userInfoCookie = cookies.get('user_info');
      const userInfo = JSON.parse(decodeURIComponent(userInfoCookie));
      const auth0UserId = userInfo.sub;
      
      // Extract ID without 'auth0|' prefix
      const patientId = auth0UserId.replace('auth0|', '');
      
      // 3. Create patient entry in MongoDB with Auth0 user ID (without prefix)
      const patientData = {
        id: patientId,
        name: `${firstName} ${lastName}`,
        geburtsdatum: new Date().toISOString(),
        krankenkasse: null,
        adresseId: null
      };
      
      try {
        await axios({
          method: 'post',
          url: `${API_BASE_URL}/api/patienten`,
          headers: { 
            'Authorization': `Bearer ${jwt_token}`,
            'Content-Type': 'application/json'
          },
          data: patientData
        });
        console.log('Patient created successfully with ID:', patientId);
      } catch (apiError) {
        console.error('Failed to create patient in DB:', apiError.response?.data || apiError.message);
        // Continue anyway - user can be created later
      }
      
    } catch (error) {
      console.error('Signup error:', error);
      return {
        error: 'Signup failed. Please try again.'
      };
    }
    
    // If we get here, signup was successful - redirect
    throw redirect(303, '/');
  }
};
