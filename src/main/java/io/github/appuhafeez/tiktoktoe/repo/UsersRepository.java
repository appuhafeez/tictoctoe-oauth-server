/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.appuhafeez.tiktoktoe.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	
	public Optional<Users> findUsersByEmail(String email);

}
