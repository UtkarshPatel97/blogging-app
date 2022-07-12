package com.blogapp.blogappapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blogappapi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);
}
