package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.HOURS;
import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.ONE_DAY;
import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.YEARS;
import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.apache.commons.lang.time.DateUtils.addMinutes;
import static org.apache.commons.lang.time.DateUtils.addMonths;
import static org.apache.commons.lang.time.DateUtils.addSeconds;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.googlecode.gwt.test.GwtTest;
import com.ibm.icu.util.Calendar;

public class HumanDateFormatterTest extends GwtTest {

	private static final HumanDateFormatterMessages MESSAGES = GWT.create(HumanDateFormatterMessages.class);
	private DateTimeFormat hourAndMinute;
	private DateTimeFormat weekdayAndDayOfMonth;
	private DateTimeFormat monthAndDayOfMonth;
	private HumanDateFormatter2 formatter;
	private Date now;

	@Before
	public void setup() throws Exception {
		hourAndMinute = DateTimeFormat.getFormat("HH:mm");
		weekdayAndDayOfMonth = DateTimeFormat.getFormat("EE d");
		monthAndDayOfMonth = DateTimeFormat.getFormat("MMM d");
		formatter = HumanDateFormatter2.get();
		now = new Date();
	}

	@Test
	public void theDifferenceOfSameDateIsNow() throws Exception {
		assertEquals(MESSAGES.now(), formatter.formatDifferenceText(now, now));
	}

