package edu.stanford.snap.spinn3rHadoop.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spinn3rDocument {
	public String docId = null;
	public URL url = null;
	public String urlString = null;
	public String date = null;
	public String title = null;
	public String title_raw = null;
	public String content = null;
	public String content_raw = null;
	public List<Link> links = new ArrayList<Link>();
	public List<Quote> quotes = new ArrayList<Quote>();
	public List<Lang> langs = new ArrayList<Lang>();
	public Spinn3rVersion version = null;
	public boolean isGarbled;
	public double nonGarbageFraction;

	public enum Spinn3rVersion {
		A, B, C, D, E;
	}

	public enum ContentType {
		WEB, TWITTER, FACEBOOK;

		public String toString() {
			switch (this) {
			case WEB:
				return "W";
			case TWITTER:
				return "T";
			case FACEBOOK:
				return "F";
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	/*
	 * Prints the document in the "full5" multi-line format.
	 * We also add languages and the information about garbage text.
	 * */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		if (docId != null) {
			str.append("I\t").append(docId).append("\n");
		} else {
			throw new IllegalArgumentException("Document has no docId");
		}
		if (version != null) {
			str.append("V\t").append(version).append("\n");
		} else {
			throw new IllegalArgumentException("Document has no version");
		}
		for (Lang l : langs) {
			str.append("S\t").append(l.toString()).append("\n");
		}
		str.append("G\t").append(isGarbled+"\t").append(nonGarbageFraction+"\t").append("\n");
		if (url != null) {
			str.append("U\t").append(url).append("\n");
		} else {
			System.err.println("DOC ID:" + docId);
			throw new IllegalArgumentException("Document has no URL");
		}
		if (date != null) {
			str.append("D\t").append(date).append("\n");
		} else {
			throw new IllegalArgumentException("Document has no date");
		}
		if (title != null) {
			str.append("T\t").append(title).append("\n");
		}
		if (title_raw != null) {
			str.append("F\t").append(title_raw).append("\n");
		}
		if (content != null) {
			str.append("C\t").append(content).append("\n");
		}
		if (content_raw != null) {
			str.append("H\t").append(content_raw).append("\n");
		}
		for (Link l : links) {
			str.append("L\t").append(l.toString()).append("\n");
		}
		for (Quote q : quotes) {
			str.append("Q\t").append(q.toString()).append("\n");
		}
		return str.toString();
	}

	/*
	 * Construct the Spinn3rDocument object from multi-line string.
	 * Used for parsing the documents stored in hadoop.
	 * */
	public Spinn3rDocument (String doc){
		for(String line : doc.split("\n")){
			String[] tokens = line.split("\t", 2);
			if(tokens.length < 2){
				System.err.println("THE LINE IS: "+ line);
				System.err.println("TOKENS ARE: "+ Arrays.asList(tokens));
			}
			String type = tokens[0];
			String value = tokens[1];
			// DocId
			if (type.equals("I")){
				this.docId = value;
			}
			// Version
			else if (type.equals("V")){
				this.version = Spinn3rVersion.valueOf(value);
			}
			// Languages
			else if (type.equals("S")){
				String [] split = value.split("\t", 2);
				String lng = split[0];
				double prob = Double.valueOf(split[1]);
				this.langs.add(new Lang(lng, prob));
			}
			// Garbled info
			else if (type.equals("G")){
				String [] split = value.split("\t", 2);
				this.isGarbled = Boolean.valueOf(split[0]);
				this.nonGarbageFraction = Double.valueOf(split[1]);
			}
			// Url
			else if (type.equals("U")){
				urlString = value;
				try {
					this.url = new URL(value);
				} catch (MalformedURLException e) {
				}
			}
			// Date
			else if (type.equals("D")){
				this.date = value;
			}
			// Title
			else if (type.equals("T")){
				this.title = value;
			}
			// Title raw
			else if (type.equals("F")){
				this.title_raw = value;
			}
			// Content
			else if (type.equals("C")){
				this.content = value;
			}
			// Content raw
			else if (type.equals("H")){
				this.content_raw = value;
			}
			// Links
			else if (type.equals("L")){
				String [] split = value.split("\t", 3);
				int startPos = Integer.valueOf(split[0]);
				if(split[1].equals("")){
					this.links.add(new Link(startPos, split[2]));
				}
				else{
					int length = Integer.valueOf(split[1]);
					this.links.add(new Link(startPos, length, split[2]));
				}
			}
			// Quotes
			else if (type.equals("Q")){
				String [] split = value.split("\t", 3);
				int startPos = Integer.valueOf(split[0]);
				int length = Integer.valueOf(split[1]);
				this.quotes.add(new Quote(startPos, length, split[2]));
				System.out.print("");
			}
			// Unknown value
			else{
				throw new IllegalArgumentException("Illegal type character '"+type+"' found during parsing spinn3r document.");
			}
		}
	}

	/*
	 * Append a language to this document.
	 * */
	public void appendLang(String lang, double prob){
		this.langs.add(new Lang(lang, prob));
	}


	/*
	 * Class for storing links about this record.
	 * 
	 * A value of -1 for startPos/length means that the link 
	 * appears in the title, not in the content.
	 * */
	public static class Link {
		public int startPos;
		// For the older versions (up to, and incl., full5), this is empty, i.e., null.
		public Integer length;
		public String url;

		public Link(int startPos, String url) {
			this.startPos = startPos;
			this.length = null;
			this.url = url;
		}

		public Link(int startPos, int length, String url) {
			this.startPos = startPos;
			this.length = length;
			this.url = url;
		}

		@Override
		public String toString() {
			return String.format("%d\t%s\t%s", startPos, length == null ? "" : length, url);
		}
	}

	/*
	 * Class for storing quotes about this record.
	 * 
	 * A value of -1 for startPos/length means that the quote 
	 * appears in the title, not in the content.
	 * */ 
	public static class Quote {
		// 
		public int startPos;
		public int length;
		public String text;

		public Quote(int startPos, int length, String text) {
			this.startPos = startPos;
			this.length = length;
			this.text = text;
		}

		@Override
		public String toString() {
			return String.format("%s\t%d\t%s", startPos, length, text);
		}
	}

	/*
	 * Class for storing languages about this record
	 * */ 
	public class Lang {
		public String lang;
		public double prob;

		public Lang(String lang, double prob){
			this.lang = lang;
			this.prob = prob;
		}
		@Override
		public String toString(){
			return String.format("%s\t%f", lang, prob);
		}
	}

	/*
	 * Returns true if document has language whose probability >= 0.8.
	 * */
	public boolean hasProbableLanguage(){
		if(this.langs.size() > 0 && this.langs.get(0).prob >= 0.8){
			return true;
		}
		return false;
	}

	/*
	 * Returns the first language if it is probable.
	 * Else it returns null.
	 * */
	public String getProbableLanguage(){
		if(this.hasProbableLanguage()){
			return this.langs.get(0).lang;
		}
		return null;
	}
}
