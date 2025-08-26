package com.animo.jRest.model;

import lombok.Getter;
import lombok.Setter;
//TODO: Convert to record
@Getter @Setter
public class RequestAuthentication {

	private String username;
	private String password;
	private String encryptionAlgo;
	private String encryptionKey;
}
