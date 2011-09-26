package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class UserAction {

	private long id;

	private ModelAction action;

	public UserAction() {}

	public long getId() {
		return id;
	}

	public ModelAction getModelAction() {
		return action;
	}
}
