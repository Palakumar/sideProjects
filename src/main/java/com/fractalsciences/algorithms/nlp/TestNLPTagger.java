package com.fractalsciences.algorithms.nlp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fractalsciences.algorithms.Subtopics;
import com.fractalsciences.algorithms.db.JDBCConnection;
import com.fractalsciences.socialmedia.handlers.TwitterData;

public class TestNLPTagger {

	public static void main(String[] args) throws IOException, SQLException {
		
		List<String> searchTerm = new ArrayList<String>();
		searchTerm.add("Starbucks");
		Subtopics subtopics = new Subtopics();
		
		JDBCConnection jdbcConn = new JDBCConnection();
		jdbcConn.getConnection();
		List<TwitterData> listTweets = jdbcConn.retrieveAllTweets(searchTerm);
		
		long stanfordLoad = 0;
		long stanfordTag = 0;
		long cmuLoad = 0;
		long cmuTag = 0;
		int numTest = 100;
		for(int i=0; i<numTest; i++){
			/*** Test Stanford Speed ***/
			long loadStanfordTagging = System.currentTimeMillis();
			NLPTaggerHandler nlpHandler = new NLPTaggerHandler();
			NLPTagger nlpTagger = nlpHandler.selectNLPTagger(NLPTaggerType.STANFORD_TAGGER);
			
			long startStanfordTagging = System.currentTimeMillis();
			for(TwitterData tweet : listTweets){
				List<TaggedChunk> taggedWords = nlpTagger.posTagSentence(tweet.getTextContent());
	//			for(TaggedChunk chunk : taggedWords){
	//				System.out.print(chunk.token + "/" + chunk.posTag + " ");
	//			}
				//System.out.println("");
			}
			long endStanfordTagging = System.currentTimeMillis();
			stanfordLoad += startStanfordTagging-loadStanfordTagging;
			stanfordTag += endStanfordTagging-startStanfordTagging;
			
			//System.out.println("****************************************************************************");
			
			/*** Test CMU Speed ***/
			long loadCMUTagging = System.currentTimeMillis();
			nlpTagger = nlpHandler.selectNLPTagger(NLPTaggerType.CMU_TAGGER);
			
			long startCMUTagging = System.currentTimeMillis();
			for(TwitterData tweet : listTweets){
				List<TaggedChunk> taggedWords = nlpTagger.posTagSentence(tweet.getTextContent());
	//			for(TaggedChunk chunk : taggedWords){
	//				System.out.print(chunk.token + "/" + chunk.posTag + " ");
	//			}
	//			System.out.println("");
			}
			long endCMUTagging = System.currentTimeMillis();
			
			cmuLoad += startCMUTagging-loadCMUTagging;
			cmuTag += endCMUTagging-startCMUTagging;
			
			System.out.println("Time to Tag Stanford: " + (endStanfordTagging-startStanfordTagging) + "ms");
			System.out.println("Time to Tag CMU: " + (endCMUTagging-startCMUTagging) + "ms");
		}
		
		System.out.println("Time to load Stanford: " + (stanfordLoad/numTest) + "ms");
		System.out.println("Time to Tag Stanford: " + (stanfordTag/numTest) + "ms");
		System.out.println("Time to load CMU: " + (cmuLoad/numTest) + "ms");
		System.out.println("Time to Tag CMU: " + (cmuTag/numTest) + "ms");
	}
}
