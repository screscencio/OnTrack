package br.com.oncast.ontrack.client.utils.date;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.google.common.base.Joiner;

public abstract class HumanDateFormat {

	protected final float difference;
	protected final HumanDateUnit unit;

	public HumanDateFormat(final long differenceInMilliseconds, final int decimalDigits, final HumanDateUnit unit) {
		this.difference = round(unit.normalize(differenceInMilliseconds), decimalDigits);
		this.unit = unit;
	}

	public HumanDateFormat(final float roundedDifference, final HumanDateUnit unit) {
		this.difference = roundedDifference;
		this.unit = unit;
	}

	public float getDifference() {
		return difference;
	}

	public String getDateText() {
		if (isMinimun()) return unit.getMoment();
		return formatDate();
	}

	protected abstract String formatDate();

	@Override
	public String toString() {
		return Joiner.on(' ').join(getDateText(), getUnitText()).trim();
	}

	public String getUnitText() {
		if (isMinimun()) return "";
		return difference == 1 ? unit.getSingular() : unit.getPlural();
	}

	private boolean isMinimun() {
		return 0 <= difference && difference < 1;
	}

	private float round(final float value, final int decimalDigits) {
		return Float.valueOf(ClientDecimalFormat.roundFloat(value, decimalDigits).replace(',', '.'));
	}

}