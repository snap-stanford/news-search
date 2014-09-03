package edu.stanford.snap.spinn3rHadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;
import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;

public class Search extends Configured implements Tool {
	private static CommandLine cmd = null;
	
	public static void main(String[] args) throws Exception {
		/** Check for arguments */
		cmd = ParseCLI.parse(args);
		if(cmd == null){
			System.exit(-1);
		}
		
		/** Run the job */
		int res = ToolRunner.run(new Configuration(), new Search(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		System.out.println(Arrays.toString(args));

		/** Get configuration */
		//Configuration conf = new Configuration(true);
		Configuration conf = getConf();
		conf.set("textinputformat.record.delimiter","\n\n");

		/** Delete output directory if it exists */
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(args[1]), true);

		/** Job configuration */
		Job job = Job.getInstance(conf, "HadoopSearch");
		job.setJarByClass(Search.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		/** Set Mapper and Reducer, use identity reducer*/
		job.setMapperClass(Map.class);
		job.setReducerClass(Reducer.class);
		//job.setNumReduceTasks(0);

		/** Set input and output formats */
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		/** Set input and output path */
		// TODO set input path according to date
		//FileInputFormat.addInputPath(job, new Path(args[0]));
		FileInputFormat.addInputPath(job, new Path("input/web/2008-08/web-2008-08-01T00-00-00Z.txt"));
		FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("output")));

		job.waitForCompletion(true);
		return 0;
	}

	public static class Map extends Mapper<LongWritable, Text, Text, NullWritable> {
		private Matcher matcher;
		private Pattern urlWhiteList = Pattern.compile("");
		private Pattern urlBlackList = Pattern.compile("^$");
		private String [] langWhiteList = {};
		private String [] langBlackList = {};
		//private Pattern [] titleWhiteList = {Pattern.compile("&#039;[oO]bama")};
		//private Pattern [] titleBlackList = {Pattern.compile("philanthropist")};
		private Pattern [] titleWhiteList = {Pattern.compile("[Oo]bama"), Pattern.compile("[Bb]arack|[Mm]ichelle")};
		private Pattern [] titleBlackList = {Pattern.compile("[Mm]ccain"), Pattern.compile("perry rosenstein")};
		private Pattern [] contentWhiteList = {Pattern.compile("")};
		private Pattern [] contentBlackList = {Pattern.compile("this saturday")};
		private Pattern [] quoteWhiteList = {Pattern.compile("politics")};
		private Pattern [] quoteBlackList = {};
		private String [] removeVersions = {};
		private boolean removeNoLanguage = true;
		private boolean removeGarbled = true;
		private boolean removeEmptyTitle = true;
		private boolean removeEmptyContent = true;
		private boolean removeNoQuotes = true;
		
		@Override
		public void setup(Context context){
			System.out.println(cmd.getOptionValue("output"));
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			boolean satisfiesConditions = true;
			
			/** 
			 * Parse document.
			 * */
			Spinn3rDocument d = new Spinn3rDocument(value.toString());

			/** 
			 * Filter by URL:
			 * 		for document to to pass it must match with the whiteList and 
			 * 		it must not match with the black list
			 * */
			matcher = urlWhiteList.matcher(d.url.getHost());
			if (!matcher.find()) {
				satisfiesConditions = false;
			}
			matcher = urlBlackList.matcher(d.url.getHost());
			if(matcher.find()){
				satisfiesConditions = false;
			}

			/** Filter by language:
			 * 		if removeNoLanguage = true then remove record with no language, otherwise leave it
			 * 		black and white lists only apply for documents with languages
			 * 		if we have a whiteList and the language is not in it, discard it
			 * 		if language is in black list, discard it
			 * */
			if(d.hasProbableLanguage()){
				if(langWhiteList.length > 0 && !Arrays.asList(langWhiteList).contains(d.getProbableLanguage())){
					satisfiesConditions = false;
				}
				if(Arrays.asList(langBlackList).contains(d.getProbableLanguage())){
					satisfiesConditions = false;
				}
			}
			else if (removeNoLanguage){
				satisfiesConditions = false;
			}
			
			/**	Filter by garbled:
			 * 		if removeGarbled = true then remove all documents that are garbled, otherwise leave it
			 * */
			if(removeGarbled && d.isGarbled){
				satisfiesConditions = false;
			}
			
			/** Filter by versions:
			 * 		if version is in removeVersions list then remove this record
			 * */
			if(Arrays.asList(removeVersions).contains(d.version.name())){
				satisfiesConditions = false;
			}
			
			/** 
			 * Filter by title:
			 * 		for document to pass it must match with all patterns in the whiteList and 
			 * 		it must not match with any pattern in the blackList
			 * */
			if(d.title != null){
				for(Pattern p : titleWhiteList){
					matcher = p.matcher(d.title);
					if (!matcher.find()) {
						satisfiesConditions = false;
					}
				}
				for(Pattern p : titleBlackList){
					matcher = p.matcher(d.title);
					if(matcher.find()){
						satisfiesConditions = false;
					}
				}
			}
			else if (removeEmptyTitle){
				satisfiesConditions = false;
			}
			
			/** 
			 * Filter by content:
			 * 		for document to pass it must match with all patterns in the whiteList and 
			 * 		it must not match with any pattern in the blackList
			 * */
			if(d.content != null){
				for(Pattern p : contentWhiteList){
					matcher = p.matcher(d.content);
					if (!matcher.find()) {
						satisfiesConditions = false;
					}
				}
				for(Pattern p : contentBlackList){
					matcher = p.matcher(d.content);
					if(matcher.find()){
						satisfiesConditions = false;
					}
				}
			}
			else if (removeEmptyContent){
				satisfiesConditions = false;
			}
			
			/** 
			 * Filter by quotes:
			 * 		for document to pass all whiteList patterns must be matched by at least one quote
			 * 		and none of the quotes is allowed to match any of blackList patterns.
			 * */
			if(!d.quotes.isEmpty()){
				for(Pattern p : quoteWhiteList){
					boolean someQuoteSatisfies = false;
					for(Spinn3rDocument.Quote q : d.quotes){
						matcher = p.matcher(q.text);
						if (matcher.find()) {
							someQuoteSatisfies = true;
						}
					}
					if(!someQuoteSatisfies){
						satisfiesConditions = false;
					}
				}
				for(Pattern p : quoteBlackList){
					boolean someQuoteBreaks = false;
					for(Spinn3rDocument.Quote q : d.quotes){
						matcher = p.matcher(q.text);
						if (matcher.find()) {
							someQuoteBreaks = true;
						}
					}
					if(someQuoteBreaks){
						satisfiesConditions = false;
					}
				}
			}
			else if (removeNoQuotes){
				satisfiesConditions = false;
			}

			/**
			 * Return only those documents that satisfy search conditions
			 * */ 
			if (satisfiesConditions){
				context.write(new Text(d.toString()), NullWritable.get());
			}
		}
	}
}