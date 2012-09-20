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
	@DefaultMessage("Delete Release")
	String deleteRelease();

}
