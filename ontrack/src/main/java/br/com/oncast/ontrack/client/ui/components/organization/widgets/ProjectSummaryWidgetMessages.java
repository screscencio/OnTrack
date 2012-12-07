package br.com.oncast.ontrack.client.ui.components.organization.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ProjectSummaryWidgetMessages extends BaseMessages {

	@DefaultMessage("Scope")
	@Description("Title for release's scope")
	String scope();

	@DefaultMessage("Unplanned Scope")
	@Description("Title for release's unplanned scope")
	String unplannedScope();

}
