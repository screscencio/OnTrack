package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface RemoveTaskWidgetMessages extends BaseMessages {

	@DefaultMessage("Please select a task first.")
	@Description("remove task no scope selected")
	String noTaskSelected();

	@DefaultMessage("Please select a task to remove")
	@Description("widget title no scope selected")
	String noTaskTitle();

	@DefaultMessage("Remove task ''{0}''")
	@Description("widget title remove task label")
	String removeTaskTitle(final String scopeDescription);

}
