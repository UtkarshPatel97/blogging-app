package com.blogapp.blogappapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blogappapi.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
