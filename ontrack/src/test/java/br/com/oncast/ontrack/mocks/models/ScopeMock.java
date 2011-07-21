package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeMock {

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScope() {
		final Scope root = new Scope("Project");
		root.add(new Scope("1").add(new Scope("1.1").add(new Scope("1.1.1")).add(new Scope("1.1.2"))).add(new Scope("1.2")));
		root.add(new Scope("2"));
		root.add(new Scope("3"));

		return root;
	}
}
