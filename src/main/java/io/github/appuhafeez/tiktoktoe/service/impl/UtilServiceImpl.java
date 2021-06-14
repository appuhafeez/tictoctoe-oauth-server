/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.service.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.github.appuhafeez.tiktoktoe.config.JdbcUserDetailsService;
import io.github.appuhafeez.tiktoktoe.entity.Authorities;
import io.github.appuhafeez.tiktoktoe.entity.Users;
import io.github.appuhafeez.tiktoktoe.exception.InvalidTokenException;
import io.github.appuhafeez.tiktoktoe.model.TokenRefreshResponse;
import io.github.appuhafeez.tiktoktoe.model.ValidateResponse;
import io.github.appuhafeez.tiktoktoe.repo.AuthoritiesRepository;
import io.github.appuhafeez.tiktoktoe.repo.UsersRepository;
import io.github.appuhafeez.tiktoktoe.service.UtilService;
import io.github.appuhafeez.tiktoktoe.service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class UtilServiceImpl implements UtilService {

	@Autowired
	private JdbcUserDetailsService jdbcUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	/**
	 * To load the user details from database for the logged in user
	 * @return UserDetails
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		return jdbcUserDetailsService.loadUserByUsername(username);
	}

	/**
	 * To generate the JWT token with authenticated user details object
	 * @return JWT token
	 */
	@Override
	public String generateJwtToken(UserDetails userDetails, boolean isRefreshToken) {
		return jwtUtil.generateToken(userDetails, isRefreshToken);
	}

	/**
	 * Extract the username from the JWT token
	 * @return Username 
	 */
	@Override
	public String extractUsername(String jwtToken) {
		return jwtUtil.extractUsername(jwtToken);
	}

	/**
	 * Validate the token for user details and expiry
	 * @return boolean: status of token validity
	 */
	@Override
	public boolean validateToken(String jwtToken, UserDetails userDetails, String scope) {
		log.info("Validating the JWT token");
		return jwtUtil.validateToken(jwtToken, userDetails,scope);
	}

	@Override
	public ValidateResponse getUserData(String username) {
		log.info("getting userData for user :: {}",username);
		ValidateResponse validateResponse = new ValidateResponse();
		Optional<Users> user = usersRepository.findById(username);
		if(user.isPresent()) {
			BeanUtils.copyProperties(user.get(), validateResponse);
		} 
		Authorities authorities = authoritiesRepository.findByUsername(username);
		validateResponse.setAuthorities(Arrays.asList(authorities.getAuthority()));
		return validateResponse;
	}

	@Override
	public TokenRefreshResponse refreshAccessToken(String jwtToken) {
		TokenRefreshResponse tokenRefreshResponse = new TokenRefreshResponse();
		try {
			String username = extractUsername(jwtToken);
			UserDetails userDetails = loadUserByUsername(username);
			if(validateToken(jwtToken, userDetails, JwtUtil.REFRESH)) {
				String jwtAccessToken = generateJwtToken(userDetails,false);
				tokenRefreshResponse.setToken(jwtAccessToken);
				tokenRefreshResponse.setUsername(username);
			}else {
				throw new InvalidTokenException("Invalid Token",HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			log.error("Error occured while validating token :: {}",e);
			throw new InvalidTokenException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return tokenRefreshResponse;
	}

}
