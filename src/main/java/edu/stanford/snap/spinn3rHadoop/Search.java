package edu.stanford.snap.spinn3rHadoop;

import java.net.InetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;

public class Search extends Configured implements Tool {
	
	/** CommandLine for main and run method */
	private static CommandLine cmd = null;

	/** Counters */
	public enum ProcessingTime {
		PARSING,
		FILTERING,
		SETUP
	}

	public enum Skipped {
		SIZE_TOO_LARGE,
		DATE_MISSING
	}

	public static void main(String[] args) throws Exception {

		/** Check for arguments */
		cmd = ParseCLI.parse(args);
		if(cmd == null){
			System.exit(-1);
		}

		/** Fill in arguments from files */
		String [] args_filled = ParseCLI.replaceArgumentsFromFile(args, cmd);
		cmd = ParseCLI.parse(args_filled);
		// print arguments for debugging
		ParseCLI.printArguments(cmd);

		/** Run the job */
		int res = ToolRunner.run(new Configuration(), new Search(), args_filled);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {

		/** Get configuration, set delimiter and store arguments for maper */
		Configuration conf = getConf();
		conf.set("textinputformat.record.delimiter","\n\n");
		conf.setStrings("args", args);

		/** Set the number of output replications */
		conf.set("dfs.replication", "1");

		/** JVM PROFILING */
		//conf.setBoolean("mapreduce.task.profile", true);
		//conf.set("mapreduce.task.profile.params", "-agentlib:hprof=cpu=samples," +
		//  "heap=sites,depth=20,force=n,thread=y,verbose=n,file=%s");
		//conf.set("mapreduce.task.profile.maps", "0-2");
		//conf.set("mapreduce.task.profile.reduces", "0");

		/** Delete output directory if it exists, just for local debugging */
		if(InetAddress.getLocalHost().getHostName().contains("Niko")){  
			FileSystem fs = FileSystem.get(conf);
			fs.delete(new Path(cmd.getOptionValue("output")), true);
		}

		/** Job configuration */
		Job job = new Job(conf, getClass().getName());
		job.setJarByClass(Search.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		/** Set Maper and Reducer */
		job.setMapperClass(Spinn3rMaper.class);
		job.setReducerClass(Reducer.class); //default reducer
		if(cmd.hasOption("reducers")){
			int numReducers = Integer.valueOf(cmd.getOptionValue("reducers"));
			job.setNumReduceTasks(numReducers);
		}
		else{
			job.setNumReduceTasks(1);
		}

		/** Set input and output formats */
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		/** Set input and output path */
		//if(InetAddress.getLocalHost().getHostName().contains("ilws6")){  // for local debugging
		//	FileInputFormat.addInputPath(job, new Path("/afs/cs.stanford.edu/u/west1/web-2008-08.txt"));
		if(InetAddress.getLocalHost().getHostName().contains("Niko")){  // for local debugging
			FileInputFormat.addInputPath(job, new Path("input/*/*"));
			FileInputFormat.setInputPathFilter(job, Spinn3rInputFilter.class);
		}
		else{
			/** Add all files and than the filter will remove those that should be skipped. */
			FileInputFormat.addInputPath(job, new Path("/dataset/spinn3r/*/*"));
			FileInputFormat.setInputPathFilter(job, Spinn3rInputFilter.class);
		}
		FileOutputFormat.setOutputPath(job, new Path(cmd.getOptionValue("output")));

		/** Wait for the job to complete */
		job.waitForCompletion(true);
		return 0;
	}
}
