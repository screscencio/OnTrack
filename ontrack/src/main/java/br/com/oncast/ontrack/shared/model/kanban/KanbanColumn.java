package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class KanbanColumn {

	private final String title;
	private final List<Scope> scopes;

	public KanbanColumn(final String title) {
		this.title = title;
		scopes = new ArrayList<Scope>();
	}

	public String getTitle() {
		return title;
	}

	public List<Scope> getScopes() {
		return new ArrayList<Scope>(scopes);
	}

	public void addScope(final Scope scope) {
		scopes.add(scope);
	}

}
