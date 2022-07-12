package com.blogapp.blogappapi.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
	
	String resourceName;
	String fieldName;
	long fieldValue;
	static String fieldValueString;
	
	public ResourceNotFoundException(String resourceName, String fieldName, long fieldValue) {
		super(String.format("%s not found with %s : %s", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	public ResourceNotFoundException(String resourceName, String fieldName, String username) {
		// TODO Auto-generated constructor stub
		super(String.format("%s not found with %s : %s", resourceName, fieldName, fieldValueString));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValueString = fieldValueString;
	}
	
	

}
