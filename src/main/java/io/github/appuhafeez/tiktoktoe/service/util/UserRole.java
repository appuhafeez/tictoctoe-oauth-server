/**
 * 
 */
package io.github.appuhafeez.tiktoktoe.service.util;


public enum UserRole {
	
	USER_ROLE("ROLE_USER"), ADMIN_ROLE("ROLE_ADMIN"), OPERATOR_ROLE("ROLE_OPERATOR");
	
	private String role;

	UserRole(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
	
	public static boolean isValidRole(String role) {
		for (UserRole type : UserRole.values()) {
	        if (type.getRole().equals(role)) {
	            return true;
	        }
	    }
		return false;
	}
}
