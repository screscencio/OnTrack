package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ReleaseWidgetMessages extends BaseMessages {

	@Description("increase release priority menu text")
	@DefaultMessage("Increase priority")
	String increasePriority();

	@Description("decrease release priority menu text")
	@DefaultMessage("Decrease priority")
	String decreasePriority();

	@Description("delete release menu text")
	@DefaultMessage("Delete release")
	String deleteRelease();

	@Description("go to release report menu text")
	@DefaultMessage("Report")
	String report();

	@Description("go to release timesheet menu text")
	@DefaultMessage("Timesheet")
	String timesheet();

	@Description("go to details menu text")
	@DefaultMessage("Details")
	String details();

	@Description("open burn-up details menu text")
	@DefaultMessage("Burn-up chart")
	String burnUp();

	@Description("go to planning icon tooltip")
	@DefaultMessage("Go to Planning")
	String goToPlanning();

	@Description("go to kanban icon tooltip")
	@DefaultMessage("Go to Kanban")
	String goToKanban();

	@Description("open impediments list popup menu text")
	@DefaultMessage("Impediments")
	String impediments();

}
