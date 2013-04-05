package br.com.oncast.ontrack.shared.model.prioritizationCriteria;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ValueInferenceEngine extends PrioritizationCriteriaInferenceEngine {

	@Override
	public boolean shouldProcess(final ScopeAction action) {
		return action.changesValueInference();
	}

	@Override
	protected PrioritizationCriteria getCriteria(final Scope scope) {
		return scope.getValue();
	}

}
