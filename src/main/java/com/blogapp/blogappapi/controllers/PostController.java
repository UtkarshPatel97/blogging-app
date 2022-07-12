package com.blogapp.blogappapi.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.engine.jdbc.StreamUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blogapp.blogappapi.config.AppConstants;
import com.blogapp.blogappapi.entity.Role;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.models.PostModel;
import com.blogapp.blogappapi.payloads.ApiResponse;
import com.blogapp.blogappapi.payloads.PostDTO;
import com.blogapp.blogappapi.payloads.PostResponse;
import com.blogapp.blogappapi.payloads.UserDTO;
import com.blogapp.blogappapi.repository.RoleRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.security.JwtTokenHelper;
import com.blogapp.blogappapi.services.FileService;
import com.blogapp.blogappapi.services.PostService;
import com.blogapp.blogappapi.services.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@RestController
@RequestMapping("/api/")
public class PostController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private JwtTokenHelper jwtTokenhelper;
	
	@Value("${project.image}")
	private String path;
	
	@PostMapping("/user/{userId}/category/{categoryId}/posts/")
	public ResponseEntity<?> newPost(HttpServletRequest request,
											@RequestBody PostModel postModel, 
											@PathVariable int userId, 
											@PathVariable int categoryId){
		PostDTO postDto = this.modelMapper.map(postModel, PostDTO.class);
		Boolean authenticationStatus = authenticate(request, userId);
		if(authenticationStatus==true){
			PostDTO createPost = this.postService.createPost(postDto, userId, categoryId);
			return new ResponseEntity<PostDTO>(createPost,HttpStatus.CREATED);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are not Allowed to create post under other user!", false),HttpStatus.FORBIDDEN);
		}
	}
	
