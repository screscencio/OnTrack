package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getDifferenceUnit;
import static br.com.oncast.ontrack.client.utils.date.HumanDateUnit.getRelativeUnit;

import java.util.Date;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

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

	public String formatDifferenceText(final Date from, final Date to) {
		final HumanDateUnit unit = getDifferenceUnit(from, to, minimum, maximum);
		final float roundedDifference = round(unit.getDifference(from, to));
		if (roundedDifference < 1 && roundedDifference >= 0) return unit.getMoment();

		return removeUnnecessaryZeros(roundedDifference) + " " + (roundedDifference == 1 ? unit.getSingular() : unit.getPlural());
	}

	public String formatDateRelativeTo(final Date date, final Date relativeTo) {
		final HumanDateUnit unit = getRelativeUnit(date, relativeTo, minimum, maximum);
		final float roundedDifference = round(unit.getDifference(date, relativeTo));
		if (roundedDifference < 1 && roundedDifference >= 0) return unit.getMoment();
		return unit.formatRelativeTo(date, roundedDifference);
	}

	public String formatAbsoluteDate(final Date date) {
		return format("EEE, dd/MM/yyyy '" + MESSAGES.at() + "' hh:mm:ss", date);
	}

	public static String formatShortAbsuluteDate(final Date date) {
		return format("dd/MM/yy", date);
	}

	private static String format(final String pattern, final Date date) {
		return DateTimeFormat.getFormat(pattern).format(date);
	}

	private String removeUnnecessaryZeros(final float value) {
		return Float.toString(value).replaceAll("\\.0+$", "");
	}

	private float round(final float value) {
		return Float.valueOf(ClientDecimalFormat.roundFloat(value, decimalDigits).replace(',', '.'));
	}

}
