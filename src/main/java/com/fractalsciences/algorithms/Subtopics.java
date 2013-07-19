package com.fractalsciences.algorithms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fractalsciences.algorithms.db.JDBCConnection;
import com.fractalsciences.algorithms.nlp.NLPPosType;
import com.fractalsciences.algorithms.nlp.NLPTagger;
import com.fractalsciences.algorithms.nlp.NLPTaggerHandler;
import com.fractalsciences.algorithms.nlp.NLPTaggerType;
import com.fractalsciences.algorithms.nlp.StanfordNLP;
import com.fractalsciences.algorithms.nlp.TaggedChunk;
import com.fractalsciences.socialmedia.connectors.MultiStreamConnector;
import com.fractalsciences.socialmedia.handlers.TwitterData;

import edu.stanford.nlp.ling.TaggedWord;



public class Subtopics {


	private static final int MAX_WORDS_PER_POS = 3;
	
	public List<String> listNouns;
	public List<String> listAdjectives;
	public List<String> listProperNouns;
	public List<String> list2Grams;
	public List<String> listHashTags;
	
	private static final String LIST_NOUNS = "Nouns";
	private static final String LIST_ADJECTIVES = "Adjectives";
	private static final String LIST_PROPER_NOUNS = "ProperNouns";
	private static final String LIST_2_GRAMS = "2-Grams";
	private static final String LIST_HASHTAGS = "HashTags";
	


	public static void main(String[] args) throws SQLException, IOException {
		List<String> searchTerm = new ArrayList<String>();
		searchTerm.add("Starbucks");
		Subtopics subtopics = new Subtopics();
		
		NLPTaggerHandler nlpHandler = new NLPTaggerHandler();
		NLPTagger nlpTagger = nlpHandler.selectNLPTagger(NLPTaggerType.CMU_TAGGER);
		
		if(nlpTagger != null){
			//
		}
		

		/*
		 * Query DataSift
		 */
		//List<TwitterData> listTweets =  subtopics.queryDataSiftMultiStream(searchTerm, 2);


		/*
		 * Query DB
		 */
		JDBCConnection jdbcConn = new JDBCConnection();
		jdbcConn.getConnection();
		List<TwitterData> listTweets = jdbcConn.retrieveAllTweets(searchTerm);

//		HashMap<String, HashMap<String, Double>> topPOS = subtopics.topPOS(listTweets);
//		subtopics.displayTopPOSWords((topPOS));
//		System.out.println("");
//		subtopics.displayTopPOSWords(subtopics.retrieveTopWords(topPOS));
		
		
		// Test Alex
		subtopics.createTextFilesSubtopicEntry(nlpTagger, listTweets, searchTerm);
	}


	/**
	 * Query Twitter
	 * @param listSearchTerms
	 * @param maxInteractions
	 * @return
	 */
	public List<TwitterData> queryDataSiftMultiStream(List<String> listSearchTerms, int maxInteractions){
		List<TwitterData> listTweetData = new ArrayList<TwitterData>();
		MultiStreamConnector multiTwitterConnector = new MultiStreamConnector();
		List<String[]> listCSDLQueries = createOrCSDLQuery(listSearchTerms);

		multiTwitterConnector.run(listTweetData, listCSDLQueries, maxInteractions);
		//		for(TwitterData tweet : listTweetData){
		//			System.out.println("Tweet Content: " + tweet.getTextContent());
		//		}
		return listTweetData;
	}




	/**
	 * Create Or CSDL query using Keywords
	 * @param queries
	 * @return
	 */
	public List<String[]> createOrCSDLQuery(List<String> queries){
		List<String[]> listQueries = new ArrayList<String[]>();
		for(String query : queries){
			String csdlQuery = "twitter.text contains_any \"" + query + "\" AND twitter.lang == \"en\"" ;
			String[] csdlAndQuery = {csdlQuery, query};
			listQueries.add(csdlAndQuery);
		}
		return listQueries;
	}

	
	/**
	 * Extract Subtopic Info For List of Text
	 * @param nlpTagger
	 * @param listTweets
	 * @return
	 */
	public HashMap<String, HashMap<String, Double>> extractSubtopicInfoListText(NLPTagger nlpTagger, List<TwitterData> listTweets){
		HashMap<String, HashMap<String, Double>> subtopicMap = new HashMap<String, HashMap<String, Double>>();
		subtopicMap.put(LIST_NOUNS, new HashMap<String, Double>());
		subtopicMap.put(LIST_ADJECTIVES, new HashMap<String, Double>());
		subtopicMap.put(LIST_PROPER_NOUNS, new HashMap<String, Double>());
		
		for(TwitterData tweet : listTweets){
			extractSubtopicInfoText(nlpTagger, subtopicMap, tweet.getTextContent());
		}
		
		return subtopicMap;
	}  
	
