package com.fractalsciences.algorithms;

import com.fractalsciences.algorithms.nlp.NLPPosType;
import com.fractalsciences.algorithms.nlp.WordNetTest;

public class WordNetHelper {


	// Get Synonyms for a word
	public String[] getSynonymsWordNet(String word, NLPPosType posType){
		String[] listSynonyms = null;
		WordNetTest wordNet = WordNetTest.getInstance();
		if(posType.equals(NLPPosType.ADJECTIVES_POS)){
			listSynonyms = wordNet.getSynonyms(word, "ADJECTIVE");
		}
		else if(posType.equals(NLPPosType.ADVERBS_POS)){
			listSynonyms = wordNet.getSynonyms(word, "ADVERB");
		}
		else if(posType.equals(NLPPosType.NOUNS_POS)){
			listSynonyms = wordNet.getSynonyms(word, "NOUN");
		}
		else{
			listSynonyms = wordNet.getSynonyms(word, "VERB");
		}
		return listSynonyms;
	}

	// Get Synonyms for a word
	public String[] getLemmaWordNet(String word, NLPPosType posType){
		String[] listLemma = null;
		WordNetTest wordNet = WordNetTest.getInstance();
		if(posType.equals(NLPPosType.ADJECTIVES_POS)){
			listLemma = wordNet.getLemma(word, "ADJECTIVE");
		}
		else if(posType.equals(NLPPosType.ADVERBS_POS)){
			listLemma = wordNet.getLemma(word, "ADVERB");
		}
		else if(posType.equals(NLPPosType.NOUNS_POS)){
			listLemma = wordNet.getLemma(word, "NOUN");
		}
		else{
			listLemma = wordNet.getLemma(word, "VERB");
		}
		return listLemma;
	}
	
	// Get Synonyms for a word
	public String[] getSynonymsWordNet(String word, String posType){
		String[] listSynonyms = null;
		WordNetTest wordNet = WordNetTest.getInstance();
		listSynonyms = wordNet.getSynonyms(word, posType);
		return listSynonyms;
	}

		// Get Synonyms for a word
	public String[] getLemmaWordNet(String word, String posType){
		String[] listLemma = null;
		WordNetTest wordNet = WordNetTest.getInstance();
		listLemma = wordNet.getLemma(word, posType);
		return listLemma;
	}
}
