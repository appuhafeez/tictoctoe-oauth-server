/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.config;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.appuhafeez.tiktoktoe.entity.Authorities;
import io.github.appuhafeez.tiktoktoe.entity.Users;
import io.github.appuhafeez.tiktoktoe.repo.AuthoritiesRepository;
import io.github.appuhafeez.tiktoktoe.repo.UsersRepository;


@Service
public class JdbcUserDetailsService implements UserDetailsService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	/**
	 * Method to load the user details from database for the logged in user
	 *@return UserDetails 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> optionalUser = usersRepository.findById(username);
		if(!optionalUser.isPresent()) {
			optionalUser = usersRepository.findUsersByEmail(username);
		}
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			Authorities auth = authoritiesRepository.findByUsername(user.getUsername());
			User authUser = new User(user.getUsername(), user.getPassword(), Arrays.asList(auth));
			return authUser;
		}
		return null;
	}

}
