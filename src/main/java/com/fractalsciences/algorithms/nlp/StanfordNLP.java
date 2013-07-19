package com.fractalsciences.algorithms.nlp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;


public class StanfordNLP {
	
	private static StanfordNLP stanfordNLP;
	StanfordCoreNLP pipeline;
	MaxentTagger stanfordPosTagger;
	
	static final String POS_PENN_TREEBANK_PROPER_NOUN = "NNP";
    static final String POS_PENN_TREEBANK_PROPER_NOUN_PLURAL = "NNPS";
    static final String POS_PENN_TREEBANK_NOUN = "NN";
    static final String POS_PENN_TREEBANK_NOUN_PLURAL = "NNS";
    static final String POS_PENN_TREEBANK_DETERMINER = "DT";
    static final String POS_PENN_TREEBANK_VERB = "VB";
    static final String POS_PENN_TREEBANK_VERB_PAST_TENSE = "VBD";
    static final String POS_PENN_TREEBANK_VERB_GERAND = "VBG";
    static final String POS_PENN_TREEBANK_VERB_PAST_PARTICIPLE = "VBN";
    static final String POS_PENN_TREEBANK_VERB_SINGULAR_PRESENT = "VBP";
    static final String POS_PENN_TREEBANK_VERB_THIRD_PERSON_PRESENT = "VBP";
    static final String POS_PENN_TREEBANK_ADJECTIVE = "JJ";
    static final String POS_PENN_TREEBANK_ADJECTIVE_COMPARATIVE = "JJR";
    static final String POS_PENN_TREEBANK_ADJECTIVE_SUPERLATIVE = "JJS";
    static final String POS_PENN_TREEBANK_ADVERB = "RB";
    static final String POS_PENN_TREEBANK_ADVERB_COMPARATIVE = "RBR";
    static final String POS_PENN_TREEBANK_ADVERB_SUPERLATIVE = "RBS";
    static final String POS_PENN_TREEBANK_COORDINATING_CONJUNCTION = "CC";
    static final String POS_PENN_TREEBANK_POSSESSIVE_ENDING = "POS";
    static final String POS_PENN_TREEBANK_PRONOUN = "PN";
    
    public static final HashMap<String, String> mapGeneralPos = new HashMap<String, String>();
	
	private StanfordNLP(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);
		createMapGeneralPOS();
	}
	
	private StanfordNLP(String modelFileName){
		stanfordPosTagger = new MaxentTagger(modelFileName);
		createMapGeneralPOS();
	}
	
	 
	
	/**
	 * Single Instance of Stanford CoreNLP 
	 * @return
	 */
	public static StanfordNLP getInstance(){
		if(stanfordNLP == null){
			stanfordNLP = new StanfordNLP();
		}
		return stanfordNLP;
	}
	
	/**
	 * Single Instance of Stanford MaxentTagger 
	 * @return
	 */
	public static StanfordNLP getInstanceMaxentTagger(){
		if(stanfordNLP == null){
			stanfordNLP = new StanfordNLP("models/left3words-wsj-0-18.tagger");
		}
		return stanfordNLP;
	}
	
	public void createMapGeneralPOS(){
		mapGeneralPos.put(POS_PENN_TREEBANK_PROPER_NOUN ,"POS_PROPER_NOUN_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_PROPER_NOUN_PLURAL, "POS_PROPER_NOUN_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_NOUN, "POS_NOUN_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_NOUN_PLURAL, "POS_NOUN_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB_PAST_TENSE, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB_GERAND, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB_PAST_PARTICIPLE, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB_SINGULAR_PRESENT, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_VERB_THIRD_PERSON_PRESENT, "POS_VERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADJECTIVE, "POS_ADJECTIVE_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADJECTIVE_COMPARATIVE, "POS_ADJECTIVE_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADJECTIVE_SUPERLATIVE, "POS_ADJECTIVE_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADVERB, "POS_ADVERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADVERB_COMPARATIVE, "POS_ADVERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_ADVERB_SUPERLATIVE, "POS_ADVERB_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_COORDINATING_CONJUNCTION, "POS_CONJUNCTION_TAGS");
	    mapGeneralPos.put(POS_PENN_TREEBANK_POSSESSIVE_ENDING, "POS_POSSESSIVE_TAGS");
	}
	
	/**
	 * Tokenize a string.
	 * @param text
	 * @return
	 */
	public List<String> tokenizeString(String text){
		List<String> listTokens = new ArrayList<String>();

		Properties props = new Properties();
		props.put("annotators", "tokenize");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				listTokens.add(token.get(TextAnnotation.class));
			}
		}
		
		return listTokens;
	}
	
	
	/**
	 * Get POS tags for a String.
	 * @param text
	 * @return
	 */
	public List<String> posString(String text){
		List<String> listPOSTags = new ArrayList<String>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				listPOSTags.add(token.get(PartOfSpeechAnnotation.class));
			}
		}
		
		return listPOSTags;
	}
	
	/**
	 * Get the NER of string.
	 * @param text
	 * @return
	 */
	public List<String> nerString(String text){
		List<String> listNER = new ArrayList<String>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				
				listNER.add(token.get(NamedEntityTagAnnotation.class));
			}
		}
		
		return listNER;
	}
	
	
	public Map<NLPToolsType, List<String>> nlpPOSNER(String text){
		Map<NLPToolsType, List<String>> nlpParamsMap = new HashMap<NLPToolsType, List<String>>();
		List<String> listTokens = new ArrayList<String>();
		List<String> listPOSTags = new ArrayList<String>();
		List<String> listNER = new ArrayList<String>();
		
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				listTokens.add(token.get(TextAnnotation.class));
				listPOSTags.add(token.get(PartOfSpeechAnnotation.class));
				listNER.add(token.get(NamedEntityTagAnnotation.class));
			}
		}
		nlpParamsMap.put(NLPToolsType.TOKENS, listTokens);
		nlpParamsMap.put(NLPToolsType.POS_TAGS, listPOSTags);
		nlpParamsMap.put(NLPToolsType.NER, listNER);
		
		return nlpParamsMap;
	}

	
	public Map<NLPToolsType, List<String>> nlpPOS(String text){
		Map<NLPToolsType, List<String>> nlpParamsMap = new HashMap<NLPToolsType, List<String>>();
		List<String> listTokens = new ArrayList<String>();
		List<String> listPOSTags = new ArrayList<String>();
		
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				listTokens.add(token.get(TextAnnotation.class));
				listPOSTags.add(token.get(PartOfSpeechAnnotation.class));
			}
		}
		nlpParamsMap.put(NLPToolsType.TOKENS, listTokens);
		nlpParamsMap.put(NLPToolsType.POS_TAGS, listPOSTags);
		
		return nlpParamsMap;
	}
	
	
	
	public List<TaggedWord> nlpTaggedWords(String text){
		List<TaggedWord> nlpTaggedWords = new ArrayList<TaggedWord>();
		
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				TaggedWord taggedWord = new TaggedWord(token.get(TextAnnotation.class), token.get(PartOfSpeechAnnotation.class));
				nlpTaggedWords.add(taggedWord);
			}
		}
		return nlpTaggedWords;
	}
	
}
