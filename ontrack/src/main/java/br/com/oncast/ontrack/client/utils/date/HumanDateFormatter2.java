package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getDifferenceUnit;
import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getRelativeUnit;

import java.util.Date;

import com.google.gwt.core.client.GWT;

public class HumanDateFormatter2 {

	static final HumanDateFormatterMessages MESSAGES = GWT.create(HumanDateFormatterMessages.class);

	private static final int DEFAULT_DECIMAL_UNITS = 0;

	private int decimalDigits = DEFAULT_DECIMAL_UNITS;

	private HumanDateUnit minimum = HumanDateUnit.getSmallest();

	private HumanDateUnit maximum = HumanDateUnit.getBiggest();

	private HumanDateFormatter2() {}

	public static HumanDateFormatter2 get() {
		return new HumanDateFormatter2();
	}

	public HumanDateFormatter2 setDecimalDigits(final int decimalDigits) {
		this.decimalDigits = decimalDigits;
		return this;
	}

	public HumanDateFormatter2 setMinimum(final HumanDateUnit minimun) {
		this.minimum = minimun;
		return this;
	}

	public HumanDateFormatter2 setMaximum(final HumanDateUnit maximum) {
		this.maximum = maximum;
		return this;
	}

	public HumanDateFormatter2 setUnit(final HumanDateUnit unit) {
		setMinimum(unit);
		setMaximum(unit);
		return this;
	}

	public String formatTimeDifference(final Date from, final Date to) {
		return formatTimeDifference(DateUtils.getDifferenceInMilliseconds(from, to));
	}

	public String formatTimeDifference(final long differenceInMilliseconds) {
		final HumanDateUnit unit = getDifferenceUnit(differenceInMilliseconds, minimum, maximum);
		return new TimeDifferenceFormat(differenceInMilliseconds, decimalDigits, unit).toString();
	}

	public String formatDateRelativeTo(final Date date, final Date relativeTo) {
		final HumanDateUnit unit = getRelativeUnit(date, relativeTo, minimum, maximum);
		return new RelativeDateFormat(date, relativeTo, decimalDigits, unit).toString();
	}

	public String formatAbsoluteDate(final Date date) {
		return DateUtils.format("EEE, dd/MM/yyyy '" + MESSAGES.at() + "' hh:mm:ss", date);
	}

	public static String formatShortAbsuluteDate(final Date date) {
		return DateUtils.format("dd/MM/yy", date);
	}

}
