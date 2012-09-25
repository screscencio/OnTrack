package br.com.oncast.ontrack.client.utils.date;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface HumanDateFormatterMessages extends BaseMessages {

	@Description("ago")
	@DefaultMessage("ago")
	String ago();

	@Description("just now")
	@DefaultMessage("Just now")
	String justNow();

	@Description("minute")
	@DefaultMessage("minute")
	String minute();

	@Description("minutes")
	@DefaultMessage("minutes")
	String minutes();

	@Description("hour")
	@DefaultMessage("hour")
	String hour();

	@Description("hours")
	@DefaultMessage("hours")
	String hours();

	@Description("day")
	@DefaultMessage("day")
	String day();

	@Description("days")
	@DefaultMessage("days")
	String days();

	@Description("week")
	@DefaultMessage("week")
	String week();

	@Description("weeks")
	@DefaultMessage("weeks")
	String weeks();

	@Description("month")
	@DefaultMessage("month")
	String month();

	@Description("months")
	@DefaultMessage("months")
	String months();

	@Description("year")
	@DefaultMessage("year")
	String year();

	@Description("years")
	@DefaultMessage("years")
	String years();

	@Description("at")
	@DefaultMessage("at")
	String at();

	@Description("less than a minute ago")
	@DefaultMessage("less than a minute ago")
	String lessThanAMinuteAgo();

}
