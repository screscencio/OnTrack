package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ReleaseChartMessages extends BaseMessages {

	@Description("declared ideal line title")
	@DefaultMessage("Declared Ideal Line")
	String declaredIdealLine();

	@Description("effort line title")
	@DefaultMessage("Effort")
	String effort();

	@Description("value line title")
	@DefaultMessage("Value")
	String value();

	@Description("infered ideal line title")
	@DefaultMessage("Infered Ideal Line")
	String inferedIdealLine();

	@Description("value points hint")
	@DefaultMessage("value points")
	String valuePoints();

	@Description("effort points hint")
	@DefaultMessage("effort points")
	String effortPoints();

	@Description("velocity label")
	@DefaultMessage("Velocity: ")
	String velocity();

	@Description("day used in points per day")
	@DefaultMessage("day")
	String day();

}
