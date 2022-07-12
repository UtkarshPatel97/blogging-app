package com.blogapp.blogappapi.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserModel {

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
}
