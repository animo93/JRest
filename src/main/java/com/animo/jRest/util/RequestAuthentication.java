package com.animo.jRest.util;

public class RequestAuthentication {
	
	private String username;
	private String password;
	private String encryptionAlgo;
	private String ecryptionKey;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEncryptionAlgo() {
		return encryptionAlgo;
	}
	public void setEncryptionAlgo(String encryptionAlgo) {
		this.encryptionAlgo = encryptionAlgo;
	}
	public String getEcryptionKey() {
		return ecryptionKey;
	}
	public void setEcryptionKey(String ecryptionKey) {
		this.ecryptionKey = ecryptionKey;
	}
	
	

}
