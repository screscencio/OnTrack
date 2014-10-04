package br.com.oncast.ontrack.client.utils.date;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.google.common.base.Joiner;

public class TimeDifferenceFormat extends HumanDateFormat {

	private final long differenceInMilliseconds;

	public TimeDifferenceFormat(final long differenceInMilliseconds, final int decimalDigits, final HumanDateUnit unit) {
		super(differenceInMilliseconds, decimalDigits, unit);
		this.differenceInMilliseconds = differenceInMilliseconds;
	}

	@Override
	public String getDateText() {
		return isMinimun() ? unit.getLessThanMinimun() : getRawDateText();
	}

	public String getRawDateText() {
		return removeUnnecessaryZeros(difference);
	}

	public String formatWith(final String positiveSuffix, final String negativeSuffix) {
		return Joiner.on(' ').join(toString(), differenceInMilliseconds >= 0 ? positiveSuffix : negativeSuffix).trim();
	}

	private String removeUnnecessaryZeros(final float value) {
		return ClientDecimalFormat.removeUnnecessaryRightZeros(Float.toString(value));
	}

}
