package br.com.oncast.ontrack.client.i18n;

import br.com.oncast.ontrack.shared.exceptions.ServerErrorMessages;
import br.com.oncast.ontrack.shared.exceptions.ServerErrorMessageCode;

import com.google.gwt.core.client.GWT;

public class ServerErrorMessageTranslator {

	private static ServerErrorMessages serverErrorMessages;

	public static String translate(final ServerErrorMessageCode code) {
		return code.selectMessage(getServerErrorMessages());
	}

	private static ServerErrorMessages getServerErrorMessages() {
		if (serverErrorMessages != null) return serverErrorMessages;
		return serverErrorMessages = GWT.create(ServerErrorMessages.class);
	}

}
