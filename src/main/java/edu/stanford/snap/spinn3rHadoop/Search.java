package edu.stanford.snap.spinn3rHadoop;

import java.io.IOException;
import java.util.Arrays;

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

import edu.stanford.snap.spinn3rHadoop.utils.DocumentFilter;
import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;
import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;

public class Search extends Configured implements Tool {
	private static CommandLine cmd = null;
	
	public static void main(String[] args) throws Exception {
		/** Check for arguments */
		cmd = ParseCLI.parse(args);
		ParseCLI.printArguments(cmd);
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
		private DocumentFilter filter;
		
		@Override
		public void setup(Context context){
			filter = new DocumentFilter(cmd);
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			/** 
			 * Parse document.
			 * */
			Spinn3rDocument d = new Spinn3rDocument(value.toString());

			/**
			 * Return only those documents that satisfy search conditions
			 * */ 
			if (filter.documentSatisfies(d)){
				context.write(new Text(d.toString()), NullWritable.get());
			}
		}
	}
}

/**
 * 
-output out
-startDate 2010-12-13T23
-endDate 2013-09-02T17
-content WEB FB TW
-titleWL '[Oo]bama' '[Bb]arack|[Mm]ichelle'
-titleBL '[Mm]ccain' 'perry rosenstein'
 * */