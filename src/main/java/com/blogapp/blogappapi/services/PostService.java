package com.blogapp.blogappapi.services;

import java.util.List;

import com.blogapp.blogappapi.payloads.PostDTO;
import com.blogapp.blogappapi.payloads.PostResponse;

public interface PostService {

	PostDTO createPost(PostDTO postDto, Integer userId, Integer categoryId);
	
	PostDTO updatePost(PostDTO postDto, Integer postId);
	
	void deletePost(Integer postId);
	
	List<PostDTO> getAllPostsByPagination(Integer pageNumber, Integer pageSize);
	
	PostResponse getAllPostByPageResponse(Integer pageNumber, Integer pageSize);
	
	PostDTO getPostById(Integer postId);
	
	List<PostDTO> getPostsByCategory(Integer categoryId);
	
	List<PostDTO> getPostsByUser(Integer userId);
	
	List<PostDTO> getPostsByCategoryByPagination(Integer categoryId, Integer pageNumber, Integer pageSize);
	
	List<PostDTO> getPostsByUserByPagination(Integer userId, Integer pageNumber, Integer pageSize);
	
	PostResponse getPostsByCategoryByPageResponse(Integer categoryId, Integer pageNumber, Integer pageSize);
	
	PostResponse getPostsByUserByPageResponse(Integer userId, Integer pageNumber, Integer pageSize);
	
	List<PostDTO> searchPosts(String keyword);
	
	List<PostDTO> getAllPosts();
	
	List<PostDTO> getAllPostsByPaginationandSort(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	PostResponse getAllPostByPageResponseandSort(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	List<PostDTO> getPostsByCategoryByPaginationandSort(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	List<PostDTO> getPostsByUserByPaginationandSort(Integer userId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	PostResponse getPostsByCategoryByPageResponseandSort(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	PostResponse getPostsByUserByPageResponseandSort(Integer userId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
}
