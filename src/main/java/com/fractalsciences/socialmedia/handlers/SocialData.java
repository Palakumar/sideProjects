package com.fractalsciences.socialmedia.handlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class SocialData {

	protected String textContent;
	protected String userName;
	protected Date date;
	protected String searchTerm;
	protected Set<String> listWords;
	
	public SocialData(){
	}
	
	public SocialData(String textContent, String userName, Date date,
			String searchTerm, Set<String> listWords) {
		this.textContent = textContent;
		this.userName = userName;
		this.date = date;
		this.searchTerm = searchTerm;
		this.listWords = listWords;
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
	        "year","years","yet","you","young","younger","youngest","your","yours"));
	
	
	public void createListWords(String textContent){
		HashSet<String> listWords = new HashSet<String>();
		for(String token : textContent.split("\\s+")){
			String cleanToken = stripPunctuations(token).toLowerCase();
			if(!cleanToken.equals("") && !stopWordsSmall.contains(cleanToken)){
				listWords.add(cleanToken);
			}
		}
		//Collections.sort(listWords);
		this.listWords = listWords ;
	}
	
	
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


	public String getTextContent() {
		return textContent;
	}


	public String getUserName() {
		return userName;
	}


	public Date getDate() {
		return date;
	}


	public String getSearchTerm() {
		return searchTerm;
	}


	public Set<String> getListWords() {
		return listWords;
	}


	public static HashSet<String> getStopWordsSmall() {
		return stopWordsSmall;
	}

	@Override
	public String toString() {
		return "SocialData [textContent=" + textContent + ", userName="
				+ userName + ", date=" + date + ", searchTerm=" + searchTerm
				+ ", listWords=" + listWords + "]";
	}
	
	
	
}
