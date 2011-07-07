package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeMock {

	public static Scope getScope() {
		final Scope root = new Scope("Root");

		final Scope s1 = new Scope("1");
		final Scope s1_1 = new Scope("1.1");
		final Scope s1_2 = new Scope("1.2");

		root.add(s1);
		s1.add(s1_1);
		s1.add(s1_2);
		s1_2.add(new Scope("1.1.1"));
		s1_2.add(new Scope("1.1.2"));
		s1_2.add(new Scope("1.1.3"));

		final Scope s2 = new Scope("2");
		root.add(s2);

		final Scope s3 = new Scope("3");
		root.add(s3);
		s3.add(new Scope("3.1"));
		s3.add(new Scope("3.2"));

		return root;
	}
}
