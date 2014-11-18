package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkingDayTest {

	@Test
	public void anyDayIsBeforeNull() throws Exception {
		assertTrue(WorkingDayFactory.create().isBefore(null));
	}

	@Test
	public void anyDayIsAfterNull() throws Exception {
		assertTrue(WorkingDayFactory.create().isAfter(null));
	}

	@Test
	public void todayIsBeforeFiveDaysFromNow() throws Exception {
		final WorkingDay today = WorkingDayFactory.create();

		assertTrue(today.isBefore(today.copy().add(5)));
	}

	@Test
	public void todayIsNotBeforeFiveDaysAgo() throws Exception {
		final WorkingDay today = WorkingDayFactory.create();

		assertFalse(today.isBefore(today.copy().add(-5)));
	}

	@Test
	public void todayIsBeforeTomorrow() throws Exception {
		final WorkingDay today = WorkingDayFactory.create();

		assertTrue(today.isBefore(today.copy().add(1)));
	}

	@Test
	public void todayIsNotBeforeToday() throws Exception {
		final WorkingDay today = WorkingDayFactory.create();

		assertFalse(today.isBefore(today.copy()));
	}

	@Test
	public void shouldConsiderDifferentMonthsInDatesComparation() throws Exception {
		final WorkingDay dayAtEndOfMonth = WorkingDayFactory.create(2011, Calendar.OCTOBER, 30);

		assertTrue(dayAtEndOfMonth.isBefore(dayAtEndOfMonth.copy().add(5)));
	}

	@Test
	public void whenTheGivenDayIsNullCountToShouldReturnZero() throws Exception {
		assertEquals(0, WorkingDayFactory.create().countTo(null));
	}

	@Test
	public void theNumberOfDaysFromMondayToFridayIsFive() throws Exception {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		final WorkingDay monday = WorkingDayFactory.create(calendar.getTime());

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		final WorkingDay friday = WorkingDayFactory.create(calendar.getTime());
		assertEquals(5, monday.countTo(friday));
	}

	@Test
	public void theNumberOfDaysFromFridayToMondayIsNegativeFive() throws Exception {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		final WorkingDay monday = WorkingDayFactory.create(calendar.getTime());

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		final WorkingDay friday = WorkingDayFactory.create(calendar.getTime());
		assertEquals(-5, friday.countTo(monday));
	}

	@Test
	public void oneWorkingDaysAfterTuesdayIsWednesday() throws Exception {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

		final Date finalDate = WorkingDayFactory.create(calendar.getTime()).add(1).getJavaDate();
		calendar.setTime(finalDate);

		assertEquals(Calendar.WEDNESDAY, calendar.get(Calendar.DAY_OF_WEEK));
	}

	@Test
	public void compareToShouldReturnTheDifferenceBetweenDays() throws Exception {
		final WorkingDay day1 = WorkingDayFactory.create(2011, Calendar.OCTOBER, 17);
		final WorkingDay day2 = WorkingDayFactory.create(2011, Calendar.OCTOBER, 21);
		assertEquals(-1, day1.compareTo(day2));
	}

}
