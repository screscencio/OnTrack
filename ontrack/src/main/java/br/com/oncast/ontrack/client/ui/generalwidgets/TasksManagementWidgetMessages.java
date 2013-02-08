package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface TasksManagementWidgetMessages extends BaseMessages {

	@DefaultMessage("Please insert the task description first.")
	@Description("error: add task with empty description")
	String emptyTaskDescription();

	@DefaultMessage("Please select a story.")
	@Description("No story selected for task management")
	String noStorySelected();

	@DefaultMessage("Task of ''{0}''")
	@Description("Tasks widget title")
	String tasksOf(final String storyDescription);

}
