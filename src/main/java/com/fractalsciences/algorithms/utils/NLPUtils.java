package com.fractalsciences.algorithms.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NLPUtils {

	/**
	 * Sort Map<String, Double> based on Value
	 * @param wordFreqDistinct
	 * @return
	 */
	public Map<String, Double> sortNewWordFreq(HashMap<String, Double> wordFreqDistinct){
		Map<String, Double> wordFreqDistinctSorted = new LinkedHashMap<String, Double>();
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>( wordFreqDistinct.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
				{
			public int compare( Map.Entry<String, Double> wordFreq1, Map.Entry<String, Double> wordFreq2 )
			{
				return (-(wordFreq1.getValue()).compareTo( wordFreq2.getValue()));
			}
				} );
		
		for (Map.Entry<String, Double> entry : list)
		{
			wordFreqDistinctSorted.put( entry.getKey(), entry.getValue() );
		}
		return wordFreqDistinctSorted;
	}
}
