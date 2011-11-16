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
	// FIXME Make this constructor protected.
	public Project() {}

	// FIXME Change constructor so ProjectRepresentation is also always set together with the rest.
	public Project(final Scope projectScope, final Release projectRelease) {
		this.projectScope = projectScope;
		this.projectRelease = projectRelease;
	}

	public Scope getProjectScope() {
		return projectScope;
	}

	// FIXME Remove this method?
	public void setProjectScope(final Scope scope) {
		this.projectScope = scope;
	}

	public Release getProjectRelease() {
		return projectRelease;
	}

	// FIXME Remove this method?
	public void setProjectRelease(final Release projectRelease) {
		this.projectRelease = projectRelease;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	// FIXME Remove this method?
	public void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}
}
