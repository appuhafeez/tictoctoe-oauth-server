/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class DefaultBeanConfig {

	/**
	 * Password Encoder for encoding passwords
	 * @return passwordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Object mapper for data serialization
	 * @return objectMapper
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
