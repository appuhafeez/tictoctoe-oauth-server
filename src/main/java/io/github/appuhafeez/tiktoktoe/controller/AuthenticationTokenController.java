/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.appuhafeez.tiktoktoe.exception.InvalidTokenException;
import io.github.appuhafeez.tiktoktoe.model.AuthenticationRequest;
import io.github.appuhafeez.tiktoktoe.model.RegisterUserRequest;
import io.github.appuhafeez.tiktoktoe.model.RegistrationResponse;
import io.github.appuhafeez.tiktoktoe.model.TokenRefreshResponse;
import io.github.appuhafeez.tiktoktoe.model.ValidateResponse;
import io.github.appuhafeez.tiktoktoe.service.UtilService;
import io.github.appuhafeez.tiktoktoe.service.impl.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;



//@CrossOrigin(origins = "${allowed.origins:*}",allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationTokenController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UtilService utilService;
	
	@Autowired
	private UserRegistrationService userRegistrationService;

	/**
	 * Method for generating the JWT token based on the user details passed in the
	 * request body
	 * 
	 * @param authenticationRequest
	 * @return JWT token
	 */
	@PostMapping("/token")
	public ResponseEntity<Map<String, String>> generateToken(@RequestBody AuthenticationRequest authenticationRequest) {
		Map<String, String> map = new HashMap<>();
		String username = null;
		try {
			username = authenticationRequest.getUsername();
			// To authenticate the passed in user object and returns a fully authenticated
			// object
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, authenticationRequest.getPassword()));
		} catch (AuthenticationException e) {
			map.put("message", "Bad credentials");
			log.error(e+"");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		// Load details of the logged in user from database
		UserDetails userDetails = utilService.loadUserByUsername(username);

		// Generate JWT Token for the successfully authenticated user
		String jwtToken = utilService.generateJwtToken(userDetails,false);
		String refreshToken = utilService.generateJwtToken(userDetails, true);
		map.put("username", username);
		map.put("token", jwtToken);
		map.put("refreshToken", refreshToken);
		map.put("grandedAuthorities", userDetails.getAuthorities().toArray()[0].toString());
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@GetMapping("/token/validate")
	public ResponseEntity<ValidateResponse> validateToken(@AuthenticationPrincipal Authentication userDetails,@RequestHeader("Authorization") String token){
		log.info("Details :: userdetails :: {} , token :: {}",userDetails,token);
		if(token !=null && userDetails!=null) {
			return new ResponseEntity<ValidateResponse>(utilService.getUserData(userDetails.getName()),HttpStatus.OK);
		}else{
			return new ResponseEntity<ValidateResponse>(new ValidateResponse("Unable to validate request"),HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<RegistrationResponse> regiserUser(@AuthenticationPrincipal Authentication authentication,@Valid @RequestBody RegisterUserRequest registerUserRequest){
		RegistrationResponse registrationResponse = userRegistrationService.registerUser(authentication, registerUserRequest);
		if(registrationResponse.getServicePass()) {
			registrationResponse.setServicePass(null);
			return new ResponseEntity<RegistrationResponse>(registrationResponse,HttpStatus.OK);
		}else {
			registrationResponse.setServicePass(null);
			return new ResponseEntity<RegistrationResponse>(registrationResponse,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestParam("refreshToken") String refreshToken){
		try {
			return new ResponseEntity<TokenRefreshResponse>(utilService.refreshAccessToken(refreshToken),HttpStatus.OK);
		}catch (InvalidTokenException e) {
			return new ResponseEntity<TokenRefreshResponse>(new TokenRefreshResponse(e.getMessage()),e.getHttpStatus());
		}
	}
}