	/**
	 * Extract Subtopic Info For Single Text
	 * @param nlpTagger
	 * @param subtopicMap
	 * @param text
	 */
	public void extractSubtopicInfoText(NLPTagger nlpTagger, HashMap<String, HashMap<String, Double>> subtopicMap, String text){
		
		// Get Tokens and POS for Text
		List<TaggedChunk> taggedWords = nlpTagger.posTagSentence(text);
		
		// Update HashTags Map for Text
		retrieveHashTags(text, subtopicMap.get(LIST_HASHTAGS));
		
		// Update 2-Grams Map for Text
		retrieve2Grams(taggedWords, subtopicMap.get(LIST_2_GRAMS));
		
		// Update Subtopic Map if Token is NOUN, ADJECTIVE, PROPER NOUN
		for(TaggedChunk taggedToken : taggedWords){
			updatePOSMap(taggedToken, nlpTagger, subtopicMap);
		}
		
	}
	
	
	/**
	 * Update Subtopic Map if Token is NOUN, ADJECTIVE, PROPER NOUN
	 * @param taggedToken
	 * @param nlpTagger
	 * @param subtopicMap
	 */
	public void updatePOSMap(TaggedChunk taggedToken, NLPTagger nlpTagger, HashMap<String, HashMap<String, Double>> subtopicMap){
		String posType = taggedToken.getPosTag();
		HashMap<String, NLPPosType> posCombination = nlpTagger.getPOSCombination();
		if(posCombination.get(posType) != null){
			if(posCombination.get(posType).equals(NLPPosType.NOUNS_POS)){
				updateMapWordFrequencyWithWord(taggedToken.getToken(), subtopicMap.get(LIST_NOUNS));
			}
			else if(posCombination.get(posType).equals(NLPPosType.ADJECTIVES_POS)){
				updateMapWordFrequencyWithWord(taggedToken.getToken(), subtopicMap.get(LIST_ADJECTIVES));
			}
			else if(posCombination.get(posType).equals(NLPPosType.PROPER_NOUNS_POS)){
				updateMapWordFrequencyWithWord(taggedToken.getToken(), subtopicMap.get(LIST_PROPER_NOUNS));
			}
		}
	}


	/**
	 * Sort Map<String, Double> based on Value
	 * @param wordFreqDistinct
	 * @return
	 */
	public HashMap<String, Double> sortReturnTopWords(HashMap<String, Double> wordFreqDistinct, int maxWords){
		HashMap<String, Double> wordFreqDistinctSorted = new LinkedHashMap<String, Double>();
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>( wordFreqDistinct.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>(){
			public int compare( Map.Entry<String, Double> wordFreq1, Map.Entry<String, Double> wordFreq2 ){
				return (-(wordFreq1.getValue()).compareTo( wordFreq2.getValue()));
			}
		});

		int wordsAdded = 0;
		for (Map.Entry<String, Double> entry : list){
			if(wordsAdded == maxWords){
				break;
			}
			wordFreqDistinctSorted.put( entry.getKey(), entry.getValue() );
			wordsAdded++;
		}
		return wordFreqDistinctSorted;
	}
	
	
	/**
	 * Sort Map<String, Double> based on Value
	 * @param wordFreqDistinct
	 * @return
	 */
	public HashMap<SubtopicEntry, Double> sortReturnTopWordsSubtopicEntry(HashMap<SubtopicEntry, Double> wordFreqDistinct, int maxWords){
		HashMap<SubtopicEntry, Double> wordFreqDistinctSorted = new LinkedHashMap<SubtopicEntry, Double>();
		List<Map.Entry<SubtopicEntry, Double>> list = new LinkedList<Map.Entry<SubtopicEntry, Double>>( wordFreqDistinct.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<SubtopicEntry, Double>>(){
			public int compare( Map.Entry<SubtopicEntry, Double> wordFreq1, Map.Entry<SubtopicEntry, Double> wordFreq2 ){
				return (-(wordFreq1.getValue()).compareTo( wordFreq2.getValue()));
			}
		});

		int wordsAdded = 0;
		for (Map.Entry<SubtopicEntry, Double> entry : list){
			if(wordsAdded == maxWords){
				break;
			}
			wordFreqDistinctSorted.put( entry.getKey(), entry.getValue() );
			wordsAdded++;
		}
		return wordFreqDistinctSorted;
	}


