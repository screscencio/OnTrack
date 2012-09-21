package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface UserInformationLoadingMessages extends BaseMessages {

	@Description("message shown when the user information load fails.")
	@DefaultMessage("Could not load user information.")
	String couldNotLoadUserInformation();

	@Description("message shown while loading user data.")
	@DefaultMessage("Loading user data")
	String loadingUserData();

}
