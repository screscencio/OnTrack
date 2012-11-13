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

	@Description("title for low estimated velocity help")
	@DefaultMessage("Estimated velocity is too low")
	String lowEstimatedVelocityWarningTitle();

	@Description("tips for fixing low estimated velocity")
	@DefaultMessage(" - You can better estimate the scopes (higher) by selecting the scope and pressing SHIFT + #;\n - You can tell us the velocity by double clicking in the 'Estimated Velocity';\n - You can update the progress (%) of scopes more often")
	String lowEstimatedVelocityTips();

}
