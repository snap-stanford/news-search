package edu.stanford.snap.spinn3rHadoop.utils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;

import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument.Quote;

/**
 * This class implements checking whether some document
 * satisfies search conditions.
 * 
 * The main concept here are White and Black lists.
 * 
 * White List is like search box, we only leave documents
 * that matches with the pattern in the White List.
 * 
 * Black List is for removing objects that matched with 
 * the White List pattern. You can think about this like a
 * second filter for documents that passes the White List.
 * If document matches any of the pattern in Black List
 * than it is removed from search results.
 * */

public class DocumentFilter {
	private static String AND_SIGN = "&&";
	private Matcher matcher;
	private String [] langWL;
	private String [] langBL;
	private ArrayList<ArrayList<Pattern>> keywordWL;
	private ArrayList<ArrayList<Pattern>> keywordBL;
	private ArrayList<ArrayList<Pattern>> urlWL;
	private ArrayList<ArrayList<Pattern>> urlBL;
	private ArrayList<ArrayList<Pattern>> titleWL;
	private ArrayList<ArrayList<Pattern>> titleBL;
	private ArrayList<ArrayList<Pattern>> contentWL;
	private ArrayList<ArrayList<Pattern>> contentBL;
	private ArrayList<ArrayList<Pattern>> quoteWL;
	private ArrayList<ArrayList<Pattern>> quoteBL;
	private String [] removeVersions;
	private boolean removeNoLanguage;
	private boolean removeGarbled;
	private boolean removeUnparsableURL;
	private boolean removeEmptyTitle;
	private boolean removeEmptyContent;
	private boolean removeNoQuotes;
	private boolean caseInsensitive;


	public DocumentFilter(CommandLine cmd){
		this.removeNoLanguage = cmd.hasOption("removeNoLanguage");
		this.removeGarbled = cmd.hasOption("removeGarbled");
		this.removeUnparsableURL = cmd.hasOption("removeUnparsableURL");
		this.removeEmptyTitle = cmd.hasOption("removeEmptyTitle");
		this.removeEmptyContent = cmd.hasOption("removeEmptyContent");
		this.removeNoQuotes = cmd.hasOption("removeNoQuotes");
		this.caseInsensitive = cmd.hasOption("caseInsensitive");

		if(cmd.getOptionValues("langWL") != null)
			this.langWL = cmd.getOptionValues("langWL");
		if(cmd.getOptionValues("langBL") != null)
			this.langBL = cmd.getOptionValues("langBL");
		if(cmd.getOptionValue("urlWL") != null)
			this.urlWL = getArrayListOfPatternsFromStings(cmd.getOptionValues("urlWL"));
		if(cmd.getOptionValue("urlBL") != null)
			this.urlBL = getArrayListOfPatternsFromStings(cmd.getOptionValues("urlBL"));
		if(cmd.getOptionValues("keywordWL") != null)
			this.keywordWL = getArrayListOfPatternsFromStings(cmd.getOptionValues("keywordWL"));
		if(cmd.getOptionValues("keywordBL") != null)
			this.keywordBL = getArrayListOfPatternsFromStings(cmd.getOptionValues("keywordBL"));
		if(cmd.getOptionValues("titleWL") != null)
			this.titleWL = getArrayListOfPatternsFromStings(cmd.getOptionValues("titleWL"));
		if(cmd.getOptionValues("titleBL") != null)
			this.titleBL = getArrayListOfPatternsFromStings(cmd.getOptionValues("titleBL"));
		if(cmd.getOptionValues("contentWL") != null)
			this.contentWL = getArrayListOfPatternsFromStings(cmd.getOptionValues("contentWL"));
		if(cmd.getOptionValues("contentBL") != null)
			this.contentBL = getArrayListOfPatternsFromStings(cmd.getOptionValues("contentBL"));
		if(cmd.getOptionValues("quoteWL") != null)
			this.quoteWL = getArrayListOfPatternsFromStings(cmd.getOptionValues("quoteWL"));
		if(cmd.getOptionValues("quoteBL") != null)
			this.quoteBL = getArrayListOfPatternsFromStings(cmd.getOptionValues("quoteBL"));
		if(cmd.getOptionValue("removeVersions") != null)
			this.removeVersions = cmd.getOptionValues("removeVersions");
	}

	private ArrayList<ArrayList<Pattern>> getArrayListOfPatternsFromStings(String [] in){
		ArrayList<ArrayList<Pattern>> l = new ArrayList<ArrayList<Pattern>>();
		for(int i = 0; i < in.length; i++){
			ArrayList<Pattern> tmp = new ArrayList<Pattern>();
			for(String p : in[i].split(AND_SIGN)){
				if(this.caseInsensitive){
					tmp.add(Pattern.compile(p.trim(), Pattern.CASE_INSENSITIVE));
				}
				else{
					tmp.add(Pattern.compile(p.trim()));
				}
			}
			l.add(tmp);
		}
		return l;
	}
	
