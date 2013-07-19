package com.fractalsciences.algorithms.nlp;

import java.util.List;

public class TaggedChunk {

	// List of tokens in a sentence
	public String token;
	
	// List of POS Tags of a sentence
	public String posTag;

	
	
	public TaggedChunk(String token, String posTag) {
		this.token = token;
		this.posTag = posTag;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
	
	
}
