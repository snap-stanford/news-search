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
				.hasArgs(3)
				.withDescription("Select the output folder.")
				.create("output");

		/***
		 * Date limitations
		 * */
		Option startDate = OptionBuilder.withArgName("startDate")
				.isRequired()
				.hasArg()
				.withDescription("Set the start date. For 31th of August at 14:00 you write: 2008-31-08:14")
				.create("startDate");

		Option endDate = OptionBuilder.withArgName("endDate")
				.isRequired()
				.hasArg()
				.withDescription("Set the end date. Use the same format as for start date.")
				.create("endDate");

		/***
		 * Content
		 * */
		Option content = OptionBuilder.withArgName("content")
				.isRequired()
				.hasArgs(3)
				.withDescription("Select the content for searching. Possibilities: WEB, FB, TW.")
				.create("content");
		/***
		 * URL
		 * */
		Option urlWhiteList = OptionBuilder.withArgName("urlWhiteList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for urls. For document to pass it must match ALL of the white list patterns.")
				.create("urlWhiteList");

		Option urlBlackList = OptionBuilder.withArgName("urlBlackList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for urls. For document to pass it can not match ANY of the black list patterns.")
				.create("urlBlackList");

		/**
		 * Language
		 * */
		Option removeNoLanguage = new Option("removeNoLanguage", "Remove documents that do not have any language detected with probability >= 0.8.");

		Option langWhiteList = OptionBuilder.withArgName("langWhiteList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for languages. If present return only documents that match some of the language in this list.")
				.create("langWhiteList");

		Option langBlackList = OptionBuilder.withArgName("langBlackList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for languages. If present return only doucuments that do not match ANY of the language in this list.")
				.create("langBlackList");

		/**
		 * Grabled
		 * */
		Option removeGarbled = new Option("removeGarbled", "Remove documents that have fraction of usefull characters < 0.8");

		/**
		 * Versions
		 * */
		Option removeVersions = OptionBuilder.withArgName("removeVersions")
				//.isRequired()
				.hasArgs(5)
				.withDescription("Select versions to remove from search query: A, B, C, D, E")
				.create("removeVersions");

		/**
		 * Title
		 * */
		Option removeEmptyTitle = new Option("removeEmptyTitle", "Remove documents that have empty title");

		Option titleWhiteList = OptionBuilder.withArgName("titleWhiteList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for titles. For document to pass it must match ALL of the white list patterns.")
				.create("titleWhiteList");

		Option titleBlackList = OptionBuilder.withArgName("titleBlackList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for titles. For document to pass it must not match ANY of the black list patterns.")
				.create("titleBlackList");

		/**
		 * Content
		 * */
		Option removeEmptyContent = new Option("removeEmptyContent", "Remove documents that have empty content");

		Option contentWhiteList = OptionBuilder.withArgName("contentWhiteList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for content. For document to pass it must match ALL of the white list patterns.")
				.create("contentWhiteList");

		Option contentBlackList = OptionBuilder.withArgName("contentBlackList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for content. For document to pass it must not match ANY of the black list patterns.")
				.create("contentBlackList");

		/**
		 * Quotes
		 * */
		Option removeNoQuotes = new Option("removeNoQuotes", "Remove documents that have no quotes");

		Option quoteWhiteList = OptionBuilder.withArgName("quoteWhiteList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("White list for quotes. For document to pass ALL of the white list patterns should be matched by at least one of its quotes.")
				.create("quoteWhiteList");

		Option quoteBlackList = OptionBuilder.withArgName("quoteBlackList")
				//.isRequired()
				.hasArgs(10)
				.withDescription("Black list for quotes. For document to pass none of the quotes should not match ANY of the black list patterns.")
				.create("quoteBlackList");

		CommandLine cmd = null;
		Options options = new Options();	

		options.addOption(output);
		options.addOption(startDate);
		options.addOption(endDate);
		options.addOption(content);
		options.addOption(urlWhiteList);
		options.addOption(urlBlackList);
		options.addOption(removeNoLanguage);
		options.addOption(langWhiteList);
		options.addOption(langBlackList);
		options.addOption(removeGarbled);
		options.addOption(removeVersions);
		options.addOption(removeEmptyTitle);
		options.addOption(titleWhiteList);
		options.addOption(titleBlackList);
		options.addOption(removeEmptyContent);
		options.addOption(contentWhiteList);
		options.addOption(contentBlackList);
		options.addOption(removeNoQuotes);
		options.addOption(quoteWhiteList);
		options.addOption(quoteBlackList);

		try {
			/** Parse */
			CommandLineParser parser = new BasicParser();
			cmd = parser.parse(options, args);

			/** Check date format and boundaries */
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH");
			format2.setLenient(false);
			Date firstDayInData = format2.parse("2008-08-01T00");
			Date now = new Date();
			Date startD = format2.parse(cmd.getOptionValue("startDate"));
			Date endD = format2.parse(cmd.getOptionValue("endDate"));
			if (startD.before(firstDayInData)){
				throw new ParseException("StartDate set too early. We collect data from 2008-08-01T00 onwards!");
			}
			if (endD.before(firstDayInData)){
				throw new ParseException("EndDate set too early. We collect data from 2008-08-01T00 onwards!");
			}
			if(now.before(startD)){
				throw new ParseException("StartdDate can not be set after now!");
			}
			if(now.before(endD)){
				throw new ParseException("EndDate can not be set after now!");
			}
			if (startD.compareTo(endD) >= 0 ){
				throw new ParseException("StartDate should be before endDate.");
			}

			/** Check types */
			String [] correctC = {"WEB", "TW", "FB"};
			for(String c : cmd.getOptionValues("content")){
				if(!Arrays.asList(correctC).contains(c)){
					throw new ParseException("Invalid content type!");
				} 
			}

			/** Check versions */
			String [] correctV = {"A", "B", "C", "D", "E"};
			if(cmd.hasOption("removeVersions")){
				for(String c : cmd.getOptionValues("removeVersions")){
					if(!Arrays.asList(correctV).contains(c)){
						throw new ParseException("Invalid removeVersions type!");
					} 
				}
			}

		} catch (Exception e) {
			System.out.println("ERROR: "+e.getLocalizedMessage());
			System.out.println("");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("search [OUTPUT FOLDER] [OPTIONS]", options);
			cmd = null;
		}
		return cmd;
	}

	public static void main(String [] args){
		/**
		-output OUT
		-startDate 2010-12-13T23
		-endDate 2013-09-02T17
		-content WEB FB TW
		-urlWhiteList "\.com|\.edu" 
		-urlBlackList "\.co\.uk" 
		-langWhiteList en 
		-langBlackList pl 
		-removeVersions A B C D E 
		-titleBlackList obama obama ... 
		-titleWhiteList barack 
		-contentWhiteList barack 
		-contentBlackList obama 
		-quoteWhiteList barack 
		-quoteBlackList obama
		 */
		if (parse(args) != null)
			System.out.println("All OK!");
		else
			System.out.println("NOT OK!");
	}
}