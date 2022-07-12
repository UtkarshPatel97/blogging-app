package com.blogapp.blogappapi.payloads;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

	private int commentId;
	
	private String commentContent;
	
	private Date commentedDate;
	
//	private PostDTO post;
	
	private UserDTO commentedUser;
	

}
