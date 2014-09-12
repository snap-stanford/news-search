package edu.stanford.snap.spinn3rHadoop.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.cli.*;

/**
 * This class is used for parsing command line arguments.
 * 
 * Manual for Commons CLI:
 * http://commons.apache.org/proper/commons-cli/usage.html
 * 
 * Documentation for Commons CLI:
 * http://commons.apache.org/proper/commons-cli/javadocs/api-release/index.html
 * */

public class ParseCLI {	
	@SuppressWarnings("static-access")
	public static CommandLine parse(String[] args){	
		/***
		 * Output
		 * */
		Option output = OptionBuilder.withArgName("output")
				.isRequired()
				.hasArg()
				.withDescription("Select the output folder.")
				.create("output");

		/***
		 * Date limitations
		 * */
		Option start = OptionBuilder.withArgName("start")
				.isRequired()
				.hasArg()
				.withDescription("Set the start date and hour. For 31th of August at 14:00 you write: 2008-31-08T14")
				.create("start");

		Option end = OptionBuilder.withArgName("end")
				.isRequired()
				.hasArg()
				.withDescription("Set the end date and hour. Use the same format as for start date.")
				.create("end");

		/***
		 * Content
		 * */
		Option content = OptionBuilder.withArgName("content")
				.isRequired()
				.hasArgs(3)
				.withDescription("Select the content for searching. Possibilities: WEB, FB, TW.")
				.create("content");
		
		/**
		 * Language
		 * */
		Option langWL = OptionBuilder.withArgName("langWL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White List for languages. If present return only documents that match this language are returned. "
						+ "If several arguments present we return all documents that match any of them.")
				.create("langWL");

		Option langBL = OptionBuilder.withArgName("langBL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black List for languages. If present documents with this language are discarded from results. " +
								 "If several arguments present all documents that match any of them are discarded.")
				.create("langBL");

		
		/***
		 * URL
		 * */
		Option urlWL = OptionBuilder.withArgName("urlWL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White List for urls. If present documents that match at least one of the patterns are returned.")
				.create("urlWL");

		Option urlBL = OptionBuilder.withArgName("urlBL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black List for urls. If present all documents that match any of the pattern are discarded from search results.")
				.create("urlBL");

		/**
		 * Title
		 * */
		Option titleWL = OptionBuilder.withArgName("titleWL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for titles. If present just documents that match all patterns are returned. "
						+ "To implement OR the '|' character should be used within one pattern.")
				.create("titleWL");

		Option titleBL = OptionBuilder.withArgName("titleBL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for titles. If present all documents that match any of the pattern are discarded from search results.")
				.create("titleBL");

		/**
		 * Content
		 * */
		Option contentWL = OptionBuilder.withArgName("contentWL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for content. If present just documents that match all patterns are returned. "
						+ "To implement OR the '|' character should be used within one pattern.")
				.create("contentWL");

		Option contentBL = OptionBuilder.withArgName("contentBL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for content. If present all documents that match any of the pattern are discarded from search results.")
				.create("contentBL");

		/**
		 * Quotes
		 * */
		Option quoteWL = OptionBuilder.withArgName("quoteWL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for quotes. If present return documents for which all patterns are matched to at least one quote.")
				.create("quoteWL");

		Option quoteBL = OptionBuilder.withArgName("quoteBL")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for quotes. If present discard documents for which some quote matches some pattern.")
				.create("quoteBL");
		
		/** Remove documents with:
		 * 		- specific version
		 * 		- no probable language
		 * 		- garbled text
		 * 		- empty title
		 * 		- empty content
		 * 		- no quotes
		 * 	Case insensitive matching
		 *  */
		Option removeVersions = OptionBuilder.withArgName("removeVersions")
				//.isRequired()
				.hasArgs(5)
				.withDescription("Select versions which should be removed from search query: A, B, C, D, E")
				.create("removeVersions");
		Option removeNoLanguage = new Option("removeNoLanguage", "Remove documents that do not have any language detected with probability >= 0.8.");
		Option removeGarbled = new Option("removeGarbled", "Remove documents that have fraction of usefull characters < 0.8.");
		Option removeEmptyTitle = new Option("removeEmptyTitle", "Remove documents that have an empty title.");
		Option removeEmptyContent = new Option("removeEmptyContent", "Remove documents that have an empty content.");
		Option removeNoQuotes = new Option("removeNoQuotes", "Remove documents that have no quotes.");
		Option caseInsensitive = new Option("caseInsensitive", "Make all the matching case-insensitive.");

		CommandLine cmd = null;
		Options options = new Options();
		options.addOption(output);
		options.addOption(start);
		options.addOption(end);
		options.addOption(content);
		options.addOption(langWL);
		options.addOption(langBL);
		options.addOption(urlWL);
		options.addOption(urlBL);
		options.addOption(titleWL);
		options.addOption(titleBL);
		options.addOption(contentWL);
		options.addOption(contentBL);
		options.addOption(quoteWL);
		options.addOption(quoteBL);
		options.addOption(removeVersions);
		options.addOption(removeNoLanguage);
		options.addOption(removeGarbled);
		options.addOption(removeEmptyTitle);
		options.addOption(removeEmptyContent);
		options.addOption(removeNoQuotes);
		options.addOption(caseInsensitive);

		try {
			/** Parse */
			CommandLineParser parser = new BasicParser();
			cmd = parser.parse(options, args);
			
			/** If left unparsed arguments */
			if(cmd.getArgs().length > 0){
					throw new ParseException("Some extra arguments that were not parsed: " + cmd.getArgList());
			}

			/** Check date format and boundaries */
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH");
			format.setLenient(false);
			Date firstDayInData = format.parse("2008-08-01T00");
			Date now = new Date();
			Date startD = format.parse(cmd.getOptionValue("start"));
			Date endD = format.parse(cmd.getOptionValue("end"));
			if (startD.before(firstDayInData)){
				throw new ParseException("start set too early. We collect data from 2008-08-01T00 onwards!");
			}
			if (endD.before(firstDayInData)){
				throw new ParseException("end set too early. We collect data from 2008-08-01T00 onwards!");
			}
			if(now.before(startD)){
				throw new ParseException("StartdDate can not be set after now!");
			}
			if(now.before(endD)){
				throw new ParseException("end can not be set after now!");
			}
			if (startD.compareTo(endD) >= 0 ){
				throw new ParseException("start should be before end.");
			}

			/** Check types */
			String [] correctC = {"WEB", "TW", "FB"};
			for(String c : cmd.getOptionValues("content")){
				if(!Arrays.asList(correctC).contains(c)){
					throw new ParseException("Invalid content type: " + c);
				} 
			}

			/** Check versions */
			String [] correctV = {"A", "B", "C", "D", "E"};
			if(cmd.hasOption("removeVersions")){
				for(String c : cmd.getOptionValues("removeVersions")){
					if(!Arrays.asList(correctV).contains(c)){
						throw new ParseException("Invalid removeVersions type: " + c);
					} 
				}
			}

		} catch (Exception e) {
			System.out.println("ERROR: "+e.getLocalizedMessage());
			System.out.println("");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("search [OUTPUT FOLDER] [OPTIONS]\n"
					+ "\nThe main concept here are White and Black lists. For document to pass the search "
					+ "it must match with (some or all, depeding on attribute) patterns in White list and it "
					+ "must not match with any of the patterns in Black list. Matching with Black list overrides matching with White list.\n"
					+ "NOTICE: empty fields are treater separately, not with White lists. For example, if we search "
					+ "for all documents with language 'en', by default all languages with no language detected will "
					+ "also be in results. To change this use '-removeNoLanguage' option. Same hold for all other fields ass well.", options);
			cmd = null;
		}
		return cmd;
	}
	
