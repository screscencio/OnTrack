package br.com.oncast.ontrack.client.i18n;

import br.com.oncast.ontrack.shared.exceptions.ExceptionMessageCodes;
import br.com.oncast.ontrack.shared.exceptions.ExceptionMessages;

import com.google.gwt.core.client.GWT;

public class ExceptionMessageTranslator {

	private static ExceptionMessages exceptionMessages;

	public static String translate(final ExceptionMessageCodes code) {
		return code.getTranslatedMessage(getMessages());
	}

	private static ExceptionMessages getMessages() {
		if (exceptionMessages != null) return exceptionMessages;
		return exceptionMessages = GWT.create(ExceptionMessages.class);
	}

}
