package edu.stanford.snap.spinn3rHadoop.utils;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.mapred.CleanupQueue;

import edu.stanford.snap.spinn3rHadoop.classifiers.Classifier;
import edu.stanford.snap.spinn3rHadoop.classifiers.TextVsSpamClassifier;
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
	protected String formatInput = "yyyy-MM-dd'T'HH";
	protected static String AND_SIGN = "&&";
	protected Matcher matcher;
	protected String [] langWL;
	protected String [] langBL;
	protected ArrayList<ArrayList<Pattern>> keywordWL;
	protected ArrayList<ArrayList<Pattern>> keywordBL;
	protected ArrayList<ArrayList<Pattern>> urlWL;
	protected ArrayList<ArrayList<Pattern>> urlBL;
	protected ArrayList<ArrayList<Pattern>> titleWL;
	protected ArrayList<ArrayList<Pattern>> titleBL;
	protected ArrayList<ArrayList<Pattern>> contentWL;
	protected ArrayList<ArrayList<Pattern>> contentBL;
	protected ArrayList<ArrayList<Pattern>> quoteWL;
	protected ArrayList<ArrayList<Pattern>> quoteBL;
	protected String [] removeVersions;
	protected boolean removeNoLanguage;
	protected boolean removeGarbled;
	protected boolean removeUnparsableURL;
	protected boolean removeEmptyTitle;
	protected boolean removeEmptyContent;
	protected boolean removeNoQuotes;
	protected boolean removeSpam;
	protected boolean caseInsensitive;
	protected String contentCleaner;
	protected Date start;
	protected Date end;


	public DocumentFilter(CommandLine cmd){
		try {
			this.start = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("start"));
			this.end = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("end"));
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		this.removeNoLanguage = cmd.hasOption("removeNoLanguage");
		this.removeGarbled = cmd.hasOption("removeGarbled");
		this.removeUnparsableURL = cmd.hasOption("removeUnparsableURL");
		this.removeEmptyTitle = cmd.hasOption("removeEmptyTitle");
		this.removeEmptyContent = cmd.hasOption("removeEmptyContent");
		this.removeNoQuotes = cmd.hasOption("removeNoQuotes");
		this.removeSpam = cmd.hasOption("removeSpam");
		this.caseInsensitive = cmd.hasOption("caseInsensitive");
		this.contentCleaner = cmd.hasOption("contentCleaner") ? (cmd.getOptionValue("contentCleaner") == null ? "" : cmd.getOptionValue("contentCleaner")) : null;

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
		/** Filter by date:
		 * */
		if(d.date.before(start) || d.date.after(end)){
			return false;
		}
		
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
		
		if (removeSpam) {
			TextVsSpamClassifier classifier = new TextVsSpamClassifier();
			if (!classifier.getClass(d).equals(classifier.TEXT)) {
				return false;
			}
		}
		
		/** Clean content if cleanContent is set
		 * 
		 */
		if (contentCleaner != null) {
			if (contentCleaner.equals(ParseCLI.CONTENT_CLEANER_INCLUDE_SPECUAL_CHAR)) {
				ContentCleaner.cleanContent(d, true);
			}
			else {
				ContentCleaner.cleanContent(d);
			}
		}

		/** If we get to here than d satisfied all conditions. */
		return true;
	}

	public static void main(String [] args) throws MalformedURLException, FileNotFoundException{
		/** Sample document */
		String documentString = "I	2008080100_00000000_W\n"
				+ "V	B\n"
				+ "S	en	0.999996\n"
				+ "G	false	1.0\n"
				+ "U	http://times.com/KB/silverlight/convertsilverlightcontrol.aspx\n"
				+ "D	2013-09-02 17:00:00\n"
				+ "T	codeproject how to convert a silverlight control to a visual webgui \n"
				+ "C	how to convert a silverlight control to [simple wordpress tag] \n"
				+ "L	1206		http://schemas.microsoft.com/winfx/2006/xaml/presentation\n"
				+ "L	1211		http://schemas.microsoft.com/winfx/2006/xaml\n"
				+ "Q	948	69	how to create property binding in a visual webgui silverlight control\n"
				+ "Q	1212	173	 and this is the media player control class public class  : control { static dependencyproperty mediasourceproperty; private mediaelement mobjmediaelement; base .\n"
				+ "Q	3047	134	 videoplayer.silverlight.controls.videoplayer, videoplayer.silverlight.controls, version=1.0.0.0, culture=neutral, publickeytoken=null\n"
				+ "Q	3633	6	 this.\n"
				+ "Q	4110	55	 videoplayer.controls.videoplayer, videoplayer.controls\n"
				+ "Q	4513	6	how to";

		CommandLine cmd = ParseCLI.parse(args);
		
		/** Fill in arguments from file */
		String [] new_args = ParseCLI.replaceArgumentsFromFile(args, cmd);
		cmd = ParseCLI.parse(new_args);
		ParseCLI.printArguments(cmd);

		
		DocumentFilter filter = new DocumentFilter(cmd);
		Spinn3rDocument d = new Spinn3rDocument(documentString);
		System.out.println("This document passed search conditions: " + filter.documentSatisfies(d));
		System.out.println("\nIf contentClenaner is true, content should change\n" + d.content);
		
		
	}
}