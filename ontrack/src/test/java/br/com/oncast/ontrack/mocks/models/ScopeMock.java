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

	public static Scope getScope2() {
		final Scope projectScope = new Scope("Project");
		final Scope child = new Scope("aaa");
		child.add(new Scope("111"));
		child.add(new Scope("222"));
		child.add(new Scope("333"));
		child.add(new Scope("444"));
		projectScope.add(child);
		projectScope.add(new Scope("bbb"));
		projectScope.add(new Scope("ccc"));
		projectScope.add(new Scope("ddd"));
		projectScope.add(new Scope("eee"));
		projectScope.add(new Scope("fff"));

		return projectScope;
	}

	public static Scope getScopeWithEffort() {
		final Scope root = new Scope("Project");
		final Scope scope = new Scope("0");
		scope.getEffort().setDeclared(5);
		root.add(scope);

		final Scope scope2 = new Scope("1");
		scope2.getEffort().setDeclared(10);
		root.add(scope2);

		final Scope scope3 = new Scope("2");
		scope3.getEffort().setDeclared(15);
		root.add(scope3);

		final Scope scope4 = new Scope("3");
		scope4.getEffort().setDeclared(20);
		root.add(scope4);

		root.add(new Scope("4"));

		return root;
	}

}
