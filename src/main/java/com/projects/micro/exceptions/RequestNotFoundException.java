package com.projects.micro.exceptions;

public class RequestNotFoundException extends Exception{
	
	public RequestNotFoundException(){
		super("Request Not Found!");
	}

}
