package com.animo.jRest.model;

import lombok.Getter;
import lombok.Setter;
//TODO: Convert to record
@Getter @Setter
public class RequestProxy {

	private String url;
	private String username;
	private String password;
	private int port;

}
