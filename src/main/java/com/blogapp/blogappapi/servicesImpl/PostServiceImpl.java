package com.blogapp.blogappapi.servicesImpl;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blogapp.blogappapi.entity.Category;
import com.blogapp.blogappapi.entity.Post;
import com.blogapp.blogappapi.entity.User;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.payloads.PostDTO;
import com.blogapp.blogappapi.payloads.PostResponse;
import com.blogapp.blogappapi.repository.CategoryRepository;
import com.blogapp.blogappapi.repository.PostRepository;
import com.blogapp.blogappapi.repository.UserRepository;
import com.blogapp.blogappapi.services.PostService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public PostDTO createPost(PostDTO postDto, Integer userId, Integer categoryId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","UserId",userId));
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",categoryId));
		Post post = this.modelMapper.map(postDto, Post.class);
		post.setPostImageName("default.png");
		post.setAddedDate(new Date());
		post.setUser(user);
		post.setCategory(category);
		Post savedPost = this.postRepository.save(post);
		return this.modelMapper.map(savedPost, PostDTO.class);
	}

	@Override
	public PostDTO updatePost(PostDTO postDto, Integer postId) {
		Post post = this.postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post ID", postId));
		post.setPostTitle(postDto.getPostTitle());
		post.setPostContent(postDto.getPostContent());
		post.setPostImageName(postDto.getPostImageName());
		Post savedPost = this.postRepository.save(post);
		return this.modelMapper.map(savedPost, PostDTO.class);
	}

	@Override
	public void deletePost(Integer postId) {
		Post post = this.postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post ID", postId));
		this.postRepository.delete(post);
	}

	@Override
	public List<PostDTO> getAllPostsByPagination(Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> pagePosts = this.postRepository.findAll(pageable);
		List<Post> posts = pagePosts.getContent();
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public PostDTO getPostById(Integer postId) {
		Post post = this.postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post ID", postId));
		return this.modelMapper.map(post, PostDTO.class);
	}

	@Override
	public List<PostDTO> getPostsByCategory(Integer categoryId) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID",categoryId));
		List<Post> posts = this.postRepository.findByCategory(category);
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDTO> getPostsByUser(Integer userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		List<Post> posts = this.postRepository.findByUser(user);
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDTO> searchPosts(String keyword) {
		List<Post> posts = this.postRepository.searchBypostTitle("%"+keyword+"%");
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDTO> getAllPosts() {
		List<Post> posts = this.postRepository.findAll();
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public PostResponse getAllPostByPageResponse(Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> pagePosts = this.postRepository.findAll(pageable);
		List<Post> posts = pagePosts.getContent();
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
		
	}

	@Override
	public List<PostDTO> getPostsByCategoryByPagination(Integer categoryId, Integer pageNumber, Integer pageSize) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> posts = this.postRepository.findAllPostsByCategory(category, pageable);
		List<Post> allposts = posts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDTO> getPostsByUserByPagination(Integer userId, Integer pageNumber, Integer pageSize) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> pagePosts = this.postRepository.findAllPostsByUser(user, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public PostResponse getPostsByCategoryByPageResponse(Integer categoryId, Integer pageNumber, Integer pageSize) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> pagePosts = this.postRepository.findAllPostsByCategory(category, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
	}

	@Override
	public PostResponse getPostsByUserByPageResponse(Integer userId, Integer pageNumber, Integer pageSize) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
		Page<Post> pagePosts = this.postRepository.findAllPostsByUser(user, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
	}

	// posts sort different methods
	@Override
	public List<PostDTO> getAllPostsByPaginationandSort(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
		//sort by ternary operations
				Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
		/*		if(sortDir.equalsIgnoreCase("asc")) {
					sort = sort.by(sortBy).ascending();
				}else {
					sort = sort.by(sortBy).descending();
				}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> pagePosts = this.postRepository.findAll(pageable);
		List<Post> posts = pagePosts.getContent();
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public PostResponse getAllPostByPageResponseandSort(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
		//sort by ternary operations
				Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
		/*		if(sortDir.equalsIgnoreCase("asc")) {
					sort = sort.by(sortBy).ascending();
				}else {
					sort = sort.by(sortBy).descending();
				}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> pagePosts = this.postRepository.findAll(pageable);
		List<Post> posts = pagePosts.getContent();
		List<PostDTO> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
	}

	@Override
	public List<PostDTO> getPostsByCategoryByPaginationandSort(Integer categoryId, Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		//sort by ternary operations
				Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
		/*		if(sortDir.equalsIgnoreCase("asc")) {
					sort = sort.by(sortBy).ascending();
				}else {
					sort = sort.by(sortBy).descending();
				}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> posts = this.postRepository.findAllPostsByCategory(category, pageable);
		List<Post> allposts = posts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDTO> getPostsByUserByPaginationandSort(Integer userId, Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		//sort by ternary operations
				Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
		/*		if(sortDir.equalsIgnoreCase("asc")) {
					sort = sort.by(sortBy).ascending();
				}else {
					sort = sort.by(sortBy).descending();
				}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> pagePosts = this.postRepository.findAllPostsByUser(user, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public PostResponse getPostsByCategoryByPageResponseandSort(Integer categoryId, Integer pageNumber,
			Integer pageSize, String sortBy, String sortDir) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		//sort by ternary operations
				Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
		/*		if(sortDir.equalsIgnoreCase("asc")) {
					sort = sort.by(sortBy).ascending();
				}else {
					sort = sort.by(sortBy).descending();
				}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> pagePosts = this.postRepository.findAllPostsByCategory(category, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
	}

	@Override
	public PostResponse getPostsByUserByPageResponseandSort(Integer userId, Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","User ID",userId));
		//sort by ternary operations
		Sort sort = (sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
/*		if(sortDir.equalsIgnoreCase("asc")) {
			sort = sort.by(sortBy).ascending();
		}else {
			sort = sort.by(sortBy).descending();
		}*/
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,sort);
		Page<Post> pagePosts = this.postRepository.findAllPostsByUser(user, pageable);
		List<Post> allposts = pagePosts.getContent();
		List<PostDTO> postDtos = allposts.stream().map((post) -> this.modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePosts.getNumber()+1);
		postResponse.setPageSize(pagePosts.getSize());
		postResponse.setTotalElements(pagePosts.getTotalElements());
		postResponse.setTotalPages(pagePosts.getTotalPages());
		postResponse.setLastPage(pagePosts.isLast());
		return postResponse;
	}

}
