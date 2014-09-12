package edu.stanford.snap.spinn3rHadoop.utils;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;

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
	private Matcher matcher;
	private Pattern [] langWL;
	private Pattern [] langBL;
	private Pattern [] urlWL;
	private Pattern [] urlBL;
	private Pattern [] titleWL;
	private Pattern [] titleBL;
	private Pattern [] contentWL;
	private Pattern [] contentBL;
	private Pattern [] quoteWL;
	private Pattern [] quoteBL;
	private String [] removeVersions;
	private boolean removeNoLanguage;
	private boolean removeGarbled;
	private boolean removeEmptyContent;
	private boolean removeEmptyTitle;
	private boolean removeNoQuotes;


	public DocumentFilter(CommandLine cmd){
		if(cmd.getOptionValues("langWL") != null)
			this.langWL = getPatternsFromStings(cmd.getOptionValues("langWL"));
		if(cmd.getOptionValues("langBL") != null)
			this.langBL = getPatternsFromStings(cmd.getOptionValues("langBL"));
		if(cmd.getOptionValue("urlWL") != null)
			this.urlWL = getPatternsFromStings(cmd.getOptionValues("urlWL"));
		if(cmd.getOptionValue("urlBL") != null)
			this.urlBL = getPatternsFromStings(cmd.getOptionValues("urlBL"));
		if(cmd.getOptionValues("titleWL") != null)
			this.titleWL = getPatternsFromStings(cmd.getOptionValues("titleWL"));
		if(cmd.getOptionValues("titleBL") != null)
			this.titleBL = getPatternsFromStings(cmd.getOptionValues("titleBL"));
		if(cmd.getOptionValues("contentWL") != null)
			this.contentWL = getPatternsFromStings(cmd.getOptionValues("contentWL"));
		if(cmd.getOptionValues("contentBL") != null)
			this.contentBL = getPatternsFromStings(cmd.getOptionValues("contentBL"));
		if(cmd.getOptionValues("quoteWL") != null)
			this.quoteWL = getPatternsFromStings(cmd.getOptionValues("quoteWL"));
		if(cmd.getOptionValues("quoteBL") != null)
			this.quoteBL = getPatternsFromStings(cmd.getOptionValues("quoteBL"));
		if(cmd.getOptionValue("removeVersions") != null)
			this.removeVersions = cmd.getOptionValues("removeVersions");
		this.removeNoLanguage = cmd.hasOption("removeNoLanguage");
		this.removeGarbled = cmd.hasOption("removeGarbled");
		this.removeEmptyContent = cmd.hasOption("removeEmptyContent");
		this.removeEmptyTitle = cmd.hasOption("removeEmptyTitle");
		this.removeNoQuotes = cmd.hasOption("removeNoQuotes");
	}

	private Pattern [] getPatternsFromStings(String [] in){
		Pattern [] p = new Pattern [in.length];
		for(int i = 0; i < p.length; i++)
			p[i] = Pattern.compile(in[i]);
		return p;
	}

	/**
	 * Check if document d satisfies search conditions.
	 * */
	public boolean documentSatisfies(Spinn3rDocument d){
		/** Filter by language:
		 * 		If removeNoLanguage = true then remove record with no language, otherwise leave them.
		 * 		Black and White lists only apply for documents with probable language.
		 * 		If we have a WL and the language is not in it, discard it.
		 * 		If language is in BL, discard it.
		 * */
		if(d.hasProbableLanguage()){
			if(langWL != null && langWL.length > 0){
				boolean someLangSatisfies = false;
				for(Pattern p : langWL){
					matcher = p.matcher(d.getProbableLanguage());
					if (matcher.find()) {
						someLangSatisfies = true;
					}
				}
				if(!someLangSatisfies){
					return false;
				}
			}
			if(langBL != null && langBL.length > 0){
				for(Pattern p : langBL){
					matcher = p.matcher(d.getProbableLanguage());
					if(matcher.find()){
						return false;
					}
				}
			}
		}
		else if (removeNoLanguage){
			return false;
		}

		/** 
		 * Filter by URL:
		 * 		For document to to pass it must match with at 
		 * 		least one pattern from the WL and it must not  
		 * 		match with any pattern from the black list.
		 * */
		if(urlWL != null && urlWL.length > 0){
			boolean someUrlSatisfies = false;
			for(Pattern p : urlWL){
				matcher = p.matcher(d.url.getHost());
				if (matcher.find()) {
					someUrlSatisfies = true;
				}
			}
			if(!someUrlSatisfies){
				return false;
			}
		}
		if(urlBL != null && urlBL.length > 0){
			for(Pattern p : urlBL){
				matcher = p.matcher(d.url.getHost());
				if(matcher.find()){
					return false;
				}
			}
		}

		/** 
		 * Filter by title:
		 * 		For document to pass it must match with all patterns in the WL and 
		 * 		it must not match with any pattern in the BL.
		 * */
		if(d.title != null && d.title.length() > 0){
			if(titleWL != null && titleWL.length > 0){
				for(Pattern p : titleWL){
					matcher = p.matcher(d.title);
					if (!matcher.find()) {
						return false;
					}
				}
			}
			if(titleBL != null && titleBL.length > 0){
				for(Pattern p : titleBL){
					matcher = p.matcher(d.title);
					if(matcher.find()){
						return false;
					}
				}
			}
		}
		else if (removeEmptyTitle){
			return false;
		}

		/** 
		 * Filter by content:
		 * 		For document to pass it must match with all patterns in the WL and 
		 * 		it must not match with any pattern in the BL.
		 * */
		if(d.content != null && d.content.length() > 0){
			if(contentWL != null && contentWL.length > 0){
				for(Pattern p : contentWL){
					matcher = p.matcher(d.content);
					if (!matcher.find()) {
						return false;
					}
				}
			}
			if(contentBL != null && contentBL.length > 0){
				for(Pattern p : contentBL){
					matcher = p.matcher(d.content);
					if(matcher.find()){
						return false;
					}
				}
			}
		}
		else if (removeEmptyContent){
			return false;
		}

		/** 
		 * Filter by quotes:
		 * 		For document to pass all WL patterns must be matched by at least one quote
		 * 		and none of the quotes is allowed to match any of BL patterns.
		 * */
		if(!d.quotes.isEmpty()){
			if(quoteWL != null && quoteWL.length > 0){
				for(Pattern p : quoteWL){
					boolean someQuoteSatisfies = false;
					for(Spinn3rDocument.Quote q : d.quotes){
						matcher = p.matcher(q.text);
						if (matcher.find()) {
							someQuoteSatisfies = true;
						}
					}
					if(!someQuoteSatisfies){
						return false;
					}
				}
			}
			if(quoteBL != null && quoteBL.length > 0){
				for(Pattern p : quoteBL){
					for(Spinn3rDocument.Quote q : d.quotes){
						matcher = p.matcher(q.text);
						if (matcher.find()) {
							return false;
						}
					}
				}
			}
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
				+ "T	codeproject: how to convert a silverlight control to a visual webgui silverlight control. free source code and programming help\n"
				+ "C	how to convert a silverlight control to a visual webgui silverlight control in this how to we are going to learn how to convert a silverlight control to a visual webgui control.\n"
				+ "L	1206		http://schemas.microsoft.com/winfx/2006/xaml/presentation\n"
				+ "L	1211		http://schemas.microsoft.com/winfx/2006/xaml\n"
				+ "Q	85	6	how to\n"
				+ "Q	549	6	how to\n"
				+ "Q	948	69	how to create property binding in a visual webgui silverlight control\n"
				+ "Q	1212	173	 and this is the media player control class public class videoplayer : control { static dependencyproperty mediasourceproperty; private mediaelement mobjmediaelement; base .\n"
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