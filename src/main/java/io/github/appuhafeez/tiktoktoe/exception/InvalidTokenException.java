package io.github.appuhafeez.tiktoktoe.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvalidTokenException extends RuntimeException{

	private static final long serialVersionUID = 60621058376469855L;
	
	private String errorMessage;
	private HttpStatus httpStatus;

}
