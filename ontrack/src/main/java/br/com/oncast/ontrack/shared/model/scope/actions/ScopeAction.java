package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface ScopeAction extends ModelAction {
	public boolean changesEffortInference();
}