/*	@PostMapping("/user/{userId}/category/{categoryId}/posts/")
	public ResponseEntity<?> newPost(@RequestBody PostModel postModel, 
											@PathVariable int userId, 
											@PathVariable int categoryId){
		PostDTO postDto = this.modelMapper.map(postModel, PostDTO.class);
		PostDTO createPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<PostDTO>(createPost,HttpStatus.CREATED);
	}*/
	
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDTO>> getPostsByUser(@PathVariable Integer userId){
		List<PostDTO> postsByUser = this.postService.getPostsByUser(userId);
		return new ResponseEntity<List<PostDTO>>(postsByUser, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}/posts")
	public ResponseEntity<List<PostDTO>> getPostsByCategory(@PathVariable Integer categoryId){
		List<PostDTO> postsByCategory = this.postService.getPostsByCategory(categoryId);
		return new ResponseEntity<List<PostDTO>>(postsByCategory, HttpStatus.OK);
	}
	
	@GetMapping("/user/{userId}/postsbypagination")
	public ResponseEntity<List<PostDTO>> getPostsByUserByPagination(@PathVariable Integer userId, 
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		List<PostDTO> postsByUser = this.postService.getPostsByUserByPagination(userId, pageNumber, pageSize);
		return new ResponseEntity<List<PostDTO>>(postsByUser, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}/postsbypagination")
	public ResponseEntity<List<PostDTO>> getPostsByCategoryByPagination(@PathVariable Integer categoryId,
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		List<PostDTO> postsByCategory = this.postService.getPostsByCategoryByPagination(categoryId, pageNumber, pageSize);
		return new ResponseEntity<List<PostDTO>>(postsByCategory, HttpStatus.OK);
	}
	
	@GetMapping("/user/{userId}/postsbypageresponse")
	public ResponseEntity<PostResponse> getPostsByUserByPageResponse(@PathVariable Integer userId, 
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		PostResponse postsByUser = this.postService.getPostsByUserByPageResponse(userId, pageNumber, pageSize);
		return new ResponseEntity<PostResponse>(postsByUser, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}/postsbypageresponse")
	public ResponseEntity<PostResponse> getPostsByCategoryByPageResponse(@PathVariable Integer categoryId,
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		PostResponse postsByCategory = this.postService.getPostsByCategoryByPageResponse(categoryId, pageNumber, pageSize);
		return new ResponseEntity<PostResponse>(postsByCategory, HttpStatus.OK);
	}
	
	@GetMapping("/postsbypage")
	public ResponseEntity<List<PostDTO>> getAllPostsByPage(@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
													@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		List<PostDTO> allPosts = this.postService.getAllPostsByPagination(pageNumber,pageSize);
		return new ResponseEntity<List<PostDTO>>(allPosts,HttpStatus.OK);
	}
	
	@GetMapping("/postsbypageresponse")
	public ResponseEntity<PostResponse> getAllPostByPostResponse(@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
													@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize){
		PostResponse postResponse = this.postService.getAllPostByPageResponse(pageNumber, pageSize);
		return new ResponseEntity<PostResponse>(postResponse,HttpStatus.OK);
	}
	
	// Sort Post Methods
	
	@GetMapping("/user/{userId}/postsbypaginationSort")
	public ResponseEntity<List<PostDTO>> getPostsByUserByPaginationSort(@PathVariable Integer userId, 
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
												@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
												@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		List<PostDTO> postsByUser = this.postService.getPostsByUserByPaginationandSort(userId, pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<List<PostDTO>>(postsByUser, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}/postsbypaginationSort")
	public ResponseEntity<List<PostDTO>> getPostsByCategoryByPaginationSort(@PathVariable Integer categoryId,
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
												@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
												@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		List<PostDTO> postsByCategory = this.postService.getPostsByCategoryByPaginationandSort(categoryId, pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<List<PostDTO>>(postsByCategory, HttpStatus.OK);
	}
	
	@GetMapping("/user/{userId}/postsbypageresponseSort")
	public ResponseEntity<PostResponse> getPostsByUserByPageResponseSort(@PathVariable Integer userId, 
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
												@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
												@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		PostResponse postsByUser = this.postService.getPostsByUserByPageResponseandSort(userId, pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postsByUser, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}/postsbypageresponseSort")
	public ResponseEntity<PostResponse> getPostsByCategoryByPageResponseSort(@PathVariable Integer categoryId,
												@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
												@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
												@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
												@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		PostResponse postsByCategory = this.postService.getPostsByCategoryByPageResponseandSort(categoryId, pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postsByCategory, HttpStatus.OK);
	}
	
	@GetMapping("/postsbypageSort")
	public ResponseEntity<List<PostDTO>> getAllPostsByPageSort(@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
													@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
													@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
													@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		List<PostDTO> allPosts = this.postService.getAllPostsByPaginationandSort(pageNumber,pageSize, sortBy, sortDir);
		return new ResponseEntity<List<PostDTO>>(allPosts,HttpStatus.OK);
	}
	
	@GetMapping("/postsbypageresponseSort")
	public ResponseEntity<PostResponse> getAllPostByPostResponseSort(@RequestParam(value="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=true) Integer pageNumber, 
													@RequestParam(value="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=true) Integer pageSize,
													@RequestParam(value="sortBy", defaultValue=AppConstants.SORT_BY, required=true) String sortBy,
													@RequestParam(value="sortDir", defaultValue=AppConstants.SORT_DIR, required=false) String sortDir){
		PostResponse postResponse = this.postService.getAllPostByPageResponseandSort(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse,HttpStatus.OK);
	}
	
	//Posts Method
	@GetMapping("posts")
	public ResponseEntity<List<PostDTO>> getAllPosts(){
		List<PostDTO> allPosts = this.postService.getAllPosts();
		return new ResponseEntity<List<PostDTO>>(allPosts,HttpStatus.OK);
	}
	
	@GetMapping("/posts/{postId}")
	public ResponseEntity<PostDTO> getPostByID(@PathVariable Integer postId){
		PostDTO postDTO = this.postService.getPostById(postId);
		return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
	}
	
	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<ApiResponse> deletePost(HttpServletRequest request,@PathVariable Integer postId) {
		PostDTO postDto = this.postService.getPostById(postId);
		Boolean authenticationStatus = authenticate(request, postDto.getUser().getId());
		if(authenticationStatus == true) {
			this.postService.deletePost(postId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Post is successfully deleted",true),HttpStatus.OK);
		}else if (adminAuthenticate(request) == true) {
			this.postService.deletePost(postId);
			return new ResponseEntity<ApiResponse>(new ApiResponse("Post is successfully deleted By Admin",true),HttpStatus.OK);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are Unauthorized to delete this post",false),HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PutMapping("/posts/{postId}")
	public ResponseEntity<?> updatePost(HttpServletRequest request,@RequestBody PostModel postModel, @PathVariable Integer postId) {
		PostDTO postDto = this.modelMapper.map(postModel, PostDTO.class);
		Boolean authenticationStatus = authenticate(request,postDto.getUser().getId());
		if(authenticationStatus == true) {
			PostDTO updatePostDto = this.postService.updatePost(postDto, postId);
			return new ResponseEntity<PostDTO>(updatePostDto, HttpStatus.OK);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are Unauthorized to Update this post",false),HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	//search method
	@GetMapping("/posts/search/{keyword}")
	public ResponseEntity<List<PostDTO>> searchPostsByTitle(@PathVariable("keyword") String keyword){
		List<PostDTO> postDtos = this.postService.searchPosts(keyword);
		return new ResponseEntity<List<PostDTO>>(postDtos,HttpStatus.OK);
		
	}
	
	//POST IMAGE UPLOAD
	@PostMapping("/post/image/upload/{postId}")
	public ResponseEntity<?> uploadPostImage(HttpServletRequest request,
									@RequestParam("image") MultipartFile image, 
									@PathVariable Integer postId) throws IOException{
		PostDTO postDto = this.postService.getPostById(postId);
		Boolean authenticationStatus = authenticate(request,postDto.getUser().getId());
		if(authenticationStatus == true) {
			String fileName = this.fileService.uploadImage(path, image);
			postDto.setPostImageName(fileName);
			PostDTO updatePost = this.postService.updatePost(postDto, postId);
			return new ResponseEntity<PostDTO>(updatePost, HttpStatus.OK);
		}else {
			return new ResponseEntity<ApiResponse>(new ApiResponse("You are Unauthorized to Update this post",false),HttpStatus.UNAUTHORIZED);
		}
	}
	
	//method to serve files
	@GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable("imageName") String imageName, 
			HttpServletResponse response) throws IOException{
		
			InputStream resource = this.fileService.getResource(path, imageName);
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			StreamUtils.copy(resource, response.getOutputStream());
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
}
