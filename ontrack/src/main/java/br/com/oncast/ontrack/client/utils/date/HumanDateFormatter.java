package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.DateUnit.DAY;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.HOUR;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MINUTE;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MONTH;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.WEEK;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.YEAR;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public enum HumanDateFormatter {
	JUST_NOW(1 * MINUTE) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return "Just now";
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("h:mm a", date);
		}
	},
	MINUTES(1 * HOUR) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, MINUTE, "minute", "minutes");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("h:mm a", date);
		}
	},
	HOURS(1 * DAY) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, HOUR, "hour", "hours");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("h:mm a", date);
		}
	},
	DAYS(1 * WEEK) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, DAY, "day", "days");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("E, d", date);
		}
	},
	WEEKS(1 * MONTH) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, WEEK, "week", "weeks");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("E, d", date);
		}
	},
	MONTHS(1 * YEAR) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, MONTH, "month", "months");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("MMM, d", date);
		}
	},
	YEARS(Long.MAX_VALUE) {
		@Override
		protected String formatDifferenceTime(final long difference) {
			return mountDifferenceText(difference, YEAR, "year", "years");
		}

		@Override
		protected String formatRelativeTime(final Date date) {
			return format("dd/MM/yy", date);
		}
	};

	private final long maxTimeDifference;

	private HumanDateFormatter(final long maxTimeDifference) {
		this.maxTimeDifference = maxTimeDifference;
	}

	public static String getDifferenceDate(final Date date) {
		final long difference = getDifference(date);
		for (final HumanDateFormatter formatter : values()) {
			if (formatter.maxTimeDifference > difference) { return formatter.formatDifferenceTime(difference); }
		}
		return getAbsoluteText(date);
	}

	public static String getRelativeDate(final Date date) {
		final long difference = getDifference(date);
		for (final HumanDateFormatter formatter : values()) {
			if (formatter.maxTimeDifference > difference) { return formatter.formatRelativeTime(date); }
		}
		return getAbsoluteText(date);
	}

	public static String getAbsoluteText(final Date date) {
		return format("EEE, dd/MM/yyyy 'at' hh:mm:ss", date);
	}

	protected abstract String formatDifferenceTime(final long difference);

	protected abstract String formatRelativeTime(final Date date);

	protected static String format(final String pattern, final Date date) {
		return DateTimeFormat.getFormat(pattern).format(date);
	}

	protected String mountDifferenceText(final long difference, final long delimiter, final String singular, final String plural) {
		final int time = (int) (difference / delimiter);
		return time + " " + (time <= 1 ? singular : plural) + " ago";
	}

	private static long getDifference(final Date date) {
		final Date currentDate = new Date();
		final long difference = currentDate.getTime() - date.getTime();
		return difference;
	}

	public static String getShortAbsuluteDate(final Date date) {
		return format("dd/MM/yy", date);
	}

}