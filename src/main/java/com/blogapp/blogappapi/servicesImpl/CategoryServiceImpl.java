package com.blogapp.blogappapi.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogapp.blogappapi.entity.Category;
import com.blogapp.blogappapi.exceptions.ResourceNotFoundException;
import com.blogapp.blogappapi.payloads.CategoryDTO;
import com.blogapp.blogappapi.repository.CategoryRepository;
import com.blogapp.blogappapi.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDto) {
		Category category = this.modelMapper.map(categoryDto, Category.class);
	//	Category category = this.dtoToCategory(categoryDto);
		Category savedCategory = this.categoryRepository.save(category);
		return this.modelMapper.map(savedCategory, CategoryDTO.class);
	//	return this.CategorytoCategoryDTO(savedCategory);
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDto, Integer categoryId) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		category.setCategoryTitle(categoryDto.getCategoryTitle());
		category.setCategoryDescription(categoryDto.getCategoryDescription());
		Category updatedCategory = this.categoryRepository.save(category);
	//	return this.CategorytoCategoryDTO(updatedCategory);
		return this.modelMapper.map(updatedCategory, CategoryDTO.class);
	}

	@Override
	public CategoryDTO getCategoryById(Integer categoryId) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
	//	return this.CategorytoCategoryDTO(category);
		return this.modelMapper.map(category, CategoryDTO.class);
	}

	@Override
	public List<CategoryDTO> getAllCategories() {
		List<Category> categories = this.categoryRepository.findAll();
	//	List<CategoryDTO> categoryDtos = categories.stream().map(category -> this.CategorytoCategoryDTO(category)).collect(Collectors.toList());
		List<CategoryDTO> categoryDtos = categories.stream().map(category -> this.modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());
		return categoryDtos;
	}

	@Override
	public void deleteCategory(Integer categoryId) {
		Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","Category ID",categoryId));
		this.categoryRepository.delete(category);
	}
	/*
	private Category dtoToCategory(CategoryDTO categoryDto) {
		Category category = new Category();
		category.setCategoryTitle(categoryDto.getCategoryTitle());
		category.setCategoryDescription(categoryDto.getCategoryDescription());
		return category;
	}

	private CategoryDTO CategorytoCategoryDTO(Category category) {
		CategoryDTO categoryDto = new CategoryDTO();
		categoryDto.setCategoryTitle(category.getCategoryTitle());
		categoryDto.setCategoryDescription(category.getCategoryDescription());
		return categoryDto;
	}*/
}
