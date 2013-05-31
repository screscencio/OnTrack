package br.com.oncast.ontrack.client.utils.date;

import static br.com.oncast.ontrack.client.utils.date.DateUnit.DAY;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.HOUR;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MINUTE;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.MONTH;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.SECOND;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.WEEK;
import static br.com.oncast.ontrack.client.utils.date.DateUnit.YEAR;
import static br.com.oncast.ontrack.client.utils.date.HumanDateFormatter2.MESSAGES;
import static com.google.gwt.i18n.client.DateTimeFormat.getFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.gwt.i18n.client.DateTimeFormat;

public enum HumanDateUnit {
	SECONDS(MINUTE, SECOND, "yyyyMMddHHmm", MESSAGES.second(), MESSAGES.seconds(), MESSAGES.now()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return MESSAGES.now();
		}
	},
	MINUTES(HOUR, MINUTE, "yyyyMMddHH", MESSAGES.minute(), MESSAGES.minutes(), MESSAGES.thisMinute()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("HH:mm").format(date);
		}
	},
	HOURS(DAY, HOUR, "yyyyMMdd", MESSAGES.hour(), MESSAGES.hours(), MESSAGES.thisHour()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("HH:mm").format(date);
		}
	},
	HOURS_IN_DIFFERENT_DAY(DAY, HOUR, "yyyyMM", MESSAGES.hour(), MESSAGES.hours(), MESSAGES.thisHour()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			final String day = roundedDifference <= 0 ? MESSAGES.tomorrow() : MESSAGES.yesterday();
			return Joiner.on(" ").join(day, MESSAGES.at(), getFormat("HH:mm").format(date));
		}
	},
	ONE_DAY(2 * DAY, DAY, "yyyyMM", MESSAGES.day(), MESSAGES.days(), MESSAGES.today()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return roundedDifference <= 0 ? MESSAGES.tomorrow() : MESSAGES.yesterday();
		}
	},
	FEW_DAYS(WEEK, DAY, "yyyyMM", MESSAGES.day(), MESSAGES.days(), MESSAGES.fewDays()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("EE d").format(date);
		}
	},
	WEEKS(MONTH, WEEK, "yyyyMM", MESSAGES.week(), MESSAGES.weeks(), MESSAGES.thisWeek()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("MMM d").format(date);
		}
	},
	MONTHS(YEAR, MONTH, "yyyy", MESSAGES.month(), MESSAGES.months(), MESSAGES.thisMonth()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("MMM d").format(date);
		}
	},
	YEARS(Long.MAX_VALUE, YEAR, "G", MESSAGES.year(), MESSAGES.years(), MESSAGES.thisYear()) {
		@Override
		public String formatRelativeTo(final Date date, final float roundedDifference) {
			return DateTimeFormat.getFormat("MM yyyy").format(date);
		}
	};

	private static final List<HumanDateUnit> VALUES = Arrays.asList(values());
	private static final int SIZE = VALUES.size();

	private final long maxDifference;
	private final float delimiter;
	private final String singular;
	private final String plural;
	private final String moment;
	private DateTimeFormat comparationFormat;

	private HumanDateUnit(final long maxDifference, final long delimiter, final String comparationFormatString, final String singular, final String plural,
			final String moment) {
		this.maxDifference = maxDifference;
		this.delimiter = delimiter;
		this.singular = singular;
		this.plural = plural;
		this.moment = moment;
		this.comparationFormat = DateTimeFormat.getFormat(comparationFormatString);
	}

	public String getPlural() {
		return plural;
	}

	public String getSingular() {
		return singular;
	}

	public String getMoment() {
		return moment;
	}

	public static HumanDateUnit getSmallest() {
		return VALUES.get(0);
	}

	public static HumanDateUnit getBiggest() {
		return VALUES.get(SIZE - 1);
	}

	public static HumanDateUnit getDifferenceUnit(final Date from, final Date to, final HumanDateUnit minimum, final HumanDateUnit maximum) {
		final long difference = absDifference(from, to);
		for (final HumanDateUnit unit : valuesRange(minimum, maximum)) {
			if (unit.accepts(difference)) return unit;
		}
		return maximum;
	}

	public static HumanDateUnit getRelativeUnit(final Date date, final Date relativeTo, final HumanDateUnit minimum, final HumanDateUnit maximum) {
		final long difference = absDifference(date, relativeTo);
		for (final HumanDateUnit unit : valuesRange(minimum, maximum)) {
			if (unit.accepts(date, relativeTo) && unit.accepts(difference)) return unit;
		}
		return maximum;
	}

	public abstract String formatRelativeTo(Date date, float roundedDifference);

	public float getDifference(final Date from, final Date to) {
		return rawDifference(from, to) / delimiter;
	}

	private static long absDifference(final Date from, final Date to) {
		return Math.abs(rawDifference(from, to));
	}

	private static long rawDifference(final Date from, final Date to) {
		return to.getTime() - from.getTime();
	}

	private static List<HumanDateUnit> valuesRange(final HumanDateUnit minimum, final HumanDateUnit maximum) {
		return VALUES.subList(minimum.ordinal(), maximum.ordinal() + 1);
	}

	private boolean accepts(final long difference) {
		return difference < this.maxDifference;
	}

	private boolean accepts(final Date date, final Date relativeTo) {
		return comparationFormat.format(date).equals(comparationFormat.format(relativeTo));
	}

}