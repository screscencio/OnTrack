package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface RemoveMembersWidgetMessages extends BaseMessages {

	@DefaultMessage("The user was removed successfully")
	@Description("message shown when the user was removed sucessfully")
	String userRemoved();

	@DefaultMessage("The user could not be removed")
	@Description("message shown when the user remotion fails")
	String userRemoveFailed();

	@DefaultMessage("You can not remove Admin")
	@Description("message shown when tries to remove Admin")
	String cantRemoveAdmin();

}
