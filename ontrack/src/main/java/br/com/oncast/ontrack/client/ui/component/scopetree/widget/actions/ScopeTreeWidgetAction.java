package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;

public interface ScopeTreeWidgetAction {
	void execute() throws UnableToCompleteActionException;

	void rollback() throws UnableToCompleteActionException;
}
