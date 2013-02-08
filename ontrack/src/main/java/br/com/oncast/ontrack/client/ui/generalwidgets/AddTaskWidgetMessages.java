package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface AddTaskWidgetMessages extends BaseMessages {

	@DefaultMessage("Please insert description first.")
	@Description("add task empty description")
	String emptyDescription();

	@DefaultMessage("Please select a scope first.")
	@Description("add task no scope selected")
	String noScopeSelected();

	@DefaultMessage("Please select a scope to add new tasks")
	@Description("widget title no scope selected")
	String noScopeTitle();

	@DefaultMessage("Add task to ''{0}''")
	@Description("widget title add task to scope")
	String addTaskTitle(final String scopeDescription);

}
