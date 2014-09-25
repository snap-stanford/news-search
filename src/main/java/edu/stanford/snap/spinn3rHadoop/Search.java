package edu.stanford.snap.spinn3rHadoop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
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

import edu.stanford.snap.spinn3rHadoop.utils.DocumentFilter;
import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;
import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public class Search extends Configured implements Tool {
	enum ProcessingTime{
		PARSING,
		FILTERING,
		SETUP,
		SKIPPED
	}
	private static CommandLine cmd = null;

	public static void main(String[] args) throws Exception {

		/** Check for arguments */
		cmd = ParseCLI.parse(args);
		if(cmd == null){
			System.exit(-1);
		}
		
		/** Fill in arguments from file */
		String [] new_args = ParseCLI.replaceArgumentsFromFile(args, cmd);
		cmd = ParseCLI.parse(new_args);
		ParseCLI.printArguments(cmd);

		/** Run the job */
		int res = ToolRunner.run(new Configuration(), new Search(), new_args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {

		/** Get configuration */
		Configuration conf = getConf();
		conf.set("textinputformat.record.delimiter","\n\n");
		conf.setStrings("args", args);
		
		/** JVM PROFILING */
		//conf.setBoolean("mapreduce.task.profile", true);
		//conf.set("mapreduce.task.profile.params", "-agentlib:hprof=cpu=samples," +
		//  "heap=sites,depth=20,force=n,thread=y,verbose=n,file=%s");
		//conf.set("mapreduce.task.profile.maps", "0-2");
		//conf.set("mapreduce.task.profile.reduces", "0");
		
		/** Delete output directory if it exists */
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(cmd.getOptionValue("output")), true);

		/** Job configuration */
		Job job = new Job(conf, "HadoopSearch");
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
		boolean DEBUG = false;
		if(DEBUG){
			FileInputFormat.addInputPath(job, new Path("input/*/*"));
			FileInputFormat.setInputPathFilter(job, Spinn3rInputFilter.class);
		}
		else{
			/** Add all files and than the filter will remove those that should be skipped. */
			FileInputFormat.addInputPath(job, new Path("/dataset/spinn3r/*/*"));
			FileInputFormat.setInputPathFilter(job, Spinn3rInputFilter.class);
		}
		FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("output")));
		
		job.waitForCompletion(true);
		
		return 0;
	}
	
	/**
	 * Spinn3rInputFilter is a filter for input.
	 * It determines whether some file should be processed or not
	 * according to its name and the date and content limitations
	 * provided on input.
	 * */
	public static class Spinn3rInputFilter extends Configured implements PathFilter {
		SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH");
		SimpleDateFormat formatFile = new SimpleDateFormat("yyyy-MM");
		List<String> searchContent;
		Date searchStart;
		Date searchEnd;
		
		public Spinn3rInputFilter() throws FileNotFoundException, ParseException{
			searchStart = formatInput.parse(cmd.getOptionValue("start"));
			searchEnd = formatInput.parse(cmd.getOptionValue("end"));
			searchContent = Arrays.asList(cmd.getOptionValues("content"));
		}

		@Override
		public boolean accept(Path path) {
			String fileContent;
			String fileStartString;
			Calendar fileStart;
			Calendar fileEnd;
			
			fileContent = path.getName().replaceAll("-.*", "").toUpperCase();
			if(!searchContent.contains(fileContent)){
				System.out.println(path.getName() + "\t\t NOT OK - WRONG CONTENT TYPE!");
				return false;
			}

			fileStartString = path.getName().replaceAll("web|fb|tw", "").substring(1, 8);
			try {
				fileStart = Calendar.getInstance();
				fileStart.setTime(formatFile.parse(fileStartString));
				fileEnd = Calendar.getInstance();
				fileEnd.setTime(fileStart.getTime());
				fileEnd.add(Calendar.MONTH, 1); // each file contains one month
				if( fileStart.getTime().before(searchEnd) && fileEnd.getTime().after(searchStart) ){
					System.out.println(path.getName() + "\t" + "\t\t * OK *\t\t for search time: " + searchStart + "-" + searchEnd);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println(path.getName() + "\t" + "\t\t *** NOT OK ***\t for search time: " + searchStart + "-" + searchEnd);
			return false;
		}
	}

	public static class Map extends Mapper<LongWritable, Text, Text, NullWritable> {
		private CommandLine cmdMap;
		private DocumentFilter filter;
		long t1, t2;
		boolean t;
		
		@Override
		public void setup(Context context){
			t1 = System.nanoTime();
			cmdMap = ParseCLI.parse(context.getConfiguration().getStrings("args"));
			filter = new DocumentFilter(cmdMap);
			t2 = System.nanoTime();
			context.getCounter(ProcessingTime.SETUP).increment(t2-t1);
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			/** 
			 * Parse document.
			 * */
			t1 = System.nanoTime();
			Spinn3rDocument d = new Spinn3rDocument(value.toString());
			t2 = System.nanoTime();
			context.getCounter(ProcessingTime.PARSING).increment(t2-t1);
			
			/**
			 * Return only those documents that satisfy search conditions
			 * */ 
			t1 = System.nanoTime();
			//TODO fix for large contents
			if(d.content != null && d.content.length() < 10000){
				t = filter.documentSatisfies(d);
			}
			else{
				context.getCounter(ProcessingTime.SKIPPED).increment(1);
				t = false;
			}
			t2 = System.nanoTime();
			context.getCounter(ProcessingTime.FILTERING).increment(t2-t1);
			
			/**
			if (t){
				if(cmdMap.hasOption("formatF5")){
					context.write(new Text(d.toStringF5()), NullWritable.get());
				}
				else{
					context.write(new Text(d.toString()), NullWritable.get());
				}
				
			}*/
		}
		
		@Override
		public void cleanup(Context context){
		}
		
	}
}

/**
 * 
-output out
-start 2010-12-13T23
-end 2013-09-02T17
-content WEB FB TW
-titleWL '[Oo]bama' '[Bb]arack|[Mm]ichelle'
-titleBL '[Mm]ccain' 'perry rosenstein'
 
-output out -start 2010-01-01T00 -end 2010-01-10T23 -content WEB FB TW -titleWL '[Ss]lovenia'
 * */