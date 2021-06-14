package io.github.appuhafeez.tiktoktoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
	
	private String errorMessage;
	
	private String token;
	
	private String username;
	
	public TokenRefreshResponse(String errMsg){
		this.errorMessage=errMsg;
	}
	
}