	@Test
	public void theDifferenceDateOfTwoDatesWithOneScondDifferenceIsOneSecond() throws Exception {
		final Date to = addSeconds(now, 1);
		assertEquals(1 + " " + MESSAGES.second(), formatter.formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceDateOfTwoDatesWithThreeScondsDifferenceIsThreeSeconds() throws Exception {
		final Date to = addSeconds(now, 3);
		assertEquals(3 + " " + MESSAGES.seconds(), formatter.formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceDateOfTwoDatesWithOneMinuteOfDifferenceIsOneMinute() throws Exception {
		final Date to = addMinutes(now, 1);
		assertEquals(1 + " " + MESSAGES.minute(), formatter.formatDifferenceText(now, to));
	}

	@Test
	public void itsPossibleToConfigureDecimalDigitsToBeRounded() throws Exception {
		final Date to = addSeconds(addMinutes(now, 1), 15);
		assertEquals(1.2 + " " + MESSAGES.minutes(), formatter.setDecimalDigits(1).formatDifferenceText(now, to));
	}

	@Test
	public void unnecessaryZerosAtRightAreChopped() throws Exception {
		final Date to = addSeconds(addMinutes(now, 1), 15);
		assertEquals(1.25 + " " + MESSAGES.minutes(), formatter.setDecimalDigits(5).formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceTextOfTwoDatesWithLessThanADayOfDifferenceIsTodayWhenTheMinimunUnitIsDay() throws Exception {
		final Date to = addMinutes(now, 43);
		assertEquals(MESSAGES.today(), formatter.setMinimum(ONE_DAY).formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceTextOfTwoDatesWithFiveDaysDifferenceIsFiveDaysWhenTheMinimunUnitIsDay() throws Exception {
		final Date to = addDays(now, 5);
		assertEquals(5 + " " + MESSAGES.days(), formatter.setMinimum(ONE_DAY).formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceTextOfTwoDatesWithFiveDaysDifferenceIs120HoursWhenTheUnitIsHours() throws Exception {
		final Date to = addDays(now, 5);
		assertEquals(120 + " " + MESSAGES.hours(), formatter.setUnit(HOURS).formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceTextOfTwoDatesWithFiveDaysNegativeDifferenceIsMinus5Days() throws Exception {
		final Date to = addDays(now, -5);
		assertEquals(-5 + " " + MESSAGES.days(), formatter.formatDifferenceText(now, to));
	}

	@Test
	public void theDifferenceTextOfTwoDatesWithFiveDaysNegativeDifferenceIsMinusPointFiveYearsWhenTheUnitIsYearsAndDecimalDigitsIsOne() throws Exception {
		final Date to = addMonths(now, -6);
		assertEquals(-0.5 + " " + MESSAGES.years(), formatter.setDecimalDigits(1).setUnit(YEARS).formatDifferenceText(now, to));
	}

	@Test
	public void theRelativeDateTextIsNowWhenDifferenceIsLessThanAMinute() throws Exception {
		final Date date = date(12, 12, 12, 13);
		final Date relativeTo = date(12, 12, 12, 00);
		assertEquals(MESSAGES.now(), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsNowWhenDifferenceIsLessThanMinusMinuteInThePast() throws Exception {
		final Date date = date(12, 12, 12, 00);
		final Date relativeTo = date(12, 12, 12, 15);
		assertEquals(MESSAGES.now(), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsInHourMinuteFormatWhenDifferenceIsLessThanAnHourAndMoreThanMinute() throws Exception {
		final Date relativeTo = addMinutes(now, 44);
		assertEquals(hourAndMinute.format(now), formatter.formatDateRelativeTo(now, relativeTo));
	}

	@Test
	public void theSameRelativeDateFormattingRulesAppliesWhenRelativeDateIsBeforeTheGivenDate() throws Exception {
		final Date relativeTo = addMinutes(now, -44);
		assertEquals(hourAndMinute.format(now), formatter.formatDateRelativeTo(now, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsTomorrowAndHourMinuteFormatWhenDifferenceIsLessThanADayAndMoreThanMinute() throws Exception {
		final Date date = date(12, 11, 00);
		final Date relativeTo = date(11, 12, 00);

		assertEquals(MESSAGES.tomorrow() + " " + MESSAGES.at() + " " + hourAndMinute.format(date), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsYesterdayAndHourMinuteFormatWhenDifferenceIsLessThanADayAndMoreThanMinuteButRelativeDateIsLater() throws Exception {
		final Date date = date(11, 12, 00);
		final Date relativeTo = date(12, 11, 00);

		assertEquals(MESSAGES.yesterday() + " " + MESSAGES.at() + " " + hourAndMinute.format(date), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsHourMinuteFormatWhenDifferenceIsMoreThanMinuteAndIsInSameDay() throws Exception {
		final Date date = date(12, 23, 59);
		final Date relativeTo = date(12, 00, 01);

		assertEquals(hourAndMinute.format(date), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsWeekdayDayOfMonthFormatWhenDifferenceIsMoreThanOneDayAndLessThanAWeek() throws Exception {
		final Date date = date(27, 23, 59);
		final Date relativeTo = date(30, 00, 01);

		assertEquals(weekdayAndDayOfMonth.format(date), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsMonthAndDayOfMonthFormatWhenDifferenceIsMoreThanOneWeekAndLessThanAMonth() throws Exception {
		final Date date = date(21, 23, 59);
		final Date relativeTo = date(29, 00, 01);

		assertEquals(monthAndDayOfMonth.format(date), formatter.formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsTodayWhenMinimumIsDayWhenTheRelativeDateIsInSameDay() throws Exception {
		final Date date = date(21, 23, 59);
		final Date relativeTo = date(21, 18, 01);

		assertEquals(MESSAGES.today(), formatter.setMinimum(ONE_DAY).formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsTodayWhenMinimumIsDayWhenTheRelativeDateIsInSameDayAndAfterTheGivenDate() throws Exception {
		final Date date = date(21, 21, 59);
		final Date relativeTo = date(21, 18, 01);

		assertEquals(MESSAGES.today(), formatter.setMinimum(ONE_DAY).formatDateRelativeTo(date, relativeTo));
	}

	@Test
	public void theRelativeDateTextIsTomorrowWhenMinimumIsDayAndTheGivenDateIsOneDayAfter() throws Exception {
		final Date date = date(22, 18, 01);
		final Date relativeTo = date(21, 23, 59);

		assertEquals(MESSAGES.tomorrow(), formatter.setMinimum(ONE_DAY).formatDateRelativeTo(date, relativeTo));
	}

	private Date date(final int day, final int hour, final int minute) {
		return date(day, hour, minute, 0);
	}

	private Date date(final int days, final int hours, final int minutes, final int seconds) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 5, days, hours, minutes, seconds);
		return calendar.getTime();
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
