package br.com.oncast.ontrack.shared.project;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Project implements IsSerializable {

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
