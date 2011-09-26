package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	private Scope projectScope;
	private Release projectRelease;

	public Project() {}

	public Project(final Scope projectScope, final Release projectRelease) {
		this.projectScope = projectScope;
		this.projectRelease = projectRelease;
	}

	public Scope getProjectScope() {
		return projectScope;
	}

	public void setProjectScope(final Scope scope) {
		this.projectScope = scope;
	}

	public Release getProjectRelease() {
		return projectRelease;
	}

	public void setProjectRelease(final Release projectRelease) {
		this.projectRelease = projectRelease;
	}
}
