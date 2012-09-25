package br.com.oncast.ontrack.client.i18n;

import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessages;

import com.google.gwt.core.client.GWT;

public class ActionExecutionErrorMessageTranslator {

	private static ActionExecutionErrorMessages actionExecutionErrorMessages;

	public static String translate(final ActionExecutionErrorMessageCode code, final String... errorArgs) {
		return code.selectMessage(getServerErrorMessages(), errorArgs);
	}

	private static ActionExecutionErrorMessages getServerErrorMessages() {
		if (actionExecutionErrorMessages != null) return actionExecutionErrorMessages;
		return actionExecutionErrorMessages = GWT.create(ActionExecutionErrorMessages.class);
	}

}
