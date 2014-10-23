package edu.stanford.snap.spinn3rHadoop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import edu.stanford.snap.spinn3rHadoop.utils.ParseCLI;

/**
 * Spinn3rInputFilter is a filter for input.
 * It determines whether some file should be processed or not
 * according to its name and the date and content limitations
 * provided on input.
 * */
public class Spinn3rInputFilter extends Configured implements PathFilter {
	String formatInput = "yyyy-MM-dd'T'HH";
	String formatFile = "yyyy-MM";
	String formatFileDaily = "yyyy-MM-dd";
	List<String> searchContent;
	Date searchStart;
	Date searchEnd;
	Configuration conf;

	@Override
	public void setConf(Configuration conf) {
		/**
		 * Due to some strange reason the setConf is called twice, 
		 * the first time with conf=null and the second time with
		 * the actual configuration.
		 * 
		 * See also: http://hadoopi.wordpress.com/2013/07/29/hadoop-filter-input-files-used-for-mapreduce/
		 * */
		if(conf != null){
			this.conf = conf;
			String [] args = conf.getStrings("args");
			CommandLine cmd = ParseCLI.parse(args);

			try {
				searchStart = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("start"));
				searchEnd = new SimpleDateFormat(formatInput).parse(cmd.getOptionValue("end"));
				searchContent = Arrays.asList(cmd.getOptionValues("content"));
			} catch (ParseException e) {
				System.out.println("ERROR while parsing date! Message: " + e.getLocalizedMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			// debug
			System.out.println("***** INPUT ********************************************************************************************************************************************");
		}
	}


	/**
	 * Accept method gets one file path and should return T/F whether to process it or not. 
	 * */
	@Override
	public boolean accept(Path path) {
		String fileContent;
		String fileStartString = null;
		Calendar fileStart;
		Calendar fileEnd;
		Date d = null;
		int fileLength = -1;


		/** Remove if the file contents does not match */
		fileContent = path.getName().replaceAll("-.*", "").toUpperCase();
		if(!searchContent.contains(fileContent)){
			// print for debugging
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
			// print for debugging
			System.out.println(path.getName() + "\t" + "\t\t * OK *\t\t for search time: " + searchStart + "-" + searchEnd);
			return true;
		}

		// print for debugging
		System.out.println(path.getName() + "\t" + "\t\t *** NOT OK ***\t for search time: " + searchStart + "-" + searchEnd);
		return false;
	}
}