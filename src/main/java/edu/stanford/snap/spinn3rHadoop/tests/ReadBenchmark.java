package edu.stanford.snap.spinn3rHadoop.tests;

import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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

public class ReadBenchmark extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ReadBenchmark(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		System.out.println(Arrays.toString(args));

		/** Job configuration */
		Job job = Job.getInstance(getConf(), "ReadBenchmark");
		job.setJarByClass(ReadBenchmark.class);
		job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(IntWritable.class);
		job.setOutputValueClass(NullWritable.class);

		/** Set Mapper and Reducer, use identity reducer*/
		job.setMapperClass(Map2.class);
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

	public static class Map2 extends Mapper<LongWritable, Text, Text, NullWritable> {
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			/** DO NOTHING! */
		}
	}
}