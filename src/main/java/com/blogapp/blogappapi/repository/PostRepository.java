package com.blogapp.blogappapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.blogapp.blogappapi.entity.Category;
import com.blogapp.blogappapi.entity.Post;
import com.blogapp.blogappapi.entity.User;

public interface PostRepository extends JpaRepository<Post, Integer>,PagingAndSortingRepository<Post, Integer> {

	List<Post> findByUser(User user);
	
	List<Post> findByCategory(Category category);
	
	Page<Post> findAllPostsByUser(User user, Pageable pageable);
	
	Page<Post> findAllPostsByCategory(Category category, Pageable pageable);
	
	@Query("select p from Post p where p.postTitle like :key")
	List<Post> searchBypostTitle(@Param("key")String postTitle);
}
