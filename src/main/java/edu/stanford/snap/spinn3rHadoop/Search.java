package edu.stanford.snap.spinn3rHadoop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
		FILTERING
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

		/** Delete output directory if it exists */
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(cmd.getOptionValue("output")), true);

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
		
		
		/** JVM PROFILING*/
		conf.setBoolean("mapred.task.profile", true);
		conf.set("mapred.task.profile.params", "-agentlib:hprof=cpu=samples,heap=sites,depth=6,force=n,thread=y,verbose=n,file=%s");
		conf.set("mapred.task.profile.maps", "0-4");
		conf.set("mapred.task.profile.reduces", "");

		/** Set input and output path */
		boolean DEBUG = true;
		if(DEBUG){
			FileInputFormat.addInputPath(job, new Path("input/web/2008-08/web-2008-08-01T00-00-00Z.txt"));
		}
		else{
			/** Add all files and than the filter will remove those that should be skipped. */
			FileInputFormat.addInputPath(job, new Path("/dataset/spinn3r2/*/*/*"));
			FileInputFormat.setInputPathFilter(job, Spinn3rInputFilter.class);
		}
		FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("output")));
		
		job.waitForCompletion(true);
		
		//fs.setReplication(new Path(cmd.getOptionValue("output")+"/*"), (short) 1);
		
		return 0;
	}
	
	/**
	 * Spinn3rInputFilter is a filter for input.
	 * It determines whether some file should be processed or not
	 * according to its name and the date and content limitations
	 * provided on input.
	 * */
	public static class Spinn3rInputFilter extends Configured implements PathFilter {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH");
		List<String> content;
		Date start;
		Date end;
		String cont;
		String dateS;
		Date date;

		@Override
		public boolean accept(Path path) {	    	
			cont = path.getName().replaceAll("-.*", "").toUpperCase();
			if(!content.contains(cont)){
				//System.out.println(path.getName() + "\t" + date + "\t\t NOT OK!");
				return false;
			}

			dateS = path.getName().replaceAll("web|fb|tw", "").substring(1, 14);
			try {
				date = format.parse(dateS);
				if( (date.after(start) || date.equals(start)) && date.before(end)){
					//System.out.println(path.getName() + "\t" + date + "\t\tOK!");
					return true;
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dateS = null;
				date = null;
			}
			//System.out.println(path.getName() + "\t" + date + "\t\tNOT OK!");
			return false;
		}

		public Spinn3rInputFilter() throws FileNotFoundException, ParseException{
			//PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
			//System.setOut(out);
			start = format.parse(cmd.getOptionValue("start"));
			end = format.parse(cmd.getOptionValue("end"));
			content = Arrays.asList(cmd.getOptionValues("content"));
		}
	}

	public static class Map extends Mapper<LongWritable, Text, Text, NullWritable> {
		//private CommandLine cmdMap;
		//private DocumentFilter filter;
		//long t1, t2;
		//boolean t;
		
		@Override
		public void setup(Context context){
			/** DO NOTHING
			cmdMap = ParseCLI.parse(context.getConfiguration().getStrings("args"));
			filter = new DocumentFilter(cmdMap);
			*/
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			/** 
			 * Parse document.
			 * */
			/**
			t1 = System.nanoTime();
			Spinn3rDocument d = new Spinn3rDocument(value.toString());
			t2 = System.nanoTime();
			context.getCounter(ProcessingTime.PARSING).increment(t2-t1);
			*/
			
			/**
			 * Return only those documents that satisfy search conditions
			 * */ 
			/**
			t1 = System.nanoTime();
			t = filter.documentSatisfies(d);
			t2 = System.nanoTime();
			context.getCounter(ProcessingTime.FILTERING).increment(t2-t1);
			if (t){
				if(cmdMap.hasOption("formatF5")){
					context.write(new Text(d.toStringF5()), NullWritable.get());
				}
				else{
					context.write(new Text(d.toString()), NullWritable.get());
				}
				
			}
			*/
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