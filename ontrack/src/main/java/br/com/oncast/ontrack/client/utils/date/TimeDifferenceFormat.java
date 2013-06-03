package br.com.oncast.ontrack.client.utils.date;

public class TimeDifferenceFormat extends HumanDateFormat {

	public TimeDifferenceFormat(final long differenceInMilliseconds, final int decimalDigits, final HumanDateUnit unit) {
		super(differenceInMilliseconds, decimalDigits, unit);
	}

	@Override
	protected String formatDate() {
		return removeUnnecessaryZeros(difference);
	}

	private String removeUnnecessaryZeros(final float value) {
		return Float.toString(value).replaceAll("\\.0+$", "");
	}

}
