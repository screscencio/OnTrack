package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// TODO ++++Think about merging this class with the "project context" class. ProjectSnapshot use of it and the RPC mechanisms are the main things that would be
// impacted.
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
}
