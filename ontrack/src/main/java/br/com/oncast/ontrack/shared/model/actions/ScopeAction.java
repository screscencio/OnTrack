package br.com.oncast.ontrack.shared.model.actions;

public interface ScopeAction extends ModelAction {
	boolean changesEffortInference();

	boolean changesProgressInference();
}
