package br.com.oncast.ontrack.shared.project;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

public class Project {

	private Scope scope;
	private Release projectRelease;

	public Project() {}

	public Project(final Scope scope, final Release projectRelease) {
		this.scope = scope;
		this.projectRelease = projectRelease;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(final Scope scope) {
		this.scope = scope;
	}

	public Release getProjectRelease() {
		return projectRelease;
	}

	public void setProjectRelease(final Release projectRelease) {
		this.projectRelease = projectRelease;
	}
}
