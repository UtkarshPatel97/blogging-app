package com.blogapp.blogappapi.services;

import com.blogapp.blogappapi.payloads.CommentDTO;

public interface CommentService {

	CommentDTO createComment(CommentDTO commentDto, Integer postId, Integer userId);
	
	CommentDTO updateComment(CommentDTO commentDto, Integer commentId);
	
	void deleteComment(Integer commentId);
	
}
