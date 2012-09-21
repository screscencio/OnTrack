package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ServerPushLoadingMessages extends BaseMessages {

	@Description("message shown when the last applied action conflicts.")
	@DefaultMessage("Could not connect to server")
	String couldNotConnectToServer();

	@Description("message shown while establising connection to server.")
	@DefaultMessage("Establishing connection to server...")
	String establishingConnection();

}
