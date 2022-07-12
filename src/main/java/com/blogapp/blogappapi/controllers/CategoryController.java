package com.blogapp.blogappapi.controllers;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blogappapi.models.CategoryModel;
import com.blogapp.blogappapi.payloads.ApiResponse;
import com.blogapp.blogappapi.payloads.CategoryDTO;
import com.blogapp.blogappapi.services.CategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/")
	public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryModel categoryModel){
		CategoryDTO categoryDto = this.modelMapper.map(categoryModel, CategoryDTO.class);
		CategoryDTO addCategoryDto = this.categoryService.createCategory(categoryDto);
		return new ResponseEntity<CategoryDTO>(addCategoryDto,HttpStatus.CREATED);
	}
	
	@PutMapping("/{categoryID}")
	public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryModel categoryModel, @PathVariable Integer categoryID){
		CategoryDTO categoryDto = this.modelMapper.map(categoryModel, CategoryDTO.class);
		CategoryDTO updateCategoryDto = this.categoryService.updateCategory(categoryDto,categoryID);
		return new ResponseEntity<CategoryDTO>(updateCategoryDto,HttpStatus.OK);
	}
	
	@GetMapping("/")
	public ResponseEntity<List<CategoryDTO>> getAllCategories(){
		return ResponseEntity.ok(this.categoryService.getAllCategories());
	}
	
	@GetMapping("/{categoryID}")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable Integer categoryID){
		return ResponseEntity.ok(this.categoryService.getCategoryById(categoryID));
	}
	
	@DeleteMapping("/{categoryID}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer categoryID){
		this.categoryService.deleteCategory(categoryID);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Category Deleted Successfully", true),HttpStatus.OK);
	}
}
