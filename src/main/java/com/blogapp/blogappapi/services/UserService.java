package com.blogapp.blogappapi.services;

import java.util.List;

import com.blogapp.blogappapi.payloads.UserDTO;

public interface UserService {
	
	UserDTO registerNewUser(UserDTO userDto);

	UserDTO createUser(UserDTO userDto);
	
	UserDTO updateUser(UserDTO userDto, Integer userId);
	
	UserDTO getUserById(Integer userId);
	
	List<UserDTO> getAllUsers();
	
	void deleteUser(Integer userId);
}
