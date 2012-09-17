package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface ProjectSelectionWidgetMessages extends Messages {

	@Description("help text of select input")
	@DefaultMessage("Hit ↓ to show your projects")
	String selectionHelpText();

	@Description("option shown for user while inputing project name")
	@DefaultMessage("Create project ''{0}'' ({1} creations available)")
	String createNewProject(String projectName, int projectQuota);

	@Description("message shown for user when project quota is exceeded")
	@DefaultMessage("Ask for more projects")
	String askForMoreProjects();

	@Description("message shown when there are no projects to list for the user")
	@DefaultMessage("No Projects. Type to create one.")
	String noProjects();

	@Description("message shown on async call to request bigger project quota")
	@DefaultMessage("Processing you request...")
	String processingProjectQuotaRequest();

	@Description("message shown when project quota request is successfuly sent")
	@DefaultMessage("Project quota request was sent!")
	String projectQuotaRequestSent();

}
