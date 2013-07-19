package com.fractalsciences.algorithms.nlp;

import java.util.HashMap;
import java.util.List;

public interface NLPTagger {
	
	// Create Map POS Tagger
	public void createPOSCombination();
	
	public HashMap<String, NLPPosType> getPOSCombination();

	// Tokenize Sentence
	public List<String> tokenizeSentence(String sentence);
	
	// POS Tagging of Sentence
	public List<TaggedChunk> posTagSentence(String sentence);
}
