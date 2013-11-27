package br.com.oncast.ontrack.client.utils.date;

import java.util.Date;

import com.google.gwt.core.client.GWT;

import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getDifferenceUnit;
import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getRelativeUnit;

public class HumanDateFormatter {

	static final HumanDateFormatterMessages MESSAGES = GWT.create(HumanDateFormatterMessages.class);

	private static final int DEFAULT_DECIMAL_UNITS = 0;

	private int decimalDigits = DEFAULT_DECIMAL_UNITS;

	private HumanDateUnit minimum = HumanDateUnit.getSmallest();

	private HumanDateUnit maximum = HumanDateUnit.getBiggest();

	private HumanDateFormatter() {}

	public static HumanDateFormatter get() {
		return new HumanDateFormatter();
	}

	public HumanDateFormatter setDecimalDigits(final int decimalDigits) {
		this.decimalDigits = decimalDigits;
		return this;
	}

	public HumanDateFormatter setMinimum(final HumanDateUnit minimun) {
		this.minimum = minimun;
		return this;
	}

	public HumanDateFormatter setMaximum(final HumanDateUnit maximum) {
		this.maximum = maximum;
		return this;
	}

	public HumanDateFormatter setUnit(final HumanDateUnit unit) {
		setMinimum(unit);
		setMaximum(unit);
		return this;
	}

	public String formatTimeDifference(final Date from, final Date to) {
		return getTimeDifferenceFormat(from, to).toString();
	}

	public String formatTimeDifference(final long differenceInMilliseconds) {
		return getTimeDifferenceFormat(differenceInMilliseconds).toString();
	}

	public TimeDifferenceFormat getTimeDifferenceFormat(final Date from, final Date to) {
		return getTimeDifferenceFormat(DateUtils.getDifferenceInMilliseconds(from, to));
	}

	public TimeDifferenceFormat getTimeDifferenceFormat(final long differenceInMilliseconds) {
		final HumanDateUnit unit = getDifferenceUnit(differenceInMilliseconds, minimum, maximum);
		return new TimeDifferenceFormat(differenceInMilliseconds, decimalDigits, unit);
	}

	public String formatDateRelativeToNow(final Date date) {
		return formatDateRelativeTo(date, new Date());
	}

	public String formatDateRelativeTo(final Date date, final Date relativeTo) {
		final HumanDateUnit unit = getRelativeUnit(date, relativeTo, minimum, maximum);
		return new RelativeDateFormat(date, relativeTo, decimalDigits, unit).toString();
	}

	public static String formatAbsoluteDate(final Date date) {
		return DateUtils.format("EEE, dd/MM/yyyy '" + MESSAGES.at() + "' hh:mm:ss", date);
	}

	public static String formatShortAbsoluteDate(final Date date) {
		return DateUtils.format("dd/MM/yy", date);
	}

}