	public static void printArguments(CommandLine in){
		System.out.println("-output: " + in.getOptionValue("output"));
		System.out.println("-start: " + in.getOptionValue("start"));
		System.out.println("-end: " + in.getOptionValue("end"));
		System.out.println("-content: " + (in.getOptionValues("content") !=null ? Arrays.asList(in.getOptionValues("content")) : "null"));
		System.out.println("-langWL: " + (in.getOptionValues("langWL") !=null ? Arrays.asList(in.getOptionValues("langWL")) : "null"));
		System.out.println("-langBL: " + (in.getOptionValues("langBL") !=null ? Arrays.asList(in.getOptionValues("langBL")) : "null"));
		System.out.println("-urlWL: " + (in.getOptionValues("urlWL") !=null ? Arrays.asList(in.getOptionValues("urlWL")) : "null"));
		System.out.println("-urlBL: " + (in.getOptionValues("urlBL") !=null ? Arrays.asList(in.getOptionValues("urlBL")) : "null"));
		System.out.println("-titleWL: " + (in.getOptionValues("titleWL") !=null ? Arrays.asList(in.getOptionValues("titleWL")) : "null"));
		System.out.println("-titleBL: " + (in.getOptionValues("titleBL") !=null ? Arrays.asList(in.getOptionValues("titleBL")) : "null"));
		System.out.println("-contentWL: " + (in.getOptionValues("contentWL") !=null ? Arrays.asList(in.getOptionValues("contentWL")) : "null"));
		System.out.println("-contentBL: " + (in.getOptionValues("contentBL") !=null ? Arrays.asList(in.getOptionValues("contentBL")) : "null"));
		System.out.println("-quoteWL: " + (in.getOptionValues("quoteWL") !=null ? Arrays.asList(in.getOptionValues("quoteWL")) : "null"));
		System.out.println("-quoteBL: " + (in.getOptionValues("quoteBL") !=null ? Arrays.asList(in.getOptionValues("quoteBL")) : "null"));
		System.out.println("-removeVersions: " + (in.getOptionValues("removeVersions") !=null ? Arrays.asList(in.getOptionValues("removeVersions")) : "null"));
		System.out.println("-removeNoLanguage: " + in.hasOption("removeNoLanguage"));
		System.out.println("-removeGarbled: " + in.hasOption("removeGarbled"));
		System.out.println("-removeEmptyTitle: " + in.hasOption("removeEmptyTitle"));
		System.out.println("-removeEmptyContent: " + in.hasOption("removeEmptyContent"));
		System.out.println("-removeNoQuotes: " + in.hasOption("removeNoQuotes"));
		System.out.println("-caseInsensitive: " + in.hasOption("caseInsensitive"));
	}

	public static void main(String [] args){
		/**
		 * Sample command line input with all arguments set:
-output out
-start 2010-12-13T23
-end 2013-09-02T17
-content WEB FB TW
-langWL en ru
-langBL	pl
-urlWL "\.com " "\.edu" 
-urlBL "\.co" "\.uk"
-titleWL '[Oo]bama' '[Bb]arack|[Mm]ichelle'
-titleBL '[Mm]ccain' 'perry rosenstein'
-contentWL 'the'
-contentBL 'this saturday'
-quoteWL 'in'
-quoteBL 'politics'
-removeVersions E
-removeNoLanguage
-removeGarbled
-removeEmptyTitle
-removeEmptyContent
-removeNoQuotes
		 */
		
		CommandLine cmd = parse(args);
		if (cmd != null){
			printArguments(cmd);
			System.out.println("\nAll OK!");
		}
		else
			System.out.println("\nNOT OK!");
	}
}