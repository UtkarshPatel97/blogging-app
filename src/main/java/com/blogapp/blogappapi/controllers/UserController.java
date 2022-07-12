package com.blogapp.blogappapi.controllers;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blogappapi.config.AppConstants;
import com.blogapp.blogappapi.entity.Role;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.models.UserModel;
import com.blogapp.blogappapi.payloads.ApiResponse;
import com.blogapp.blogappapi.payloads.UserDTO;
import com.blogapp.blogappapi.repository.RoleRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.security.JwtTokenHelper;
import com.blogapp.blogappapi.services.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private JwtTokenHelper jwtTokenhelper;
	
	@PostMapping("/")
	public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserModel userModel){
		UserDTO userDto = this.modelMapper.map(userModel, UserDTO.class);
		UserDTO createUserDto = this.userService.createUser(userDto);
		return new ResponseEntity<>(createUserDto,HttpStatus.CREATED);
	}
	
	@PutMapping("/{userId}")
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserModel userModel, @PathVariable Integer userId){
		UserDTO userDto = this.modelMapper.map(userModel, UserDTO.class);
		UserDTO updateUserDto = this.userService.updateUser(userDto, userId);
		return ResponseEntity.ok(updateUserDto);
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/admin/{userId}")
	public ResponseEntity<UserDTO> giveAdminRoleToUser(@PathVariable Integer userId){
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User Id",userId));
		Role role = this.roleRepository.findById(AppConstants.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role","User Role",AppConstants.ROLE_ADMIN));
		user.getRoles().add(role);
		User savedUser = this.userRepository.save(user);
		UserDTO userDto = this.modelMapper.map(savedUser, UserDTO.class);
		return new ResponseEntity<UserDTO>(userDto,HttpStatus.OK);
	}
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request, @PathVariable Integer userId){
		Boolean authenticationStatus = authenticate(request, userId);
		if(authenticationStatus == true) {
			this.userService.deleteUser(userId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("User Deleted Successfully", true),HttpStatus.OK);
		}else if(adminAuthenticate(request) == true){
			this.userService.deleteUser(userId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("User Deleted Successfully By Admin",true),HttpStatus.OK);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are Unauthorized to Delete User", false),HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/")
	public ResponseEntity<List<UserDTO>> getAllUsers(){
		return ResponseEntity.ok(this.userService.getAllUsers());
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Integer userId){
		return ResponseEntity.ok(this.userService.getUserById(userId));
	}
	
	public boolean authenticate(HttpServletRequest request, Integer UserId) {
		String userToken = request.getHeader("Authorization");
		String username =null;
		System.out.println(userToken);
		if(userToken != null && userToken.startsWith("Bearer")) {
			String tokenSubString = userToken.substring(7);
			try {
				username = this.jwtTokenhelper.getUsernameFromToken(tokenSubString);
			}catch(IllegalArgumentException e){
				System.out.println("Unable toget JWT Token");
			}catch(ExpiredJwtException e) {
				System.out.println("JWT Token is expired");
			}catch(MalformedJwtException e) {
				System.out.println("Invalid JWt");
			}
		}else {
			System.out.println("Invalid Token Value");
		}
		if(username != null) {
			UserDTO userDto = this.userService.getUserById(UserId);
			String user = userDto.getEmail();
			if(username.equals(user)) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}	
	}
	
	
	public boolean adminAuthenticate(HttpServletRequest request) {
		String userToken = request.getHeader("Authorization");
		String username1 =null;
		System.out.println(userToken);
		if(userToken != null && userToken.startsWith("Bearer")) {
			String tokenSubString = userToken.substring(7);
			try {
				username1 = this.jwtTokenhelper.getUsernameFromToken(tokenSubString);
			}catch(IllegalArgumentException e){
				System.out.println("Unable toget JWT Token");
			}catch(ExpiredJwtException e) {
				System.out.println("JWT Token is expired");
			}catch(MalformedJwtException e) {
				System.out.println("Invalid JWt");
			}
		}else {
			System.out.println("Invalid Token Value");
		}
		User userDto = this.userRepository.findByEmail(username1).orElseThrow(()-> new ResourceNotFoundException("Email","Email Id ",0));
		Set<Role> userRoles = userDto.getRoles();
		Role role = this.roleRepository.getById(AppConstants.ROLE_ADMIN);
		Boolean roleAvailable = userRoles.contains(role);
		System.out.println(roleAvailable);
		if(roleAvailable == true) {
			return true;
		}else {
			return false;
		}
	}
}
