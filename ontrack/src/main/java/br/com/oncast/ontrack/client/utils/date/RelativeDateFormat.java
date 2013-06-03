package br.com.oncast.ontrack.client.utils.date;

import java.util.Date;

public class RelativeDateFormat extends HumanDateFormat {

	private final Date date;

	public RelativeDateFormat(final Date date, final Date relativeTo, final int decimalDigits, final HumanDateUnit unit) {
		super(DateUtils.getDifferenceInMilliseconds(date, relativeTo), decimalDigits, unit);
		this.date = date;
	}

	@Override
	public String getDateText() {
		return isMinimun() ? unit.getMoment() : unit.formatRelativeTo(date, difference);
	}

	@Override
	public String getUnitText() {
		return "";
	}

}