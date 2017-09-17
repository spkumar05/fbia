package com.hearst.fbia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConversion {
	public static void main(String[] args) throws ParseException {
		String input = "9/25/2017 3:13:01 PM";

		SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
		Date date = df1.parse(input);

		System.out.println(date);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		String isoDate = df.format(date);
		System.out.println(isoDate);
	}
}