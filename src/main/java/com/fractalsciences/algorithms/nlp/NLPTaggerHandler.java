package com.fractalsciences.algorithms.nlp;

import java.io.IOException;

public class NLPTaggerHandler {

	/**
	 * Select Tagger Type
	 * @param taggerType
	 * @return
	 * @throws IOException 
	 */
	public NLPTagger selectNLPTagger(NLPTaggerType taggerType) throws IOException{
		if(taggerType == NLPTaggerType.STANFORD_TAGGER){
			return NLPTaggerStanfordImpl.getInstance();
		}
		else if(taggerType == NLPTaggerType.CMU_TAGGER){
			return new NLPTaggerCMUImpl();
		}
		else{
			return null;
		}
	}
}