	private boolean satsfiedWhiteAndBlackList(ArrayList<ArrayList<Pattern>> wl, ArrayList<ArrayList<Pattern>> bl, String text){
		/**
		 * 	We threat patterns as follows:
		 * 		There is AND between Patterns inside one ArrayList at second level.
		 * 		There is OR between ArrayList<Patterns>  at first level. 
		 * */
		
		if(wl != null && !wl.isEmpty()){
			boolean oneKeywordMatched = false; 		//OR phase
			for(ArrayList<Pattern> wlpattern : wl){
				boolean andSatisfied = true; 		//AND phase
				for(Pattern p : wlpattern){
					matcher = p.matcher(text);
					if (!matcher.find()) {
						andSatisfied = false;
					}
				}
				if(andSatisfied){
					oneKeywordMatched = true;
					break;
				}
			}
			if(!oneKeywordMatched){
				return false;
			}
		}
		
		if(bl != null && !bl.isEmpty()){
			boolean oneKeywordMatched = false; 		//OR phase
			for(ArrayList<Pattern> blpattern : bl){
				boolean andSatisfied = true; 		//AND phase
				for(Pattern p : blpattern){
					matcher = p.matcher(text);
					if (!matcher.find()) {
						andSatisfied = false;
					}
				}
				if(andSatisfied){
					oneKeywordMatched = true;
					break;
				}
			}
			if(oneKeywordMatched){
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if document d satisfies search conditions.
	 * */
	public boolean documentSatisfies(Spinn3rDocument d){
		/** Filter by language:
		 * 		Black and White lists only apply for documents with probable language.
		 * */
		if(d.hasProbableLanguage()){
			if(langWL != null && langWL.length > 0 && !Arrays.asList(langWL).contains(d.getProbableLanguage())){
				return false;
			}
			if(langBL != null && langBL.length > 0 && Arrays.asList(langBL).contains(d.getProbableLanguage())){
				return false;
			}
		}
		else if (removeNoLanguage){
			return false;
		}

		/** 
		 * Filter by URL
		 * */
		if(d.url != null){
			if(!satsfiedWhiteAndBlackList(urlWL, urlBL, d.url.getHost()))
				return false;
		}
		else if(removeUnparsableURL){
			return false;
		}
		
		/** 
		 * Filter by keywords:
		 * */
		if(!satsfiedWhiteAndBlackList(this.keywordWL, this.keywordBL, d.title + " " + d.content)){
			return false;
		}
		

		/** 
		 * Filter by title:
		 * */		
		if(d.title != null && d.title.length() > 0){
			if(!satsfiedWhiteAndBlackList(titleWL, titleBL, d.title))
				return false;
		}
		else if (removeEmptyTitle){
			return false;
		}

		/** 
		 * Filter by content:
		 * */
		if(d.content != null && d.content.length() > 0){
			if(!satsfiedWhiteAndBlackList(contentWL, contentBL, d.content))
				return false;
		}
		else if (removeEmptyContent){
			return false;
		}

		/** 
		 * Filter by quotes:
		 * */
		if(!d.quotes.isEmpty()){
			String allQuotes = "";
			for(Quote q : d.quotes)
				allQuotes += q.text;
			if(!satsfiedWhiteAndBlackList(quoteWL, quoteBL, allQuotes))
				return false;
		}
		else if (removeNoQuotes){
			return false;
		}

		/**	Filter by garbled:
		 * 		if removeGarbled = true then remove all documents that are garbled, otherwise leave it
		 * */
		if(removeGarbled && d.isGarbled){
			return false;
		}

		/** Filter by versions:
		 * 		if version is in removeVersions list then remove this record
		 * */
		if(removeVersions != null && removeVersions.length > 0){
			if(Arrays.asList(removeVersions).contains(d.version.name())){
				return false;
			}
		}

		/** If we get to here than d satisfied all conditions. */
		return true;
	}

	public static void main(String [] args) throws MalformedURLException{
		/** Sample document */
		String documentString = "I	2008080100_00000000_W\n"
				+ "V	B\n"
				+ "S	en	0.999996\n"
				+ "G	false	1.0\n"
				+ "U	http://codeproject.com/KB/silverlight/convertsilverlightcontrol.aspx\n"
				+ "D	2008-08-01 00:00:00\n"
				+ "T	codeproject how to convert a silverlight control to a visual webgui \n"
				+ "C	how to convert a silverlight control to \n"
				+ "L	1206		http://schemas.microsoft.com/winfx/2006/xaml/presentation\n"
				+ "L	1211		http://schemas.microsoft.com/winfx/2006/xaml\n"
				+ "Q	948	69	how to create property binding in a visual webgui silverlight control\n"
				+ "Q	1212	173	 and this is the media player control class public class  : control { static dependencyproperty mediasourceproperty; private mediaelement mobjmediaelement; base .\n"
				+ "Q	3047	134	 videoplayer.silverlight.controls.videoplayer, videoplayer.silverlight.controls, version=1.0.0.0, culture=neutral, publickeytoken=null\n"
				+ "Q	3633	6	 this.\n"
				+ "Q	4110	55	 videoplayer.controls.videoplayer, videoplayer.controls\n"
				+ "Q	4513	6	how to\n";

		CommandLine cmd = ParseCLI.parse(args);
		ParseCLI.printArguments(cmd);
		DocumentFilter filter = new DocumentFilter(cmd);
		Spinn3rDocument d = new Spinn3rDocument(documentString);
		System.out.println("This document passed search conditions: " + filter.documentSatisfies(d));
	}
}