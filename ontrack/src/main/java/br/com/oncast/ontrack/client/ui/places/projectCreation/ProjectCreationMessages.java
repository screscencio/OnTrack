package br.com.oncast.ontrack.client.ui.places.projectCreation;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ProjectCreationMessages extends BaseMessages {

	@Description("message shown when the project creation fails.")
	@DefaultMessage("Project creation failed. An error occurred.")
	String projectCreationFailed();

	@Description("message shown when the project creation fails unexpectedly.")
	@DefaultMessage("It was not possible to create the project.\n Verify your connection status.")
	String itWasNotPossibleToCreateTheProject();

	@Description("displayed when creating a project.")
	@DefaultMessage("Creating project ''{0}''")
	String creatingProject(String projectName);
}
