package com.blogapp.blogappapi.payloads;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.blogapp.blogappapi.entity.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {
	
	private Integer postId;
	
	private String postTitle;
	
	private String postContent;
	
	private String postImageName;
	
	private Date addedDate;
	
	private CategoryDTO category;
	
	private UserDTO user;
	
	private Set<CommentDTO> comments = new HashSet<>();

}
