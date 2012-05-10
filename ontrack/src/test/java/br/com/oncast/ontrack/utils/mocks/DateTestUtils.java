package br.com.oncast.ontrack.utils.mocks;

import java.util.Calendar;
import java.util.Date;

public class DateTestUtils {

	public static Date newDate(final int year, final int month, final int day) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		final Date declaredDay = calendar.getTime();
		return declaredDay;
	}

}