	/**
	 * Retrieve Top Words For Each POS type
	 * @param mapPOS
	 * @return
	 */
	public HashMap<String, HashMap<String, Double>> retrieveTopWords(HashMap<String, HashMap<String, Double>> mapPOS){
		HashMap<String, HashMap<String, Double>> topPOSWords = new HashMap<String, HashMap<String, Double>>();
		for(Map.Entry<String, HashMap<String, Double>> entry : mapPOS.entrySet()){
			HashMap<String, Double> topWords = sortReturnTopWords(entry.getValue(), MAX_WORDS_PER_POS);
			topPOSWords.put(entry.getKey(), topWords);
		}
		return topPOSWords;
	}
	
	
	/**
	 * Display Top Words
	 * @param topPOSWords
	 */
	public void displayTopPOSWords(HashMap<String, HashMap<String, Double>> topPOSWords){
		for(Map.Entry<String, HashMap<String, Double>> entry : topPOSWords.entrySet()){
			System.out.println("POS Type: " + entry.getKey());
			HashMap<String, Double> topWords = entry.getValue();
			System.out.println(topWords.toString());
		}
	}
	
	
	
	public void createTweetFrequencyMap(List<TwitterData> listTweets, HashMap<String, Double> mapKeywords){
		
	}
	
	
	/**
	 * Update HashTag Map with Matched tags
	 * @param text
	 * @param mapHashTags
	 */
	public void retrieveHashTags(String text, HashMap<String, Double> mapHashTags){

		List<String> listMatched = patternMatcher(text, "#");
		// TODO: Lower/Upper Case 
		updateMapWordFrequencyWithListWords(listMatched, mapHashTags);
	}

	
	/**
	 * Update Reference Map with Matched tags
	 * @param text
	 * @param mapHashTags
	 */
	public void retrieveAtReference(String text, HashMap<String, Double> mapAtReference){

		List<String> listMatched = patternMatcher(text, "@");
		// TODO: Lower/Upper Case 
		updateMapWordFrequencyWithListWords(listMatched, mapAtReference);
		
	}
	
	
	/**
	 * Update Word Frequency Map for a Word.
	 * @param word
	 * @param mapWordFrequency
	 */
	public void updateMapWordFrequencyWithWord(String word, HashMap<String, Double> mapWordFrequency){
		if(mapWordFrequency.containsKey(word)){
			Double keywordFrequency = mapWordFrequency.get(word) + 1.0;
			mapWordFrequency.put(word, keywordFrequency);
		}
		else{
			mapWordFrequency.put(word, 1.0);
		}
	}
	
	
	
