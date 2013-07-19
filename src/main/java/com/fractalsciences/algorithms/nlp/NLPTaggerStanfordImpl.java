package com.fractalsciences.algorithms.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NLPTaggerStanfordImpl implements NLPTagger{

	private static NLPTaggerStanfordImpl nlpTaggerStanfordImpl;
	
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
    
    private static HashMap<String, NLPPosType> posCombination;
    private StanfordCoreNLP pipeline;
	
	private NLPTaggerStanfordImpl(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);
		createPOSCombination();
	}
	
	public static NLPTaggerStanfordImpl getInstance(){
		if(nlpTaggerStanfordImpl == null){
			nlpTaggerStanfordImpl = new NLPTaggerStanfordImpl();
		}
		return nlpTaggerStanfordImpl;
	}
	
	
	@Override
	public List<String> tokenizeSentence(String text) {
		List<String> listTokens = new ArrayList<String>();
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

	
	@Override
	public List<TaggedChunk> posTagSentence(String text) {
		List<TaggedChunk> taggedWords = new ArrayList<TaggedChunk>();
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				TaggedChunk taggedWord = new TaggedChunk(token.get(TextAnnotation.class), token.get(PartOfSpeechAnnotation.class));
				taggedWords.add(taggedWord);
			}
		}
		return taggedWords;
	}
	
	
	@Override
	public void createPOSCombination() {
		HashMap<String, NLPPosType> posCombination = new HashMap<String, NLPPosType>();
		posCombination.put(POS_PENN_TREEBANK_PROPER_NOUN , NLPPosType.PROPER_NOUNS_POS);
		posCombination.put(POS_PENN_TREEBANK_PROPER_NOUN_PLURAL, NLPPosType.PROPER_NOUNS_POS);
		posCombination.put(POS_PENN_TREEBANK_NOUN, NLPPosType.NOUNS_POS);
		posCombination.put(POS_PENN_TREEBANK_NOUN_PLURAL, NLPPosType.NOUNS_POS);
		posCombination.put(POS_PENN_TREEBANK_ADJECTIVE, NLPPosType.ADJECTIVES_POS);
		posCombination.put(POS_PENN_TREEBANK_ADJECTIVE_COMPARATIVE, NLPPosType.ADJECTIVES_POS);
		posCombination.put(POS_PENN_TREEBANK_ADJECTIVE_SUPERLATIVE, NLPPosType.ADJECTIVES_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB_PAST_TENSE, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB_GERAND, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB_PAST_PARTICIPLE, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB_SINGULAR_PRESENT, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_VERB_THIRD_PERSON_PRESENT, NLPPosType.VERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_ADVERB, NLPPosType.ADVERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_ADVERB_COMPARATIVE, NLPPosType.ADVERBS_POS);
		posCombination.put(POS_PENN_TREEBANK_ADVERB_SUPERLATIVE, NLPPosType.ADVERBS_POS);
		this.posCombination = posCombination;
	}

	@Override
	public HashMap<String, NLPPosType> getPOSCombination() {
		return posCombination;
	}

}
