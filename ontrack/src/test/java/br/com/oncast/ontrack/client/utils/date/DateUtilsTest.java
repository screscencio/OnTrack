package br.com.oncast.ontrack.client.utils.date;

import java.util.Date;

import org.junit.Test;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

	@Test
	public void shouldCalculateTheTimeBetweenDates_WhenFromIsBeforeTo() {
		final Date from = new Date();
		final Date to = new Date();
		CalendarUtil.addDaysToDate(to, 3);
		final long difference = to.getTime() - from.getTime();

		assertEquals(difference, DateUtils.getDifferenceInMilliseconds(from, to));
	}

	@Test
	public void shouldCalculateTheTimeBetweenDates_WhenFromIsAfterTo() {
		final Date from = new Date();
		final Date to = new Date();
		CalendarUtil.addDaysToDate(to, -3);
		final long difference = to.getTime() - from.getTime();

		assertEquals(difference, DateUtils.getDifferenceInMilliseconds(from, to));
	}

	// @Test
	// public void shouldCalculateTheTimeBetweenDates_WhenFromIsAfterTo() {
	// final Date from = new Date();
	// final Date to = new Date();
	// CalendarUtil.addDaysToDate(to, -3);
	// final long difference = to.getTime() - from.getTime();
	//
	// assertEquals(difference, DateUtils.getDifferenceInMilliseconds(from, to));
	// }
}