package edu.stanford.snap.spinn3rHadoop.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {

	public static void main(String[] args) {
		String query = "^edu.nytimes";
		String queryEscaped = Pattern.quote(query);
		Pattern p = Pattern.compile(queryEscaped, Pattern.CASE_INSENSITIVE);
		
		String s = "edu.nytimes.com";
		Matcher m = p.matcher(s);
		
		if (m.find()){
			System.out.println("It matches!");
		}
		else {
			System.out.println("It does NOT matches!");
		}
	}

}
