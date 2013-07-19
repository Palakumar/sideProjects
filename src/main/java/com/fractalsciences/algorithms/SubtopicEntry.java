package com.fractalsciences.algorithms;

import java.util.HashSet;



public class SubtopicEntry {

	public String subtopic;
	public String subtopicPOS;
	//public HashSet<Integer> listIndex;
	
	
	
	public SubtopicEntry(String subtopic, String subtopicPOS) {
		this.subtopic = subtopic;
		this.subtopicPOS = subtopicPOS;
		//listIndex = new HashSet<Integer>();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subtopic == null) ? 0 : subtopic.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubtopicEntry other = (SubtopicEntry) obj;
		if (subtopic == null) {
			if (other.subtopic != null)
				return false;
		} else if (!subtopic.equals(other.subtopic))
			return false;
		return true;
	}
	
	
	
	
	
}
