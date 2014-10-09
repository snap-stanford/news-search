package edu.stanford.snap.spinn3rHadoop;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

import edu.stanford.snap.spinn3rHadoop.utils.DocumentFilter;
import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;
import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public class Search extends AbstractSearch {

  public static class Map extends Mapper<LongWritable, Text, Text, NullWritable> {
	private Integer MAX_CONTENT_LENGTH_FOR_FILTERING = 1000000;
    private CommandLine cmdMap;
    private DocumentFilter filter;
    long t1, t2;
    boolean t;

    @Override
    public void setup(Context context) {
      t1 = System.nanoTime();
      cmdMap = ParseCLI.parse(context.getConfiguration().getStrings("args"));
      filter = new DocumentFilter(cmdMap);
      t2 = System.nanoTime();
      context.getCounter(ProcessingTime.SETUP).increment(t2 - t1);
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException,
        InterruptedException {
      /**
       * Parse document.
       * */
      t1 = System.nanoTime();
      Spinn3rDocument d = new Spinn3rDocument(value.toString());
      t2 = System.nanoTime();
      context.getCounter(ProcessingTime.PARSING).increment(t2 - t1);

      /**
       * Return only those documents that satisfy search conditions
       * */
      t1 = System.nanoTime();
      // TODO fix for large contents
      if (d.content == null || (d.content != null && d.content.length() < MAX_CONTENT_LENGTH_FOR_FILTERING)) {
        t = filter.documentSatisfies(d);
      } else {
        context.getCounter(ProcessingTime.SKIPPED).increment(1);
        t = false;
      }
      t2 = System.nanoTime();
      context.getCounter(ProcessingTime.FILTERING).increment(t2 - t1);

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

    @Override
    public void cleanup(Context context) {
    }
  }

  @Override
  public Class<? extends Mapper<LongWritable, Text, Text, NullWritable>> getMapperClass() {
    return Map.class;
  }

  public static void main(String[] args) throws Exception {
    /** Run the job */
    int res = ToolRunner.run(new Configuration(), new Search(), args);
    System.exit(res);
  }

}
