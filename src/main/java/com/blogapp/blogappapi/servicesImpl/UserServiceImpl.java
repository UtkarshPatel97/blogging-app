package com.blogapp.blogappapi.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blogapp.blogappapi.config.AppConstants;
import com.blogapp.blogappapi.entity.Role;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.payloads.UserDTO;
import com.blogapp.blogappapi.repository.RoleRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public UserDTO createUser(UserDTO userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
		Role role = this.roleRepository.findById(AppConstants.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role","User Role",AppConstants.ROLE_USER));
		user.getRoles().add(role);
	//	User user = this.dtoToUser(userDto);
		User savedUser = this.userRepository.save(user);
		return this.modelMapper.map(savedUser, UserDTO.class);
	//	return this.userToDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(UserDTO userDto, Integer userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setAbout(userDto.getAbout());
		User updatedUser = this.userRepository.save(user);
		return this.modelMapper.map(updatedUser, UserDTO.class);
	//	return this.userToDTO(updatedUser);
	}

	@Override
	public UserDTO getUserById(Integer userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
	//	return this.userToDTO(user);
		return this.modelMapper.map(user, UserDTO.class);
	}

	@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = this.userRepository.findAll();
	//	List<UserDTO> userDtos = users.stream().map(user -> this.userToDTO(user)).collect(Collectors.toList());
		List<UserDTO> userDtos = users.stream().map(user -> this.modelMapper.map(user,UserDTO.class)).collect(Collectors.toList());
		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		this.userRepository.delete(user);
	}
/*
	private User dtoToUser(UserDTO userDto) {
		User user = new User();
		user.setId(userDto.getId());
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setAbout(userDto.getAbout());
		return user;
	}
	
	private UserDTO userToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setName(user.getName());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(user.getPassword());
		userDTO.setAbout(user.getAbout());
		return userDTO;
	}*/

	@Override
	public UserDTO registerNewUser(UserDTO userDto) {
			User user = this.modelMapper.map(userDto, User.class);
		//	User user = this.dtoToUser(userDto);
			
			//encoded password
			user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
			
			//roles
			Role role = this.roleRepository.findById(AppConstants.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role","User Role",AppConstants.ROLE_USER));
			user.getRoles().add(role);
			
			User savedUser = this.userRepository.save(user);
			return this.modelMapper.map(savedUser, UserDTO.class);
		//	return this.userToDTO(savedUser);
	}
}
