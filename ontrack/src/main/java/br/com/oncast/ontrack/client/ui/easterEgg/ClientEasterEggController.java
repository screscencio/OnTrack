package br.com.oncast.ontrack.client.ui.easterEgg;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.svnRevisionProvider.AppVersionProvider;

import com.google.gwt.user.client.Window;

public class ClientEasterEggController {

	private static final String EASTER_EGG_MSG = "Congratulate OnTrack's dev team".toUpperCase();

	public static void verifyEasterEgg(final Scope scope) {
		if (!scope.getDescription().toUpperCase().equals(EASTER_EGG_MSG)) return;
		showEasterEggMessage();
	}

	private static void showEasterEggMessage() {
		Window.alert("Instance version: " + AppVersionProvider.INSTANCE.version());
	}
}
