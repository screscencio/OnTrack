package br.com.oncast.ontrack.utils.mocks.models;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseTestUtils {

	private static int releaseCounter = 0;

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getEmptyRelease() {
		return ReleaseFactoryTestUtil.create("release");
	}

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getRelease() {
		final Release projectRelease = ReleaseFactoryTestUtil.create("project");
		final Release r1 = ReleaseFactoryTestUtil.create("R1");
		final Release r2 = ReleaseFactoryTestUtil.create("R2");
		final Release r3 = ReleaseFactoryTestUtil.create("R3");
		final Release it1 = ReleaseFactoryTestUtil.create("It1");
		final Release it2 = ReleaseFactoryTestUtil.create("It2");
		final Release it3 = ReleaseFactoryTestUtil.create("It3");
		final Release it4 = ReleaseFactoryTestUtil.create("It4");

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
		final Release projectRelease = ReleaseFactoryTestUtil.create("project");
		final Release r1 = ReleaseFactoryTestUtil.create("R1");
		final Release r2 = ReleaseFactoryTestUtil.create("R2");
		final Release r3 = ReleaseFactoryTestUtil.create("R3");

		final Release it1 = ReleaseFactoryTestUtil.create("It1");
		final Release it2 = ReleaseFactoryTestUtil.create("It2");
		final Release it3 = ReleaseFactoryTestUtil.create("It3");
		final Release it4 = ReleaseFactoryTestUtil.create("It4");

		final Release w1 = ReleaseFactoryTestUtil.create("w1");
		final Release w2 = ReleaseFactoryTestUtil.create("w2");
		final Release w3 = ReleaseFactoryTestUtil.create("w3");

		final Release d1 = ReleaseFactoryTestUtil.create("d1");
		final Release d2 = ReleaseFactoryTestUtil.create("d2");
		final Release d3 = ReleaseFactoryTestUtil.create("d3");

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

	public static Release createRelease() {
		return ReleaseFactoryTestUtil.create(Release.class.getSimpleName() + ++releaseCounter);
	}

}
