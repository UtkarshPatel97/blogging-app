package com.blogapp.blogappapi.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryModel {

	@NotEmpty
	@Size(min=3, message="Title must have min 3 characters")
	private String categoryTitle;
	
	@NotEmpty
	@Size(min=3, max=120, message="Description must be have min 3 chars and max 120 chars")
	private String categoryDescription;
}
