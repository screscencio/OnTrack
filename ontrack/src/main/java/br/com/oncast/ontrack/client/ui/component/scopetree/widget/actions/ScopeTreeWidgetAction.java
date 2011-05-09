package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UnableToCompleteActionException;

public interface ScopeTreeWidgetAction {
	void execute() throws UnableToCompleteActionException;
}
