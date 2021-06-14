package io.github.appuhafeez.tiktoktoe.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(Include.NON_NULL)
public class ValidateResponse {

	private String username;
	
	private String email;
	
	private String errorMessage;
	
	private List<String> authorities;
	
	public ValidateResponse(String errMessage)
	{
		this.errorMessage = errMessage;
	}
}
