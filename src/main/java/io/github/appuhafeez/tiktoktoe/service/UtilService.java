/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.service;

import org.springframework.security.core.userdetails.UserDetails;

import io.github.appuhafeez.tiktoktoe.model.TokenRefreshResponse;
import io.github.appuhafeez.tiktoktoe.model.ValidateResponse;


public interface UtilService {

	UserDetails loadUserByUsername(String username);
	
	String generateJwtToken(UserDetails userDetails, boolean isRefreshToken);

	String extractUsername(String jwtToken);
	
	boolean validateToken(String jwtToken, UserDetails userDetails, String scope);
	
	ValidateResponse getUserData(String username);
	
	TokenRefreshResponse refreshAccessToken(String jwtToken);
	
}
