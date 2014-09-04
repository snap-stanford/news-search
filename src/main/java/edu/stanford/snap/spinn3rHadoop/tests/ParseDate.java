package edu.stanford.snap.spinn3rHadoop.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseDate {
	public static void main(String[] args) throws ParseException {
		/**
		 * When lanient=false some of the following records can not be parsed
		 * but they can be if lanient=true.
		 * 
		 * The following dates can only be parsed when lenient = TRUE!
		 * Otherwise they throw error. Also the parsing shifts the clock for one hour.
		 * 2011-03-13T02  >  Sun Mar 13 01:00:00 PST 2011
		 * 2012-03-11T02  >  Sun Mar 11 01:00:00 PST 2012
		 * 2013-03-10T02  >  Sun Mar 10 01:00:00 PST 2013
		 * */
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH");
		format.setLenient(true);
		Date date1 = format.parse("2011-03-13T01");
		System.out.println(date1);
		Date date2 = format.parse("2011-03-13T02");
		System.out.println(date2);

	}

}
