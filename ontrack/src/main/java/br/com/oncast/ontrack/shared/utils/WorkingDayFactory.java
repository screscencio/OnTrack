package br.com.oncast.ontrack.shared.utils;

import java.util.Date;

public class WorkingDayFactory {

	public static WorkingDay create() {
		return new WorkingDay(new Date());
	}

	public static WorkingDay create(final Date date) {
		return new WorkingDay(date);
	}

	public static WorkingDay create(final int year, final int month, final int day) {
		return new WorkingDay(year, month, day);
	}
}
