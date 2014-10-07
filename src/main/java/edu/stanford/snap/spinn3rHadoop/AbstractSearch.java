package edu.stanford.snap.spinn3rHadoop;

import java.io.FileNotFoundException;
import java.net.InetAddress;
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
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;

public abstract class AbstractSearch extends Configured implements Tool {
  
	public enum ProcessingTime {
		PARSING,
		FILTERING,
		SETUP,
		SKIPPED
	}
	
  //////////////////
	private CommandLine cmd = null;
	
	/** Store the command line and return the updated args */
	protected String[] setCommandLine(String[] args) throws FileNotFoundException {
    /** Check for arguments */
    cmd = ParseCLI.parse(args);
    if(cmd == null){
      System.exit(-1);
    }
	  
    /** Fill in arguments from file */
    String [] new_args = ParseCLI.replaceArgumentsFromFile(args, cmd);
    cmd = ParseCLI.parse(new_args);
    ParseCLI.printArguments(cmd);
    return new_args;
	}
	
	protected CommandLine getCommandLine() {
	  return cmd;
	}

  public abstract Class<? extends Mapper<? extends Writable, ? extends Writable, ? extends Writable, ? extends Writable>> getMapperClass();

  @SuppressWarnings("rawtypes")
  public Class<? extends Reducer> getReducerClass() {
    return Reducer.class;
  }

	@Override
	public int run(String[] args) throws Exception {
    //////////////////
	  String[] new_args = setCommandLine(args);

		/** Get configuration */
		Configuration conf = getConf();
		conf.set("textinputformat.record.delimiter","\n\n");
		conf.setStrings("args", new_args);
		
		/** Set the number of output replications */
		conf.set("dfs.replication", "1");

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
		job.setJarByClass(AbstractSearch.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		/** Set Mapper and Reducer, use identity reducer*/
    //////////////////
		job.setMapperClass(getMapperClass());
		if(cmd.hasOption("reducers")){
			int numReducers = Integer.valueOf(cmd.getOptionValue("reducers"));
      //////////////////
			job.setReducerClass(getReducerClass());
			job.setNumReduceTasks(numReducers);
		}
		else{
			job.setNumReduceTasks(1);
			//////////////////
			job.setReducerClass(getReducerClass());
		}

		/** Set input and output formats */
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		/** Set input and output path */
		if(InetAddress.getLocalHost().getHostName().contains("Niko")){ 	// for local debugging
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
  ////////////////// now non-static
	public static class Spinn3rInputFilter extends Configured implements PathFilter {
		String formatInput = "yyyy-MM-dd'T'HH";
		String formatFile = "yyyy-MM";
		String formatFileDaily = "yyyy-MM-dd";
		List<String> searchContent;
		Date searchStart;
		Date searchEnd;
		Configuration conf;
		
		
		@Override
	    public void setConf(Configuration conf) {
	        this.conf = conf;
	        System.out.println("Inside setconf");
	        System.out.println(conf);
	        if(conf != null){
	        	System.out.println(Arrays.asList(conf.getStrings("args")));
	        	this.conf = conf;
	        	
	        	String [] args = conf.getStrings("args");
	        	CommandLine cmd = ParseCLI.parse(args);
				System.out.println("Ahoj2");
				try {
					searchStart = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("start"));
					searchEnd = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("end"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchContent = Arrays.asList(cmd.getOptionValues("content"));
	        }
	        /**
	        String [] args = .getStrings("args");
	        CommandLine cmd = ParseCLI.parse(args);
			System.out.println("Ahoj2");
			try {
				searchStart = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("start"));
				searchEnd = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("end"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchContent = Arrays.asList(cmd.getOptionValues("content"));
			*/
	    }
		
		//public Spinn3rInputFilter(Configuration c) throws FileNotFoundException, ParseException{
		//	super(c);
			
			/** Parse job limitation form command line */
      //////////////////
			/**
			System.out.println("Ahoj!");
			System.out.println(conf);
			String [] args = getConf().getStrings("args");
			System.out.println("Ahoj1");
			System.out.println(Arrays.asList(args));
			
			CommandLine cmd = ParseCLI.parse(args);
			System.out.println("Ahoj2");
			searchStart = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("start"));
			searchEnd = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("end"));
			searchContent = Arrays.asList(cmd.getOptionValues("content"));
			*/
		//}


		/**
		 * Accept method gets one file path and should return T/F whether to process it or not. 
		 * */
		@Override
		public boolean accept(Path path) {
			
			System.out.println("Ahoj before");
			System.out.println(Arrays.asList(conf.getStrings("args")));
			System.out.println("Ahoj after");
			
			
			String fileContent;
			String fileStartString = null;
			Calendar fileStart;
			Calendar fileEnd;
			Date d = null;
			int fileLength = -1;


			/** Remove if the file contents does not match */
			fileContent = path.getName().replaceAll("-.*", "").toUpperCase();
			if(!searchContent.contains(fileContent)){
				System.out.println(path.getName() + "\t\t NOT OK - WRONG CONTENT TYPE!");
				return false;
			}

			/** Check date constraints */
			try {
				/** Special case for daily files from the current month */
				if(path.getName().contains("daily")){
					fileLength = Calendar.DAY_OF_MONTH;
					fileStartString = path.getName().replaceAll("web|fb|tw", "").substring(1, 11);
					d = new SimpleDateFormat(formatFileDaily).parse(fileStartString);
				}
				/** Regular monthly files */
				else{
					fileLength = Calendar.MONTH;
					fileStartString = path.getName().replaceAll("web|fb|tw", "").substring(1, 8);
					d = new SimpleDateFormat(formatFile).parse(fileStartString);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			/** Calculate file start and end date */
			fileStart = Calendar.getInstance();
			fileStart.setTime(d);
			fileEnd = Calendar.getInstance();
			fileEnd.setTime(fileStart.getTime());
			fileEnd.add(fileLength, 1);

			/** Check if we should process it or not, depending on date */
			if( fileStart.getTime().before(searchEnd) && fileEnd.getTime().after(searchStart) ){
				System.out.println(path.getName() + "\t" + "\t\t * OK *\t\t for search time: " + searchStart + "-" + searchEnd);
				return true;
			}

			System.out.println(path.getName() + "\t" + "\t\t *** NOT OK ***\t for search time: " + searchStart + "-" + searchEnd);
			return false;
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