package com.videogames.interfaces;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.videogames.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	public Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
}
