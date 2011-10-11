package br.com.oncast.ontrack.shared.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class DateUtils {

	/**
	 * Returns the number of days between two dates, not considering weekends.
	 */
	public static int daysBetween(final Date date1, final Date date2) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int daysBetween = 1;
		while (before(calendar.getTime(), date2)) {
			if (!isWeekend(calendar)) daysBetween++;
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return daysBetween;
	}

	/**
	 * @return true if the day of date1 is before the day of date2. <br>
	 *         Obs the hours, minutes, seconds and milliseconds are not considered.
	 */
	public static boolean before(final Date date1, final Date date2) {
		return getDateString(date1).compareTo(getDateString(date2)) < 0;
	}

	public static boolean isWeekend(final Calendar day) {
		final int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
		return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
	}

	public static Date getNextWorkingDay() {
		final Calendar workingDay = Calendar.getInstance();
		while (isWeekend(workingDay))
			workingDay.add(Calendar.DAY_OF_MONTH, 1);
		return workingDay.getTime();
	}

	public static void addWorkingDays(final Calendar calendar, final int days) {
		final int daysAbsolute = Math.abs(days);
		for (int i = 0; i < daysAbsolute; i++)
			addOneWorkingDay(calendar, days / daysAbsolute);
	}

	private static void addOneWorkingDay(final Calendar calendar, final int direction) {
		do {
			calendar.add(Calendar.DAY_OF_MONTH, 1 * direction);
		} while (DateUtils.isWeekend(calendar));
	}

	private static String getDateString(final Date startDate) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		final Formatter formatter = new Formatter();
		final Formatter string = formatter.format("%04d%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));

		return string.toString();
	}

}
