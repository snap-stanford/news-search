package edu.stanford.snap.spinn3rHadoop.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
	private static int MAX_ARGUMENTS = 10;
	private static List<String> ALLOWED_LANGUAGESE = Arrays.asList("af", "ar", "bg", "bn", "cs", "da", "de", "el", "en", "es", "et", "fa", "fi", 
		"fr", "gu", "he", "hi", "hr", "hu", "id", "it", "ja", "kn", "ko", "lt", "lv", "mk", "ml", "mr", "ne", "nl", "no", "pa", 
		"pl", "pt", "ro", "ru", "sk", "sl", "so", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh-cn", 
		"zh-tw","ar", "bg", "bn", "ca", "cs", "da", "de", "el", "en", "es", "et", "fa", "fi", "fr", "gu", "he", "hi", "hr", 
		"hu", "id", "it", "ja", "ko", "lt", "lv", "mk", "ml", "nl", "no", "pa", "pl", "pt", "ro", "ru", "si", "sq", "sv", "ta", 
		"te", "th", "tl", "tr", "uk", "ur", "vi", "zh-cn", "zh-tw");
	
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
				.withDescription("Set the start date and hour. Example: for 31th of August 2008 at 14:00 you write: 2008-31-08T14")
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
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White List for languages.")
				.create("langWL");

		Option langBL = OptionBuilder.withArgName("langBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black List for languages.")
				.create("langBL");

		
		/***
		 * URL
		 * */
		Option urlWL = OptionBuilder.withArgName("urlWL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White List for urls.")
				.create("urlWL");

		Option urlBL = OptionBuilder.withArgName("urlBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black List for urls.")
				.create("urlBL");

		/**
		 * Keywords
		 * 		search if this appears in the document: 
		 * 		either in title or in content.
		 * */
		Option keywordWL = OptionBuilder.withArgName("keywordWL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White list for keywords. We search for keywords in title and content.")
				.create("keywordWL");

		Option keywordBL = OptionBuilder.withArgName("keywordBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black list for keywords. We search for keywords in title and content.")
				.create("keywordBL");
		
		/**
		 * Title
		 * */
		Option titleWL = OptionBuilder.withArgName("titleWL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White list for titles.")
				.create("titleWL");

		Option titleBL = OptionBuilder.withArgName("titleBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black list for titles.")
				.create("titleBL");

		/**
		 * Content
		 * */
		Option contentWL = OptionBuilder.withArgName("contentWL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White list for content.")
				.create("contentWL");

		Option contentBL = OptionBuilder.withArgName("contentBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black list for content.")
				.create("contentBL");

		/**
		 * Quotes
		 * */
		Option quoteWL = OptionBuilder.withArgName("quoteWL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("White list for quotes. Return documents for which some pattern is matched by at least one quote.")
				.create("quoteWL");

		Option quoteBL = OptionBuilder.withArgName("quoteBL")
				//.isRequired()
				.hasArgs(MAX_ARGUMENTS)
				.withDescription("Black list for quotes. Discard documents for which any quote matches any pattern.")
				.create("quoteBL");
		
		/** Remove documents with:
		 * 		- specific version
		 * 		- no probable language
		 * 		- garbled text
		 * 		- empty title
		 * 		- empty content
		 * 		- no quotes
		 *  */
		Option removeVersions = OptionBuilder.withArgName("removeVersions")
				//.isRequired()
				.hasArgs(5)
				.withDescription("Select versions which should be removed from search query: A, B, C, D, E")
				.create("removeVersions");
		Option removeNoLanguage = new Option("removeNoLanguage", "Remove documents without probable language (probability >= 0.8).");
		Option removeGarbled = new Option("removeGarbled", "Remove documents that have fraction of usefull characters < 0.8.");
		Option removeUnparsableURL = new Option("removeUnparsableURL", "Remove source URLs that can not be parsed.");
		Option removeEmptyTitle = new Option("removeEmptyTitle", "Remove documents with empty title.");
		Option removeEmptyContent = new Option("removeEmptyContent", "Remove documents with empty content.");
		Option removeNoQuotes = new Option("removeNoQuotes", "Remove documents without quotes.");
		
		/**
		 * Case insensitive matching
		 * Use F5 output format
		 */
		Option caseInsensitive = new Option("caseInsensitive", "Make all the matching case-insensitive.");
		Option formatF5 = new Option("formatF5", "Use the F5 format for output.");

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
		options.addOption(keywordWL);
		options.addOption(keywordBL);
		options.addOption(titleWL);
		options.addOption(titleBL);
		options.addOption(contentWL);
		options.addOption(contentBL);
		options.addOption(quoteWL);
		options.addOption(quoteBL);
		options.addOption(removeVersions);
		options.addOption(removeNoLanguage);
		options.addOption(removeGarbled);
		options.addOption(removeUnparsableURL);
		options.addOption(removeEmptyTitle);
		options.addOption(removeEmptyContent);
		options.addOption(removeNoQuotes);
		options.addOption(caseInsensitive);
		options.addOption(formatF5);

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
			
			/** Check languages */
			if(cmd.hasOption("langWL")){
				for(String l : cmd.getOptionValues("langWL")){
					if(!ALLOWED_LANGUAGESE.contains(l)){
						throw new ParseException("Invalid lenguage: " + l);
					}
				}
			}
			if(cmd.hasOption("langBL")){
				for(String l : cmd.getOptionValues("langBL")){
					if(!ALLOWED_LANGUAGESE.contains(l)){
						throw new ParseException("Invalid lenguage: " + l);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("ERROR: "+e.getLocalizedMessage());
			System.out.println("");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("search [OUTPUT FOLDER] [OPTIONS]\n"
					+ "\nThe main concept here are White and Black lists. For document to pass the search "
					+ "it must match with some pattern in White list and it "
					+ "must not match with any of the patterns in Black list. "
					+ "Matching with Black list overrides matching with White list. "
					+ "When several arguments are present for a list there is the OR operator between them. "
					+ "To use AND use &&. For example: 'black && white' will return documents that contain both words. \n"
					+ "NOTICE: empty fields are treater separately, not with White lists. For example, if we search "
					+ "for all documents with language 'en', by default all languages with no language detected will "
					+ "also be in results. To change this use '-removeNoLanguage' option. Same hold for all other fields ass well.", options);
			cmd = null;
		}
		return cmd;
	}
	
	/**
	 * Helper for reading arguments from file
	 * */
	private static String [] fixOneFile(String argument, CommandLine cmd, String [] args) throws FileNotFoundException{
		String [] fixed = args;
		if(cmd.hasOption(argument) && cmd.getOptionValues(argument).length == 1){
			String fname = cmd.getOptionValue(argument);
			File f = new File(fname);
			if(f.exists() && !f.isDirectory()) {
				Scanner in = new Scanner(new FileReader(f));
				ArrayList<String> content = new ArrayList<String>();
				while(in.hasNextLine())
					content.add(in.nextLine());
				int fnameIndex = Arrays.asList(args).indexOf(fname);
				String [] new_args = new String[args.length - 1 + content.size()];
				System.arraycopy(args, 0, new_args, 0, fnameIndex);
				System.arraycopy(content.toArray(), 0, new_args, fnameIndex, content.size());
				System.arraycopy(args, fnameIndex+1, new_args, content.size()+fnameIndex, args.length-fnameIndex-1);
				fixed = new_args;
			}
		}
		return fixed;
	}
	
	/**
	 * The following method reads arguments from file and replaces them with file content and returns 
	 * args as they would be if all arguments would be given as command line arguments. 
	 * */
	public static String [] replaceArgumentsFromFile(String [] args, CommandLine cmd) throws FileNotFoundException{
		String [] fixed = args;
		fixed = fixOneFile("urlWL", cmd, fixed);
		fixed = fixOneFile("urlBL", cmd, fixed);
		fixed = fixOneFile("keywordWL", cmd, fixed);
		fixed = fixOneFile("keywordBL", cmd, fixed);
		fixed = fixOneFile("titleWL", cmd, fixed);
		fixed = fixOneFile("titleBL", cmd, fixed);
		fixed = fixOneFile("contentWL", cmd, fixed);
		fixed = fixOneFile("contentBL", cmd, fixed);
		fixed = fixOneFile("quoteWL", cmd, fixed);
		fixed = fixOneFile("quoteBL", cmd, fixed);
		return fixed;
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
		System.out.println("-keywordWL: " + (in.getOptionValues("keywordWL") !=null ? Arrays.asList(in.getOptionValues("keywordWL")) : "null"));
		System.out.println("-keywordBL: " + (in.getOptionValues("keywordBL") !=null ? Arrays.asList(in.getOptionValues("keywordBL")) : "null"));
		System.out.println("-titleWL: " + (in.getOptionValues("titleWL") !=null ? Arrays.asList(in.getOptionValues("titleWL")) : "null"));
		System.out.println("-titleBL: " + (in.getOptionValues("titleBL") !=null ? Arrays.asList(in.getOptionValues("titleBL")) : "null"));
		System.out.println("-contentWL: " + (in.getOptionValues("contentWL") !=null ? Arrays.asList(in.getOptionValues("contentWL")) : "null"));
		System.out.println("-contentBL: " + (in.getOptionValues("contentBL") !=null ? Arrays.asList(in.getOptionValues("contentBL")) : "null"));
		System.out.println("-quoteWL: " + (in.getOptionValues("quoteWL") !=null ? Arrays.asList(in.getOptionValues("quoteWL")) : "null"));
		System.out.println("-quoteBL: " + (in.getOptionValues("quoteBL") !=null ? Arrays.asList(in.getOptionValues("quoteBL")) : "null"));
		System.out.println("-removeVersions: " + (in.getOptionValues("removeVersions") !=null ? Arrays.asList(in.getOptionValues("removeVersions")) : "null"));
		System.out.println("-removeNoLanguage: " + in.hasOption("removeNoLanguage"));
		System.out.println("-removeGarbled: " + in.hasOption("removeGarbled"));
		System.out.println("-removeUnparsableURL: " + in.hasOption("removeUnparsableURL"));
		System.out.println("-removeEmptyTitle: " + in.hasOption("removeEmptyTitle"));
		System.out.println("-removeEmptyContent: " + in.hasOption("removeEmptyContent"));
		System.out.println("-removeNoQuotes: " + in.hasOption("removeNoQuotes"));
		System.out.println("-caseInsensitive: " + in.hasOption("caseInsensitive"));
		System.out.println("-formatF5: " + in.hasOption("formatF5"));
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