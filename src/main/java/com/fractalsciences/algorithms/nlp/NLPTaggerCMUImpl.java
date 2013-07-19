package com.fractalsciences.algorithms.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;


public class NLPTaggerCMUImpl implements NLPTagger {

	static final String POS_CMU_NOUN = "N";
    static final String POS_CMU_VERB = "V";
    static final String POS_CMU_ADJECTIVE = "A";
    static final String POS_CMU_ADVERB = "R";
    static final String POS_CMU_INTERJECTION = "!";
    static final String POS_CMU_DETERMINER = "D";
    static final String POS_CMU_PREPOSITION = "P";
    static final String POS_CMU_COORDINATING_CONJUNCTION = "&";
    static final String POS_CMU_VERB_PARTICLE = "T";
    static final String POS_CMU_EXISTENTIAL = "X";
    static final String POS_CMU_EXISTENTIAL_PLUS_VERB = "Y";
    static final String POS_CMU_PRONOUN = "O";
    static final String POS_CMU_NOMINAL_POSSESSIVE = "S";
    static final String POS_CMU_PROPER_NOUN_POSSESSIVE = "Z";
    static final String POS_CMU_NOMINAL_VERB = "L";
    static final String POS_CMU_PROPER_NOUN_PLUS_VERB = "M";
    static final String POS_CMU_PROPER_NOUN = "^";
    static final String POS_CMU_HASHTAG = "#";
    static final String POS_CMU_AT_MENTION = "@";
    static final String POS_CMU_URL = "U";
    static final String POS_CMU_EMOTICONE = "E";
    static final String POS_CMU_DISCOURSE = "~";
    static final String POS_CMU_NUMERAL = "$";
    static final String POS_CMU_PUNCTUATION = ",";
    static final String POS_CMU_GARBAGE = "G";
    

	private static NLPTaggerCMUImpl nlpTaggerCMUImpl;
	private static HashMap<String, NLPPosType> posCombination;
	private Tagger tagger;
	private static final String modelFilename = "/cmu/arktweetnlp/model.20120919"; 

	public NLPTaggerCMUImpl() throws IOException{	
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		createPOSCombination();
	}

//	public static NLPTaggerCMUImpl getInstance() throws IOException{
//		if(nlpTaggerCMUImpl == null){
//			nlpTaggerCMUImpl = new NLPTaggerCMUImpl();
//		}
//		return nlpTaggerCMUImpl;
//	}

	@Override
	public List<String> tokenizeSentence(String sentence) {
		List<String> listTokens = new ArrayList<String>();
		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(sentence);
		for (TaggedToken token : taggedTokens) {
			listTokens.add(token.token);
		}
		return listTokens;
	}
	

	@Override
	public List<TaggedChunk> posTagSentence(String sentence) {
		List<TaggedChunk> taggedWords = new ArrayList<TaggedChunk>();
		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(sentence);
		for (TaggedToken token : taggedTokens) {
			TaggedChunk taggedWord = new TaggedChunk(token.token, token.tag);
			taggedWords.add(taggedWord);
		}
		return taggedWords;
	}
	

	@Override
	public void createPOSCombination() {
		HashMap<String, NLPPosType> posCombination = new HashMap<String, NLPPosType>();
		posCombination.put(POS_CMU_PROPER_NOUN, NLPPosType.PROPER_NOUNS_POS);
		//posCombination.put(POS_CMU_PROPER_NOUN_POSESSIVE, NLPPosType.PROPER_NOUNS_POS);
		posCombination.put(POS_CMU_AT_MENTION, NLPPosType.PROPER_NOUNS_POS);
		posCombination.put(POS_CMU_NOUN, NLPPosType.NOUNS_POS);
		//posCombination.put(POS_CMU_NOMINAL_POSSESSIVE, NLPPosType.NOUNS_POS);
		posCombination.put(POS_CMU_HASHTAG, NLPPosType.NOUNS_POS);
		posCombination.put(POS_CMU_ADJECTIVE, NLPPosType.ADJECTIVES_POS);
		posCombination.put(POS_CMU_VERB, NLPPosType.VERBS_POS);
		posCombination.put(POS_CMU_ADVERB, NLPPosType.ADVERBS_POS);
		posCombination.put(POS_CMU_INTERJECTION, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_DETERMINER, NLPPosType.DERTERMINER_POS);
		posCombination.put(POS_CMU_PREPOSITION, NLPPosType.PREPOSITION_POS);
		posCombination.put(POS_CMU_COORDINATING_CONJUNCTION, NLPPosType.CONJUNCTION_POS);
		posCombination.put(POS_CMU_VERB_PARTICLE, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_EXISTENTIAL, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_EXISTENTIAL_PLUS_VERB, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_PRONOUN, NLPPosType.PRONOUN_POS);
		posCombination.put(POS_CMU_NOMINAL_POSSESSIVE, NLPPosType.POSSESSIVE_POS);
		posCombination.put(POS_CMU_PROPER_NOUN_POSSESSIVE, NLPPosType.POSSESSIVE_POS);
		posCombination.put(POS_CMU_NOMINAL_VERB, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_PROPER_NOUN_PLUS_VERB, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_URL, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_EMOTICONE, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_DISCOURSE, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_NUMERAL, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_PUNCTUATION, NLPPosType.MISC_POS);
		posCombination.put(POS_CMU_GARBAGE, NLPPosType.MISC_POS);
		//posCombination.put(, NLPPosType.);
		this.posCombination = posCombination;
	}

	@Override
	public HashMap<String, NLPPosType> getPOSCombination() {
		return posCombination;
	}

}
