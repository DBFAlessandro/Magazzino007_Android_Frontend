package com.amsoftware.testrestapplication.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse implements Serializable
{
	private String authenticationResponse;
    private boolean status;

	public LoginResponse() 
	{
	
	}

	public LoginResponse(String response) 
	{
		this.authenticationResponse = response;
	}

	@JsonProperty("id_token")
	public String getIdToken() 
	{
		return authenticationResponse;
	}

	public void setIdToken(String idToken) 
	{
		this.authenticationResponse = idToken;
	}
    public boolean getStatus()
	{
		return status;
	}
	public void setStatus(boolean status)
	{
		this.status = status;
	}
}
