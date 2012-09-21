package br.com.oncast.ontrack.shared.exceptions;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ExceptionMessages extends BaseMessages {

	@Description("Some action problem happened")
	@DefaultMessage("The project is out of sync. Some changes may have been reverted.")
	String handleIncommingAction();

}
