package br.com.oncast.ontrack.shared.model.action;

public interface ScopeAction extends ModelAction {
	boolean changesEffortInference();

	boolean changesProgressInference();

	boolean changesValueInference();
}
