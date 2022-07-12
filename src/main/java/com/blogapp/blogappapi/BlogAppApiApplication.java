package com.blogapp.blogappapi;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blogapp.blogappapi.config.AppConstants;
import com.blogapp.blogappapi.entity.Role;
import com.blogapp.blogappapi.repository.RoleRepository;

@SpringBootApplication
public class BlogAppApiApplication implements CommandLineRunner{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(BlogAppApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		try {
			Role role = new Role();
			role.setRoleId(AppConstants.ROLE_ADMIN);
			role.setRoleName("ROLE_ADMIN");
			Role role1 = new Role();
			role1.setRoleId(AppConstants.ROLE_USER);
			role1.setRoleName("ROLE_NORMAL");
			List<Role> roles=List.of(role,role1);
			List<Role> result =this.roleRepository.saveAll(roles);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
