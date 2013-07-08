package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ActionSyncStateMenuItemMessages extends BaseMessages {

	@DefaultMessage("Connected")
	@Description("Tooltip indicating that the application is connected")
	String connected();

	@DefaultMessage("No connection")
	@Description("Tooltip indicating that the application is not connected")
	String noConnection();

	@DefaultMessage("Syncing")
	@Description("Tooltip indicating that the application is syncing with server")
	String syncing();

	@DefaultMessage("modification still need to be saved")
	@Description("Tooltip indicating that there is only one pending action")
	String singleModificationNeedToBeSent();

	@DefaultMessage("modifications still needs to be saved")
	@Description("Tooltip indicating that there is more than one pending actions")
	String multipleModificationsNeedsToBeSent();

	@DefaultMessage("all your modifications were saved")
	@Description("Tooltip indicating that there is no pending actions")
	String upToDate();

}
