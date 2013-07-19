package com.fractalsciences.algorithms.nlp;

import java.util.ArrayList;
import java.util.List;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetTest {

	
	private static WordNetTest wordNet;
	
	private WordNetTest(){
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
	}
	
	public static WordNetTest getInstance(){
		if(wordNet == null){
			wordNet = new WordNetTest();
		}
		return wordNet;
	}
	
	// Get the List of Synonyms
	public String[] getSynonyms(String word, String wordType){
		String[] listSynonyms = null;
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(word, getSynsetType(wordType));
		if(synsets.length>0){
			listSynonyms = synsets[0].getWordForms();
		}
		return listSynonyms;
	}
	
	// Get the List of Lemma for a particular Word
	public String[] getLemma(String word, String wordType){
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		String[] listLemma = database.getBaseFormCandidates(word, getSynsetType(wordType));
		return listLemma;
	}
	
	// Map Synset Type
	public SynsetType getSynsetType(String wordType){
		if(wordType.equals(NLPPosType.ADJECTIVES_POS.toString())){
			return SynsetType.ADJECTIVE;
		}
		else if(wordType.equals(NLPPosType.ADVERBS_POS.toString())){
			return SynsetType.ADVERB;
		}
		else if(wordType.equals(NLPPosType.NOUNS_POS.toString())){
			return SynsetType.NOUN;
		}
		else{
			return SynsetType.VERB;
		}
	}
	
	public static void main(String[] args) {
		
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		String seedWord = "chocolates";
		
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(seedWord, SynsetType.NOUN); 
		for (int i = 0; i < synsets.length; i++) { 
			String[] synonyms = synsets[i].getWordForms();
			for(String synonym:synonyms){
				System.out.print(synonym + " ");
			}
			System.out.println("");
		}
		
		String[] lemmaList = database.getBaseFormCandidates(seedWord, SynsetType.NOUN);
		if(!(lemmaList.length>0)){
			System.out.println("No Lemma");
		}
		for(String lemma : lemmaList){
			System.out.println("Lemma: " + lemma);
		}
	}
	 
	
}
