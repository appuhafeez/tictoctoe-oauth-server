package io.github.appuhafeez.tiktoktoe.service.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class JwtUtil {
	
	public static final String AUTH = "auth";

	public static final String REFRESH = "refresh";

	private static final String SCOPE = "scope";

	/**
	 * bcrypt hashed secret key for signing the jwt tokens
	 * Key was hashed with 16 rounds and with value 'spring-boot-jwt-security-secret'
	 */
	@Value("${spring.security.jwt.hash.key}")
	private String jwtHashKey;
	
	@Value("${spring.security.jwt.token.validity:60000}")
	private Integer jwtTokenValidity;
	
	@Value("${spring.security.jwt.token.refresh.validity:25920000000}")
	private long jwtRefreshTokenValidity;

	
	/**
	 * Extract the username from JWT token
	 * @param token
	 * @return username
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extraction expiry time from the JWT token
	 * @param token
	 * @return expiry time
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(jwtHashKey).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Generate the JWT token for the successfully authenticated user
	 * @param userDetails
	 * @return JWT Token
	 */
	public String generateToken(UserDetails userDetails, boolean isRefreshToken) {
		Map<String, Object> claims = new HashMap<>();
		if(isRefreshToken) {
			claims.put(SCOPE, REFRESH);
		}else {
			claims.put(SCOPE, AUTH);
		}
		return createToken(claims, userDetails.getUsername(),isRefreshToken);
	}

	private String createToken(Map<String, Object> claims, String subject, boolean isRefreshToken) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (isRefreshToken? jwtRefreshTokenValidity :jwtTokenValidity)))
				.signWith(SignatureAlgorithm.HS256, jwtHashKey).compact();
	}

	/**
	 * Validate token for the user and expiry
	 * @param token
	 * @param userDetails
	 * @return boolean flag specifying the validity of the token
	 */
	public boolean validateToken(String token, UserDetails userDetails,String scopeProvided) {
		final String username = extractUsername(token);
		final String scope = getSpecificClaim(token, SCOPE);
		if(!scope.equals(scopeProvided)) return false;
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	public String getSpecificClaim(String token, String key) {
		Claims claims = Jwts.parser().setSigningKey(jwtHashKey).parseClaimsJws(token).getBody();
		return claims.get(key, String.class);
	}
}