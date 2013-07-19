package com.fractalsciences.socialmedia.handlers;

import java.util.Date;
import java.util.Set;


public class TwitterData extends SocialData {

	public TwitterData(){
		super();
	}
	
	public TwitterData(String textContent, String userName, Date date,
			String searchTerm, Set<String> listWords) {
		super(textContent, userName, date,
		searchTerm, listWords); 
	}

	
}
