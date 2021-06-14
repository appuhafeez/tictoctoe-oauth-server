package io.github.appuhafeez.tiktoktoe.model;


import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RegisterUserRequest {

	@NotNull(message = "username is required")
	private String username;
	@NotNull(message = "password is required")
	private String password;
	@NotNull(message = "email is required")
	private String email;
	private String authority;
	
}
