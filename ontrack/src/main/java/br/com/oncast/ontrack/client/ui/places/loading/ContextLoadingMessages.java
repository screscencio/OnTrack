package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ContextLoadingMessages extends BaseMessages {

	@Description("shown while loads the project context")
	@DefaultMessage("Syncing...")
	String syncing();

	@Description("shown when the requested project was not found in the server")
	@DefaultMessage("The requested project was not found.")
	String projectNotFound();

}
