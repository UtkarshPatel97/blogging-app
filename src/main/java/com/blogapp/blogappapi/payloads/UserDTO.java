package com.blogapp.blogappapi.payloads;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

	private int id;
	
	@NotEmpty
	@Size(min = 3, message="Username must be min of 3 characters")
	private String name;
	
	@Email(message = "Email Address is not Valid !!")
	private String email;
	
	@NotEmpty
//	@Pattern(regexp="")
	@Size(min = 6, max = 12, message="Password must be min of 6 chars & max of 12 chars")
	private String password;
	
	@NotEmpty
	private String about;
	
	private Set<RoleDTO> roles = new HashSet<>();
}
