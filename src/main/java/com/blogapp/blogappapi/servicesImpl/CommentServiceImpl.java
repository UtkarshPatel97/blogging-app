package com.blogapp.blogappapi.servicesImpl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blogapp.blogappapi.entity.Comment;
import com.blogapp.blogappapi.entity.Post;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.payloads.CommentDTO;
import com.blogapp.blogappapi.repository.CommentRepository;
import com.blogapp.blogappapi.repository.PostRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.services.CommentService;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CommentDTO createComment(CommentDTO commentDto, Integer postId, Integer userId) {
		Post post = this.postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post ID:", postId));
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "User ID:", userId));
		Comment comment = this.modelMapper.map(commentDto, Comment.class);
		comment.setPost(post);
		comment.setCommentedUser(user);
		comment.setCommentedDate(new Date());
		Comment saveComment = this.commentRepository.save(comment);
		return this.modelMapper.map(saveComment, CommentDTO.class);
	}

	@Override
	public void deleteComment(Integer commentId) {
		Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment ID:", commentId));
		this.commentRepository.delete(comment);
	}

	@Override
	public CommentDTO updateComment(CommentDTO commentDto, Integer commentId) {
		Comment comment = this.commentRepository.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment","Comment ID: ", commentId));
		comment.setCommentContent(commentDto.getCommentContent());
		comment.setCommentedDate(new Date());
		Comment updatedComment = this.commentRepository.save(comment);
		return this.modelMapper.map(updatedComment, CommentDTO.class);
	}
/*
	@Override
	public List<CommentDTO> getAllCommentsByUser(Integer userId, Integer pageNumber, Integer pageSize) {
		User user = this.userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","User ID: ",userId));
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Comment> pageComments = this.commentRepository.findAllCommentsByUser(user, pageable);
		List<Comment> allcomments = pageComments.getContent();
		List<CommentDTO> commentDtos =allcomments.stream().map((comment)-> this.modelMapper.map(comment, CommentDTO.class)).collect(Collectors.toList());
		return commentDtos;
	}*/

}
