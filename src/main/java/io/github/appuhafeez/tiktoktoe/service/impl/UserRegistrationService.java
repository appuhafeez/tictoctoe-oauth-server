package io.github.appuhafeez.tiktoktoe.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.appuhafeez.tiktoktoe.entity.Authorities;
import io.github.appuhafeez.tiktoktoe.entity.Users;
import io.github.appuhafeez.tiktoktoe.model.RegisterUserRequest;
import io.github.appuhafeez.tiktoktoe.model.RegistrationResponse;
import io.github.appuhafeez.tiktoktoe.repo.AuthoritiesRepository;
import io.github.appuhafeez.tiktoktoe.repo.UsersRepository;
import io.github.appuhafeez.tiktoktoe.service.util.UserRole;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserRegistrationService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthoritiesRepository authoritiesRepository;
	
	@SuppressWarnings("unlikely-arg-type")
	public RegistrationResponse registerUser(Authentication authentication,RegisterUserRequest registerUserRequest) {
		try {
			if(authentication==null || !UserRole.isValidRole(registerUserRequest.getAuthority()) || !authentication.getAuthorities().contains(UserRole.ADMIN_ROLE.name())) {
				registerUserRequest.setAuthority(UserRole.USER_ROLE.name());
			}
			Optional<Users> users = usersRepository.findUsersByEmail(registerUserRequest.getEmail());
			if(users.isPresent()) {
				return new RegistrationResponse("Email already exists",false);
			}
			users = usersRepository.findById(registerUserRequest.getUsername());
			if(users.isPresent()) {
				return new RegistrationResponse("Username already exists",false);
			}
			boolean flag = saveData(registerUserRequest);
			if(flag) {
				return new RegistrationResponse("Registration success",true);
			}else {
				return new RegistrationResponse("Something Went wrong please try again later",false);
			}
		}catch (Exception e) {
			log.error("Error while registering user :: {}",e);
			return new RegistrationResponse(e.getMessage(),false);
		}
	}

	private boolean saveData(RegisterUserRequest registerUserRequest) {
		String encodedPass = passwordEncoder.encode(registerUserRequest.getPassword());
		Users user = usersRepository.saveAndFlush(new Users(registerUserRequest.getUsername(),encodedPass,1,registerUserRequest.getEmail()));
		Authorities authority = authoritiesRepository.saveAndFlush(new Authorities(user.getUsername(), registerUserRequest.getAuthority()));
		if (user != null && authority != null) {
			return true;
		}
		return false;
	}

}
