package br.com.oncast.ontrack.utils.model;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

public class ReleaseTestUtils {

	private static int releaseCounter = 0;

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getEmptyRelease() {
		return createRelease("release");
	}

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getRelease() {
		final Release projectRelease = createRelease("project");
		final Release r1 = createRelease("R1");
		final Release r2 = createRelease("R2");
		final Release r3 = createRelease("R3");
		final Release it1 = createRelease("It1");
		final Release it2 = createRelease("It2");
		final Release it3 = createRelease("It3");
		final Release it4 = createRelease("It4");

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
		final Release projectRelease = createRelease("project");
		final Release r1 = createRelease("R1");
		final Release r2 = createRelease("R2");
		final Release r3 = createRelease("R3");

		final Release it1 = createRelease("It1");
		final Release it2 = createRelease("It2");
		final Release it3 = createRelease("It3");
		final Release it4 = createRelease("It4");

		final Release w1 = createRelease("w1");
		final Release w2 = createRelease("w2");
		final Release w3 = createRelease("w3");

		final Release d1 = createRelease("d1");
		final Release d2 = createRelease("d2");
		final Release d3 = createRelease("d3");

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

	public static Release getReleaseWithScopesWithEffort() {
		final Release projectRelease = getRelease();
		final Release r1 = projectRelease.getChild(0);

		final Scope scope = ScopeTestUtils.getScopeWithEffort();
		r1.addScope(scope.getChild(0));
		r1.addScope(scope.getChild(1));
		r1.addScope(scope.getChild(2));

		for (final Release r : r1.getChildren()) {
			final Scope s = ScopeTestUtils.getScopeWithEffort();
			r.addScope(s.getChild(0));
			r.addScope(s.getChild(1));
			r.addScope(s.getChild(2));
		}

		return r1;
	}

	public static Release createRelease(final String description) {
		return new Release(description, new UUID());
	}

	public static Release createRelease() {
		return createRelease(Release.class.getSimpleName() + ++releaseCounter);
	}

	public static Release setInferedStartDay(final Release release, final WorkingDay day) {
		final Scope scope = ScopeTestUtils.createScope();
		ScopeTestUtils.setStartDate(scope, day);
		release.addScope(scope);
		return release;
	}

	public static Release setInferedEndDay(final Release release, final WorkingDay day) {
		final Scope scope = ScopeTestUtils.createScope();
		ScopeTestUtils.setEndDate(scope, day);
		release.addScope(scope);
		return release;
	}

}
