package com.blogapp.blogappapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blogappapi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
