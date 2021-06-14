/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.appuhafeez.tiktoktoe.entity.Authorities;


public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {

	Authorities findByUsername(String username);
}
