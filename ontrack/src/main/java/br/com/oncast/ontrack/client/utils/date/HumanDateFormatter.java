package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.DateUnit.DAY;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.HOUR;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MINUTE;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MONTH;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.WEEK;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.YEAR;

import java.util.Date;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public enum HumanDateFormatter {
	JUST_NOW(1 * MINUTE, "yyyyMMddHHmm") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return onlyNumbers ? "0" : messages.lessThanAMinute();
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return messages.justNow();
		}
	},
	MINUTES(1 * HOUR, "yyyyMMddHH") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, MINUTE, messages.minute(), messages.minutes());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("h:mm a", date);
		}
	},
	HOURS(1 * DAY, "yyyyMMdd") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, HOUR, messages.hour(), messages.hours());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("h:mm a", date);
		}
	},
	DAYS(1 * WEEK, "yyyyMM") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, DAY, messages.day(), messages.days());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("E, d", date);
		}
	},
	WEEKS(1 * MONTH, "yyyyMM") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, WEEK, messages.week(), messages.weeks());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("E, d", date);
		}
	},
	MONTHS(1 * YEAR, "yyyy") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, MONTH, messages.month(), messages.months());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("MMM, d", date);
		}
	},
	YEARS(Long.MAX_VALUE, "G") {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, YEAR, messages.year(), messages.years());
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("MMM, yyyy", date);
		}
	};

	private static final HumanDateFormatterMessages messages = GWT.create(HumanDateFormatterMessages.class);

	private static final int DEFAULT_DIGITS = 0;
	private static final boolean DEFAULT_ONLY_NUMBERS = false;

	private static int decimalDigits = DEFAULT_DIGITS;
	private static boolean onlyNumbers = DEFAULT_ONLY_NUMBERS;

	private final long maxTimeDifference;
	private DateTimeFormat format;

	private HumanDateFormatter(final long maxTimeDifference, final String pattern) {
		this.maxTimeDifference = maxTimeDifference;
		this.format = DateTimeFormat.getFormat(pattern);
	}

	public static String getDifferenceDate(final Date date) {
		final long difference = new Date().getTime() - date.getTime();
		for (final HumanDateFormatter formatter : values()) {
			if (formatter.maxTimeDifference > difference) { return formatter.formatDifferenceTime(difference) + " " + messages.ago(); }
		}
		return getAbsoluteText(date);
	}

	public static String getRelativeDate(final Date date) {
		final Date currentDate = new Date();
		for (final HumanDateFormatter formatter : values()) {
			if (formatter.accepts(currentDate, date)) { return formatter.formatRelativeTime(date); }
		}
		return getAbsoluteText(date);
	}

	public static String getAbsoluteText(final Date date) {
		return format("EEE, dd/MM/yyyy '" + messages.at() + "' hh:mm:ss", date);
	}

	protected abstract String formatDifferenceTime(final long difference);

	protected abstract String formatRelativeTime(final Date date);

	protected static String format(final String pattern, final Date date) {
		return DateTimeFormat.getFormat(pattern).format(date);
	}

	protected static String mountDifferenceText(final long difference, final long delimiter, final String singular, final String plural) {
		final float time = difference / (float) delimiter;
		String differenceText = ClientDecimalFormat.roundFloat(time, decimalDigits).replaceAll("\\.0+$", "");
		if (!onlyNumbers) differenceText += " " + (time <= 1 ? singular : plural);
		return differenceText;
	}

	private boolean accepts(final Date currentDate, final Date date) {
		return format.format(currentDate).equals(format.format(date));
	}

	public static String getShortAbsuluteDate(final Date date) {
		return format("dd/MM/yy", date);
	}

	public static String getDifferenceText(final long difference) {
		for (final HumanDateFormatter formatter : values()) {
			if (formatter.maxTimeDifference > difference) { return formatter.formatDifferenceTime(difference); }
		}
		return mountDifferenceText(difference, 1, "ms", "ms");
	}

	public static String getDifferenceText(final long difference, final int digits) {
		decimalDigits = digits;
		final String differenceText = getDifferenceText(difference);
		decimalDigits = DEFAULT_DIGITS;
		return differenceText;
	}

	public static String getDifferenceText(final long difference, final int digits, final boolean numbersOnly) {
		decimalDigits = digits;
		onlyNumbers = numbersOnly;
		final String differenceText = getDifferenceText(difference);
		decimalDigits = DEFAULT_DIGITS;
		onlyNumbers = DEFAULT_ONLY_NUMBERS;
		return differenceText;
	}

}