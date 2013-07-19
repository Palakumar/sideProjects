package com.fractalsciences.algorithms.nlp;

public class POSTagCombinationWeights {

	public int NOUN_TAG_WEIGHT = 1;
	public int PROPER_NOUN_TAG_WEIGHT= 1;
	public int VERB_TAG_WEIGHT= 1;
	public int ADJECTIVE_TAG_WEIGHT = 1;
	public int ADVERB_TAG_WEIGHT= 1;

	public NLPPosType[] ADJECTIVE_NOUN_POS = {NLPPosType.ADJECTIVES_POS, NLPPosType.NOUNS_POS};
	public NLPPosType[] NOUN_NOUN_POS = {NLPPosType.NOUNS_POS, NLPPosType.NOUNS_POS};
	public NLPPosType[] ADJECTIVE_PROPER_NOUN_POS = {NLPPosType.ADJECTIVES_POS, NLPPosType.PROPER_NOUNS_POS};
	public NLPPosType[] PROPER_NOUN_PROPER_NOUN_POS = {NLPPosType.PROPER_NOUNS_POS, NLPPosType.PROPER_NOUNS_POS};

	public static void main(String[] args) {
		
		POSTagCombinationWeights posComb = new POSTagCombinationWeights();
		NLPPosType[] test = {NLPPosType.ADJECTIVES_POS, NLPPosType.NOUNS_POS};
		NLPPosType[] test2 = {NLPPosType.ADVERBS_POS, NLPPosType.NOUNS_POS};
		
		if(test[0] == posComb.ADJECTIVE_NOUN_POS[0] && test[1] == posComb.ADJECTIVE_NOUN_POS[1]){
			System.out.println("one is true");
		}
		if(test2 != posComb.ADJECTIVE_NOUN_POS){
			System.out.println("two is true");
		}
	}
}