	/**
	 * Update Word Frequency Map for a Word.
	 * @param word
	 * @param mapWordFrequency
	 */
	public void updateMapWordFrequencyWithWordSubtopicEntry(SubtopicEntry subtopic, HashMap<SubtopicEntry, Double> mapWordFrequency){
		if(mapWordFrequency.containsKey(subtopic)){
			Double keywordFrequency = mapWordFrequency.get(subtopic) + 1.0;
			mapWordFrequency.put(subtopic, keywordFrequency);
		}
		else{
			mapWordFrequency.put(subtopic, 1.0);
		}
	}
	
	
	/**
	 * Update Word Frequency Map for a List.
	 * @param listMatched
	 * @param mapMatched
	 */
	public void updateMapWordFrequencyWithListWords(List<String> listWords, HashMap<String, Double> mapWordFrequency){
		for(String word : listWords){
			updateMapWordFrequencyWithWord(word, mapWordFrequency);
		}
	}
	
	
	/**
	 * Match Specified Pattern and return all matched occurrences
	 * @param text
	 * @param patternToMatch
	 * @return listMatched - Matched terms
	 */
	public List<String> patternMatcher(String text, String patternToMatch){
		List<String> listMatched = new ArrayList<String>();
		Pattern pattern = Pattern.compile(patternToMatch);
		Matcher matcher = pattern.matcher(text);
		
		// Update Map with occurrence of HashTags or @References 
		while(matcher.find()){
			listMatched.add(matcher.group().toLowerCase());
		}
		return listMatched;
	}
	
	
	/**
	 * Update the 2-Grams Map.
	 * @param taggedWords
	 * @param map2Grams
	 */
	public void retrieve2Grams(List<TaggedChunk> taggedWords, HashMap<String, Double> map2Grams){
		List<String> tmpList2Grams = new ArrayList<String>();
		for(int i=0; i<taggedWords.size()-1; i++){
			String twoGram = taggedWords.get(i) + " " + taggedWords.get(i+1);
			tmpList2Grams.add(twoGram);
		}
		updateMapWordFrequencyWithListWords(tmpList2Grams, map2Grams);
	}
	
	
	
