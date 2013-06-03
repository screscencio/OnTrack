package br.com.oncast.ontrack.client.utils.date;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils {

	public static long getDifferenceInMilliseconds(final Date from, final Date to) {
		return to.getTime() - from.getTime();
	}

	public static String format(final String pattern, final Date date) {
		return DateTimeFormat.getFormat(pattern).format(date);
	}

}
