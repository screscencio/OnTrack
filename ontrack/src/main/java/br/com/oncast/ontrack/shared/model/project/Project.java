package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectRepresentation projectRepresentation;
	private Scope projectScope;
	private Release projectRelease;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Project() {}

	public Project(final ProjectRepresentation projectRepresentation, final Scope projectScope, final Release projectRelease) {
		this.projectRepresentation = projectRepresentation;
		this.projectScope = projectScope;
		this.projectRelease = projectRelease;
	}

	public Scope getProjectScope() {
		return projectScope;
	}

	public Release getProjectRelease() {
		return projectRelease;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	public boolean hasKanbanFor(final Release release) {
		// FIXME LOBO implement;
		return false;
	}

	public Kanban getKanban(final Release release) {
		// FIXME LOBO implement
		return null;
	}
}