	public HashMap<String, HashMap<String, Double>> topPOS(List<TwitterData> listTweets){
		HashMap<String, HashMap<String, Double>> mapPOS = new HashMap<String, HashMap<String, Double>>();

		StanfordNLP stanfordNLP = StanfordNLP.getInstance();
		for(TwitterData tweet : listTweets){
			String tweetText = tweet.getTextContent();
			List<TaggedWord> taggedWords = stanfordNLP.nlpTaggedWords(tweetText);
			for(TaggedWord taggedWord : taggedWords){
				if(stanfordNLP.mapGeneralPos.containsKey(taggedWord.tag())){
					HashMap<String, Double> topicFrequencyMap;
					if(mapPOS.containsKey(stanfordNLP.mapGeneralPos.get(taggedWord.tag()))){
						topicFrequencyMap = mapPOS.get(stanfordNLP.mapGeneralPos.get(taggedWord.tag()));
						
						/* Words in Map are Lower Case */
						if(topicFrequencyMap.containsKey(taggedWord.word().toLowerCase())){
							Double newFrequencyTopic = topicFrequencyMap.get(taggedWord.word().toLowerCase()) + 1.0;
							topicFrequencyMap.put(taggedWord.word().toLowerCase(), newFrequencyTopic);
						}
						else{
							topicFrequencyMap.put(taggedWord.word().toLowerCase(), 1.0);
						}
					}
					else{
						topicFrequencyMap = new HashMap<String, Double>();
						topicFrequencyMap.put(taggedWord.word().toLowerCase(), 1.0);

					}
					mapPOS.put(stanfordNLP.mapGeneralPos.get(taggedWord.tag()), topicFrequencyMap);
				}
			}

		}
		return mapPOS;
	}
	
	
	public HashMap<SubtopicEntry, Double> aggregateSubtopicsBasedOnSynonyms(HashMap<SubtopicEntry, Double> subtopicsmap){
		HashMap<SubtopicEntry, Double> aggregatedSubtopic = new HashMap<SubtopicEntry, Double>();
		HashSet<String> alreadyAddedSubtopic = new HashSet<String>();
		WordNetHelper subtopicHelper= new WordNetHelper();
		Iterator<Map.Entry<SubtopicEntry, Double>> iter = subtopicsmap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<SubtopicEntry, Double> entry = iter.next();
			
			
			if(!alreadyAddedSubtopic.contains(entry.getKey().subtopic)){
				// Add to List of Processed Subotpics
				alreadyAddedSubtopic.add(entry.getKey().subtopic);

				double countSubtopic = 0;
				String[] listSynonyms = subtopicHelper.getSynonymsWordNet(entry.getKey().subtopic, entry.getKey().subtopicPOS);
				if(listSynonyms != null){
					for(String synonym:listSynonyms){
						SubtopicEntry synonymSubtopic = new SubtopicEntry(synonym, entry.getKey().subtopicPOS);
						if(!alreadyAddedSubtopic.contains(synonym) && subtopicsmap.containsKey(synonymSubtopic)){
							countSubtopic += subtopicsmap.get(synonymSubtopic);
							
							alreadyAddedSubtopic.add(synonym);
						}
					}
				}
				aggregatedSubtopic.put(entry.getKey(), entry.getValue() + countSubtopic);
			}
		}
		return aggregatedSubtopic;
	}
	
	
	/**
	 * Create Text Files for Alex.
	 * @param nlpTagger
	 * @param listTweets
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public void createTextFiles(NLPTagger nlpTagger, List<TwitterData> listTweets, List<String> searchTerm) throws FileNotFoundException, UnsupportedEncodingException{
		HashMap<String, Double> wordFrequency = new HashMap<String, Double>();
		HashMap<String, List<Integer>> inverseIndex = new HashMap<String, List<Integer>>();
		int max_words = 100;

		int indexTweet = 1;
		HashMap<String, NLPPosType> posCombination = nlpTagger.getPOSCombination();
		
		WordNetHelper subtopicHelper = new WordNetHelper();
		for(TwitterData tweet : listTweets){

			// Get Tokens and POS for Text
			List<TaggedChunk> taggedWords = nlpTagger.posTagSentence(tweet.getTextContent());
			for(TaggedChunk taggedToken : taggedWords){
				String posType = taggedToken.getPosTag();
				if(posCombination.get(posType) != null && (posCombination.get(posType).equals(NLPPosType.NOUNS_POS) || posCombination.get(posType).equals(NLPPosType.ADJECTIVES_POS) || 
						posCombination.get(posType).equals(NLPPosType.ADVERBS_POS) || posCombination.get(posType).equals(NLPPosType.VERBS_POS))){
					
					String cleanToken = stripPunctuations(taggedToken.getToken()).toLowerCase();
					
					if(!cleanToken.equals("") && !stopWordsSmall.contains(cleanToken) && !searchTerm.contains(cleanToken)){
						cleanToken = cleanToken;// + "_" + posCombination.get(posType);
						updateMapWordFrequencyWithWord(cleanToken, wordFrequency);

						// Update Inverse Index
						if(inverseIndex.containsKey(cleanToken)){
							List<Integer> listTweet = inverseIndex.get(cleanToken);
							listTweet.add(indexTweet);
							inverseIndex.put(cleanToken, listTweet);
						}
						else{
							List<Integer> listTweet = new ArrayList<Integer>();
							listTweet.add(indexTweet);
							inverseIndex.put(cleanToken, listTweet);
						}
					}
				}

			}
			indexTweet++;
		}
		
		// Sort Map
		HashMap<String, Double> sortedWordFrequency = sortReturnTopWords(wordFrequency, max_words);
		
		// Write Tweets to File
		PrintWriter writer = new PrintWriter("C:\\work\\ListTweets_test.txt", "UTF-8");
		PrintWriter writerFreq = new PrintWriter("C:\\work\\Word_Frequency_test.txt", "UTF-8");
		PrintWriter writerMatrix = new PrintWriter("C:\\work\\DataMatrix_test.txt", "UTF-8");
		
		int i=1;
		for(TwitterData tweet : listTweets){
			writer.println(tweet.getTextContent());

			for(Map.Entry<String, Double> entry : sortedWordFrequency.entrySet()){
				List<Integer> tweets = inverseIndex.get(entry.getKey());

				if(tweets.contains(i)){
					writerMatrix.print(1 + "	");
				}
				else{
					writerMatrix.print(0 + "	");
				}
			}
			writerMatrix.println("");
			i++;
		}
		
		for(Map.Entry<String, Double> entry : sortedWordFrequency.entrySet()){
			writerFreq.println(entry.getKey() + "	" + entry.getValue());
		}
		writer.close();
		writerFreq.close();
		writerMatrix.close();
	}  
	
	/**
	 * Create Text Files for Alex.
	 * @param nlpTagger
	 * @param listTweets
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public void createTextFilesSubtopicEntry(NLPTagger nlpTagger, List<TwitterData> listTweets, List<String> searchTerm) throws FileNotFoundException, UnsupportedEncodingException{
		HashMap<SubtopicEntry, Double> wordFrequency = new HashMap<SubtopicEntry, Double>();
		HashMap<String, List<Integer>> inverseIndex = new HashMap<String, List<Integer>>();
		int max_words = 100;
		String suffix = "_1000_Starbucks_NEW";
		int indexTweet = 1;
		HashMap<String, NLPPosType> posCombination = nlpTagger.getPOSCombination();
		
		WordNetHelper subtopicHelper = new WordNetHelper();
		for(TwitterData tweet : listTweets){

			// Get Tokens and POS for Text
			List<TaggedChunk> taggedWords = nlpTagger.posTagSentence(tweet.getTextContent());
			for(TaggedChunk taggedToken : taggedWords){
				String posType = taggedToken.getPosTag();
				if(posCombination.get(posType) != null && (posCombination.get(posType).equals(NLPPosType.NOUNS_POS) || posCombination.get(posType).equals(NLPPosType.ADJECTIVES_POS) || 
						posCombination.get(posType).equals(NLPPosType.ADVERBS_POS) || posCombination.get(posType).equals(NLPPosType.VERBS_POS))){
					
					String cleanToken = stripPunctuations(taggedToken.getToken()).toLowerCase();
					
					if(!cleanToken.equals("") && !stopWordsSmall.contains(cleanToken) && !searchTerm.contains(cleanToken)){
						cleanToken = cleanToken;// + "_" + posCombination.get(posType);
						SubtopicEntry subtopic = new SubtopicEntry(cleanToken, posCombination.get(posType).toString());
						updateMapWordFrequencyWithWordSubtopicEntry(subtopic, wordFrequency);

						List<Integer> listTweet = null;
						// Update Inverse Index
						if(inverseIndex.containsKey(cleanToken)){
							listTweet = inverseIndex.get(cleanToken);
							listTweet.add(indexTweet);
							inverseIndex.put(cleanToken, listTweet);
							
						}
						else{
							listTweet = new ArrayList<Integer>();
							listTweet.add(indexTweet);
							inverseIndex.put(cleanToken, listTweet);
						}
						
					}
				}

			}
			indexTweet++;
		}
		
		// Aggregate Subtopics Based on Synonyms
		HashMap<SubtopicEntry, Double> aggregatedSubtopics = aggregateSubtopicsBasedOnSynonyms(wordFrequency);
		
		// Sort Map
		HashMap<SubtopicEntry, Double> sortedWordFrequency = sortReturnTopWordsSubtopicEntry(aggregatedSubtopics, max_words);
		
		// Write Tweets to File
		PrintWriter writer = new PrintWriter("C:\\work\\ListTweets" + suffix +".txt", "UTF-8");
		PrintWriter writerFreq = new PrintWriter("C:\\work\\Word_Frequency" + suffix +".txt", "UTF-8");
		PrintWriter writerMatrix = new PrintWriter("C:\\work\\DataMatrix" + suffix +".txt", "UTF-8");
		
		int i=1;
		for(TwitterData tweet : listTweets){
			writer.println(tweet.getTextContent());

			for(Map.Entry<SubtopicEntry, Double> entry : sortedWordFrequency.entrySet()){
				
				//if(entry.getKey().subtopic.equalsIgnoreCase("chocolates")){
					//List<Integer> listTweetsIndex = inverseIndex.get(entry.getKey().subtopic);
					//HashSet<Integer> tweets = entry.getKey().listIndex;

					String[] listSynonyms = subtopicHelper.getSynonymsWordNet(entry.getKey().subtopic, entry.getKey().subtopicPOS);
					HashSet<Integer> listTweetsIndex= new HashSet<Integer>();
					if(listSynonyms != null){
						for(String synonym : listSynonyms){
							if(inverseIndex.get(synonym) != null){
								listTweetsIndex.addAll(inverseIndex.get(synonym));
							}
						}
					}

					if(listTweetsIndex.contains(i)){
						writerMatrix.print(1 + "	");
					}
					else{
						writerMatrix.print(0 + "	");
					}
				//}
			}
			writerMatrix.println("");
			i++;
		}
		
		for(Map.Entry<SubtopicEntry, Double> entry : sortedWordFrequency.entrySet()){
			writerFreq.println(entry.getKey().subtopic + "	" + entry.getValue());
			String[] listSynonyms = subtopicHelper.getSynonymsWordNet(entry.getKey().subtopic, entry.getKey().subtopicPOS);
			String[] listLemma = subtopicHelper.getLemmaWordNet(entry.getKey().subtopic, entry.getKey().subtopicPOS);
//			if(listSynonyms == null && listLemma == null){
//				writerFreq.println(entry.getKey().subtopic);
//			}
//			else if(listSynonyms == null){
//				writerFreq.println(entry.getKey().subtopic + "	" + Arrays.toString(listLemma));
//			}
//			else if(listLemma == null){
//				writerFreq.println(entry.getKey().subtopic + "	" + Arrays.toString(listSynonyms));
//			}
//			else{
//				writerFreq.println(entry.getKey().subtopic + "	" + Arrays.toString(listSynonyms) + "	" + Arrays.toString(listLemma));
//			}
			//writerFreq.print("" + subtopicHelper.getSynonyms(word, posType))
		}
		writer.close();
		writerFreq.close();
		writerMatrix.close();
	}
	
	
	public static HashSet<String> stopWordsSmall = new HashSet<String>(Arrays.asList(
			"http", "www","check-in", "amp", "via",
	        "a","about","above","across","after","again","against","all","almost","alone","along","already",
	        "also","although","always","among","an","and","another","any","anybody","anyone","anything",
	        "anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","b",
	        "back","backed","backing","backs","be","became","because","become","becomes","been","before",
	        "began","behind","being","beings","best","better","between","big","both","but","by","c","came","can",
	        "cannot","case","cases","certain","certainly","clear","clearly","come","could","d","did","differ",
	        "different","differently","do","does","done","down","down","downed","downing","downs","during","e",
	        "each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody",
	        "everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds",
	        "first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave",
	        "general","generally","get","gets","give","given","gives","go","going","good","goods","got","great",
	        "greater","greatest","group","grouped","grouping","groups","h","had","has","have","having","he","her",
	        "here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if",
	        "important","in","interest","interested","interesting","interests","into","is","it","its","itself","j",
	        "just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest",
	        "least","less","let","lets","like","likely","long","longer","longest","m","made","make","making","man",
	        "many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my",
	        "myself","n","necessary","need","needed","needing","needs","never","new","new","newer","newest","next",
	        "no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","o","of","off","often",
	        "old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order",
	        "ordered","ordering","orders","other","others","our","out","over","p","part","parted","parting",
	        "parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present",
	        "presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really",
	        "right","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem",
	        "seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows",
	        "side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere",
	        "state","states","still","still","such","sure","t","take","taken","than","that","the","their","them","then",
	        "there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts",
	        "three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two",
	        "u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants",
	        "was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who",
	        "whole","whose","why","will","with","within","without","work","worked","working","works","would","x","y",
	        "year","years","yet","you","young","younger","youngest","your","yours","im","am","love"));
	
	
	/**
	 * Remove punctuation before, after word. 
	 * Keep "-" if middle of the word.
	 * @param text
	 * @return String - remove punctuation before, after word.
	 */
	public String stripPunctuations(String text) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {

			// Match Alphanumeric character
			if ((text.charAt(i) >= 65 && text.charAt(i) <= 90) || 
					(text.charAt(i) >= 97 && text.charAt(i) <= 122)) {
				sb.append(text.charAt(i));
			}
			// Strip punctuation at the beginning and end of word
			else if(i == 0 || i == text.length()-1){
				continue;
			}
			// Keep "-" if middle of the word
			else if(text.charAt(i) == 45 && (i+1)<text.length()){
				if((text.charAt(i) >= 65 && text.charAt(i) <= 90) || 
						(text.charAt(i) >= 97 && text.charAt(i) <= 122)){ sb.append(text.charAt(i));}
			}
			// If single quote followed by "s", remove single quote and "s"
			else if(text.charAt(i) == 39 && (text.charAt(i+1) == 83 || text.charAt(i+1) == 115)){
				return sb.toString();
			}
			else{
				return "";
			}
		}
		return sb.toString();
	}
	
}
