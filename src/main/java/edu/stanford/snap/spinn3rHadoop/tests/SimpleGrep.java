package edu.stanford.snap.spinn3rHadoop.tests;

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

public class SimpleGrep extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		/** Check for arguments */
		if(args.length < 3){
			System.err.println("Error: To few arguments given!");
			System.err.println("There are " + args.length + " arguments given, but there should be at least 3.");
			System.err.println("");
			System.err.println("Usage: hadoop jar <FILE.JAR> <CLASS> <INPUT> <OUTPUT> <SEARCH-PATTERN>");
			System.exit(-1);
		}

		/** Run the job */
		int res = ToolRunner.run(new Configuration(), new SimpleGrep(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		System.out.println(Arrays.toString(args));

		/** Copy pattern so we can access it from the Mapper */
		Configuration conf = getConf();
		conf.set("pattern", args[2]);

		/** Delete output directory if it exists */
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(args[1]), true);

		/** Job configuration */
		Job job = Job.getInstance(conf, "SimpleGrep");
		
		job.setJarByClass(SimpleGrep.class);
		job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(IntWritable.class);
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
		private Pattern pattern;
		private Matcher matcher;

		/** Load the patter in the beginning */
		@Override
		protected void setup(Context context) throws IOException{
			/** Get pattern from configuration and create a Pattern object */
			Configuration conf = context.getConfiguration();
			String ptrn = conf.get("pattern");
			pattern = Pattern.compile(ptrn);
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			System.out.println("KEY:" + key);
			System.out.println("VALUE:" + value);

			/** Create matcher object */
			matcher = pattern.matcher(value.toString());

			/** Search */
			if (matcher.find( )) {
				context.write(value, NullWritable.get());
			}
		}
	}
}