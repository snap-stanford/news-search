package edu.stanford.snap.spinn3rHadoop.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {

	public static void main(String[] args) {
		Pattern p = Pattern.compile("SLOVENIA", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher("Slovenia is a nice country.");
		
		if (m.find()){
			System.out.println("It matches!");
		}
		else {
			System.out.println("It does NOT matches!");
		}
	}

}
