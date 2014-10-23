package edu.stanford.snap.spinn3rHadoop;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.stanford.snap.spinn3rHadoop.Search.ProcessingTime;
import edu.stanford.snap.spinn3rHadoop.utils.DocumentFilter;
import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;
import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public class Spinn3rMaper extends Mapper<LongWritable, Text, Text, NullWritable> {
	private Integer MAX_CONTENT_LENGTH_FOR_FILTERING = 1000000;
	private CommandLine cmdMap;
	private DocumentFilter filter;
	long t1, t2;
	boolean t;

	/** Called once at the very beginning */
	@Override
	public void setup(Context context) {
		t1 = System.nanoTime();
		cmdMap = ParseCLI.parse(context.getConfiguration().getStrings("args"));
		filter = new DocumentFilter(cmdMap);
		t2 = System.nanoTime();
		context.getCounter(ProcessingTime.SETUP).increment(t2 - t1);
	}

	/** Called for every record in the data */
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException,
	InterruptedException {
		/**
		 * Parse document and measure time
		 * */
		t1 = System.nanoTime();
		Spinn3rDocument d = new Spinn3rDocument(value.toString());
		t2 = System.nanoTime();
		context.getCounter(ProcessingTime.PARSING).increment(t2-t1);

		/**
		 * Return only those documents that satisfy search conditions
		 * */
		t1 = System.nanoTime();
		/** We skip files with VERY large content, since regex are not able to handle them */
		if (d.content == null || (d.content != null && d.content.length() < MAX_CONTENT_LENGTH_FOR_FILTERING)) {
			t = filter.documentSatisfies(d);
		} else {
			t = false;
			context.getCounter(ProcessingTime.SKIPPED).increment(1);
		}
		t2 = System.nanoTime();
		context.getCounter(ProcessingTime.FILTERING).increment(t2-t1);

		/**
		 * Output if satisfies
		 * */
		if (t) {
			if (cmdMap.hasOption("formatF5")) {
				context.write(new Text(d.toStringF5()), NullWritable.get());
			} else {
				context.write(new Text(d.toString()), NullWritable.get());
			}
		}
	}

	/** Called once at the end */
	@Override
	public void cleanup(Context context) {
	}
}
