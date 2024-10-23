package com.animo.jRest.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestProxy {

	private String url;
	private String username;
	private String password;
	private int port;

}
