package com.blogapp.blogappapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//loading user from database by username
		//this.userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("Email","Email Address is wrong",username));
		User user =this.userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("Email","Email Address"+username+"is Not Found",0));
		
		return user;
	}

}
