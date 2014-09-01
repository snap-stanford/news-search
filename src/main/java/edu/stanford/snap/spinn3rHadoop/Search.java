package edu.stanford.snap.spinn3rHadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Search extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		/** Check for arguments */
		if(args.length < 2){
			System.err.println("Error: To few arguments given!");
			System.err.println("There are " + args.length + " arguments given, but there should be at least 3.");
			System.err.println("");
			System.err.println("Usage: hadoop jar <FILE.JAR> <INPUT> <OUTPUT>");
			System.exit(-1);
		}

		/** Run the job */
		int res = ToolRunner.run(new Configuration(), new Search(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		System.out.println(Arrays.toString(args));

		/** Get donfiguration */
		Configuration conf = new Configuration(true);
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

		/** Set input and output formats */
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		/** Set input and output path */
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
		return 0;
	}

	public static class Map extends Mapper<LongWritable, Text, Text, NullWritable> {
		private Matcher matcher = null;
		//private Pattern urlWhiteList = Pattern.compile("\\.pt|\\.com|\\.uk");
		//private Pattern urlBlackList = Pattern.compile("olhares.aeiou.pt");
		private Pattern urlWhiteList = Pattern.compile("");
		private Pattern urlBlackList = Pattern.compile("^$");

		private boolean removeNoLanguage = true;
		private String [] langWhiteList = {};
		private String [] langBlackList = {};
		
		private boolean removeGarbled = true;

		private String [] removeVersions = {};
		
		private boolean removeEmptyTitle = true;
		private Pattern titleWhiteList = Pattern.compile("&#039;[oO]bama");
		private Pattern titleBlackList = Pattern.compile("philanthropist");

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
			 * 		for document to to pass it must match with the whiteList and 
			 * 		it must not match with the blackList
			 * */
			if(d.title != null){
				matcher = titleWhiteList.matcher(d.title);
				if (!matcher.find()) {
					satisfiesConditions = false;
				}
				matcher = titleBlackList.matcher(d.title);
				if(matcher.find()){
					satisfiesConditions = false;
				}
			}
			else if (removeEmptyTitle){
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