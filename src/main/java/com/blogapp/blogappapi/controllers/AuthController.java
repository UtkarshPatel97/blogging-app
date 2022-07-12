package com.blogapp.blogappapi.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blogappapi.exceptions.ApiException;
import com.blogapp.blogappapi.models.UserModel;
import com.blogapp.blogappapi.payloads.JwtAuthRequest;
import com.blogapp.blogappapi.payloads.JwtAuthResponse;
import com.blogapp.blogappapi.payloads.UserDTO;
import com.blogapp.blogappapi.security.JwtTokenHelper;
import com.blogapp.blogappapi.services.UserService;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

	@Autowired
	private JwtTokenHelper jwtTokenHelper;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest jwtAuthRequest) throws Exception{
		
		this.authenticate(jwtAuthRequest.getUsername(), jwtAuthRequest.getPassword());
		
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtAuthRequest.getUsername());
		
		String token = this.jwtTokenHelper.generateToken(userDetails);
		
		token = "Bearer "+token;
		
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		
		jwtAuthResponse.setToken(token);
		
		return new ResponseEntity<JwtAuthResponse>(jwtAuthResponse,HttpStatus.CREATED);
		
	}

	private void authenticate(String username, String password) throws Exception {
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,password);
		
		try{
			this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		}catch(BadCredentialsException e) {
			System.out.println("Invalid Details !!");
			throw new ApiException("Invalid Username or password");
		}
		
	}
	
	//register new user api
	@PostMapping("/register")
	public ResponseEntity<UserDTO> registerUser(@RequestBody UserModel userDto){
		UserDTO userDTO = this.modelMapper.map(userDto, UserDTO.class);
		UserDTO newUser = this.userService.registerNewUser(userDTO);
		return new ResponseEntity<UserDTO>(newUser,HttpStatus.CREATED);
	}
}
