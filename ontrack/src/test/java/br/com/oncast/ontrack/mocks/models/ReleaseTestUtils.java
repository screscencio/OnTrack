package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.TestReleaseFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseTestUtils {

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getEmptyRelease() {
		return TestReleaseFactory.create("release");
	}

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getRelease() {
		final Release projectRelease = TestReleaseFactory.create("project");
		final Release r1 = TestReleaseFactory.create("R1");
		final Release r2 = TestReleaseFactory.create("R2");
		final Release r3 = TestReleaseFactory.create("R3");
		final Release it1 = TestReleaseFactory.create("It1");
		final Release it2 = TestReleaseFactory.create("It2");
		final Release it3 = TestReleaseFactory.create("It3");
		final Release it4 = TestReleaseFactory.create("It4");

		projectRelease.addChild(r1);
		projectRelease.addChild(r2);
		projectRelease.addChild(r3);
		r1.addChild(it1);
		r1.addChild(it2);
		r1.addChild(it3);
		r2.addChild(it4);

		return projectRelease;
	}

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getBigRelease() {
		final Release projectRelease = TestReleaseFactory.create("project");
		final Release r1 = TestReleaseFactory.create("R1");
		final Release r2 = TestReleaseFactory.create("R2");
		final Release r3 = TestReleaseFactory.create("R3");

		final Release it1 = TestReleaseFactory.create("It1");
		final Release it2 = TestReleaseFactory.create("It2");
		final Release it3 = TestReleaseFactory.create("It3");
		final Release it4 = TestReleaseFactory.create("It4");

		final Release w1 = TestReleaseFactory.create("w1");
		final Release w2 = TestReleaseFactory.create("w2");
		final Release w3 = TestReleaseFactory.create("w3");

		final Release d1 = TestReleaseFactory.create("d1");
		final Release d2 = TestReleaseFactory.create("d2");
		final Release d3 = TestReleaseFactory.create("d3");

		projectRelease.addChild(r1);
		projectRelease.addChild(r2);
		projectRelease.addChild(r3);

		r1.addChild(it1);
		r1.addChild(it2);
		r1.addChild(it3);
		r2.addChild(it4);

		it2.addChild(w1);
		it2.addChild(w2);
		it2.addChild(w3);

		w2.addChild(d1);
		w2.addChild(d2);
		w2.addChild(d3);

		return projectRelease;
	}

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getReleaseWithScopes() {
		final Release projectRelease = getRelease();
		final Release r1 = projectRelease.getChild(0);

		final Scope scope = ScopeTestUtils.getSimpleScope();
		r1.addScope(scope.getChild(0));
		r1.addScope(scope.getChild(1));
		r1.addScope(scope.getChild(2));

		for (final Release r : r1.getChildren()) {
			final Scope s = ScopeTestUtils.getSimpleScope();
			r.addScope(s.getChild(0));
			r.addScope(s.getChild(1));
			r.addScope(s.getChild(2));
		}

		return r1;
	}

}
