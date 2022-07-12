package com.blogapp.blogappapi.services;

import java.util.List;

import com.blogapp.blogappapi.payloads.CategoryDTO;

public interface CategoryService {

	CategoryDTO createCategory(CategoryDTO categoryDto);
	
	CategoryDTO updateCategory(CategoryDTO categoryDto, Integer categoryId);
	
	CategoryDTO getCategoryById(Integer categoryId);
	
	List<CategoryDTO> getAllCategories();
	
	void deleteCategory(Integer categoryId);
}
