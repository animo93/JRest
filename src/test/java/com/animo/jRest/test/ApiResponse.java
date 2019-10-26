package com.animo.jRest.test;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {
	
	@JsonProperty(value="message")
	private String message;
	@JsonProperty(value="documentation_url")
	private String documentation_url;
	
	

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDocumentation_url() {
		return documentation_url;
	}
	public void setDocumentation_url(String documentation_url) {
		this.documentation_url = documentation_url;
	}
	
	
	
}