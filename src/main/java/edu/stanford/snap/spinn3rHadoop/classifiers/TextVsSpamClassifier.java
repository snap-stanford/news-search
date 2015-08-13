package edu.stanford.snap.spinn3rHadoop.classifiers;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public class TextVsSpamClassifier implements Classifier {
	public static final String SPAM = "spam";
	public static final String TEXT = "text";
	private Set<String> stopwords; 
	
	public TextVsSpamClassifier() {
		this.stopwords = EnglishStopwords.getStopwords();
	}
	
	public int getNumberOfSentences(String content) {
		return content.split("[\\.]\\s").length;
	}
	
	public double getRelativeNumberOfStopwords(String content) {
		double counter = 0.0;
		for (String word: content.split("\\s")) {
			if (stopwords.contains(word)) {
				counter ++;
			}
		}
		return counter / content.split("\\s").length;
	}
	
	public int getNumberOfWords(String content) {
		return content.split("\\s").length;
	}
	
	@Override
	public String getClass(Spinn3rDocument doc) {
		int numSentences = getNumberOfSentences(doc.content);
		int numWords = getNumberOfWords(doc.content);
		double relNumOfStopwords = getRelativeNumberOfStopwords(doc.content);
		
		if (numSentences < 6) {
			if (numWords < 56) {
				return SPAM;
			}
			else {
				if (relNumOfStopwords <= 0.2191) { return SPAM; }
				else { return TEXT;	}
			}
			
		}
		else {
			if (relNumOfStopwords <= 0.1120) { return SPAM; }
			else { return TEXT; }
		}
	}

}
