package com.blogapp.blogappapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blogappapi.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
