package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.client.services.actionExecution.UndoWarningMessages;

public interface ShowsUndoAlertAfterActionExecution {

	String getAlertMessage(UndoWarningMessages messages);

}
