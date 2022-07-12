package com.blogapp.blogappapi.controllers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blogappapi.config.AppConstants;
import com.blogapp.blogappapi.entity.Comment;
import com.blogapp.blogappapi.entity.Post;
import com.blogapp.blogappapi.entity.Role;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.models.CommentModel;
import com.blogapp.blogappapi.payloads.ApiResponse;
import com.blogapp.blogappapi.payloads.CommentDTO;
import com.blogapp.blogappapi.payloads.UserDTO;
import com.blogapp.blogappapi.repository.CommentRepository;
import com.blogapp.blogappapi.repository.PostRepository;
import com.blogapp.blogappapi.repository.RoleRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.security.JwtTokenHelper;
import com.blogapp.blogappapi.services.CommentService;
import com.blogapp.blogappapi.services.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@RestController
@RequestMapping("/api/")
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private JwtTokenHelper jwtTokenhelper;
	
	@PostMapping("/user/{userId}/post/{postId}/comment/")
	public ResponseEntity<CommentDTO> createComment(@RequestBody CommentModel commentModel, 
													@PathVariable("userId") Integer userId, 
													@PathVariable("postId") Integer postId){
		CommentDTO commentDto = this.modelMapper.map(commentModel, CommentDTO.class);
		CommentDTO createCommentDto = this.commentService.createComment(commentDto, postId, userId);
		return new ResponseEntity<CommentDTO>(createCommentDto, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<ApiResponse> deleteComment(HttpServletRequest request,
													@PathVariable Integer commentId){
		Comment comment = this.commentRepository.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment","Comment ID:",commentId));
		int post_id = comment.getPost().getPostId();
		int commented_user = comment.getCommentedUser().getId();
		//this.commentService.deleteComment(commentId);
		//Post postDto = this.postRepository.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Post","Post ID:",postId));
		Boolean authenticationStatus = authenticate(request, commented_user);
		if(authenticationStatus == true) {
			this.commentService.deleteComment(commentId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Comment is Successfully deleted",true),HttpStatus.OK);
		}else if(postOwnerAuthenticate(request,post_id) == true) {
			this.commentService.deleteComment(commentId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Comment is Successfully deleted By Post Owner",true),HttpStatus.OK);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are Unauthorized to delete comment",true),HttpStatus.UNAUTHORIZED);
		}
	}
	
	

	@PutMapping("/comment/{commentId}")
	public ResponseEntity<CommentDTO> updateComment(@RequestBody CommentModel commentModel,
													@PathVariable Integer commentId){
		CommentDTO commentDto = this.modelMapper.map(commentModel, CommentDTO.class);
		CommentDTO updateCommentDTO = this.commentService.updateComment(commentDto, commentId);
		return new ResponseEntity<CommentDTO>(updateCommentDTO,HttpStatus.OK);
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
	
	
	private boolean postOwnerAuthenticate(HttpServletRequest request, Integer postId) {
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
		Post postDto = this.postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post","Post Id",postId));
		String userDto = postDto.getUser().getEmail();
		if(username.equals(userDto)) {
			return true;
		}else {
			return false;
		}
	}
}
