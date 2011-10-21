package br.com.oncast.ontrack.shared.utils;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;

@SuppressWarnings("deprecation")
public class WorkingDay implements Comparable<WorkingDay>, Serializable {

	private static final long serialVersionUID = 6260976764571783140L;

	private static final int INITIAL_YEAR = 1900;
	private static final int SATURDAY = 6;
	private static final int SUNDAY = 0;

	private Date javaDate;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this and do not set any field inside it.
	WorkingDay() {}

	WorkingDay(final int year, final int month, final int day) {
		final Date date = new Date();
		date.setYear(year - INITIAL_YEAR);
		date.setMonth(month);
		date.setDate(day);
		javaDate = getNearestWorkingDayFrom(date);
	}

	WorkingDay(final Date date) {
		javaDate = getNearestWorkingDayFrom(CalendarUtil.copyDate(date));
	}

	public WorkingDay add(final int nWorkingDays) {
		final boolean forward = nWorkingDays >= 0;
		for (int i = 0; i < Math.abs(nWorkingDays); i++) {
			addOneWorkingDay(forward);
		}
		return this;
	}

	public boolean isBefore(final WorkingDay day) {
		return getDaysBetween(day) > 0;
	}

	public boolean isBeforeOrSameDayOf(final WorkingDay day) {
		return getDaysBetween(day) >= 0;
	}

	public boolean isAfter(final WorkingDay date) {
		return getDaysBetween(date) < 0;
	}

	public int countTo(final WorkingDay day) {
		if (day == null) return -1;
		int nWorkingDays = 1; // Today included
		final WorkingDay copy = copy();
		while (copy.isBefore(day)) {
			copy.add(1);
			nWorkingDays++;
		}
		return nWorkingDays;
	}

	public Date getJavaDate() {
		return CalendarUtil.copyDate(javaDate);
	}

	public WorkingDay copy() {
		return new WorkingDay(javaDate);
	}

	Date getNearestWorkingDayFrom(final Date date) {
		while (isWeekend(date))
			CalendarUtil.addDaysToDate(date, 1);
		return date;
	}

	private boolean isWeekend(final Date date) {
		final int dayOfWeek = date.getDay();
		return (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY);
	}

	private void addOneWorkingDay(final boolean forward) {
		do {
			CalendarUtil.addDaysToDate(javaDate, forward ? 1 : -1);
		} while (isWeekend(javaDate));
	}

	private int getDaysBetween(final WorkingDay day) {
		return CalendarUtil.getDaysBetween(javaDate, day.javaDate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + javaDate.getYear();
		result = prime * result + javaDate.getMonth();
		result = prime * result + javaDate.getDate();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof WorkingDay)) return false;
		final WorkingDay other = (WorkingDay) obj;

		return javaDate.getYear() == other.javaDate.getYear() && javaDate.getMonth() == other.javaDate.getMonth()
				&& javaDate.getDate() == other.javaDate.getDate();
	}

	@Override
	public int compareTo(final WorkingDay day) {
		if (equals(day)) return 0;
		return isBefore(day) ? -1 : 1;
	}

	@Override
	public String toString() {
		return getDayAndMonthString() + "/" + (javaDate.getYear() + INITIAL_YEAR);
	}

	public String getDayAndMonthString() {
		return fillWithZeroIfDataHasJustOneDigit(javaDate.getDate()) + "/" + fillWithZeroIfDataHasJustOneDigit(javaDate.getMonth() + 1);
	}

	private String fillWithZeroIfDataHasJustOneDigit(final int date) {
		String dateString = String.valueOf(date);
		if (dateString.length() == 1) dateString = "0" + dateString;

		return dateString;
	}
}
