package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseMockFactory;

public class ReleaseMock {

	// IMPORTANT Doesn't change this scope without checking the tests that use it.
	public static Release getRelease() {
		final Release projectRelease = ReleaseMockFactory.create("project");
		final Release r1 = ReleaseMockFactory.create("R1");
		final Release r2 = ReleaseMockFactory.create("R2");
		final Release r3 = ReleaseMockFactory.create("R3");
		final Release it1 = ReleaseMockFactory.create("It1");
		final Release it2 = ReleaseMockFactory.create("It2");
		final Release it3 = ReleaseMockFactory.create("It3");
		final Release it4 = ReleaseMockFactory.create("It4");

		projectRelease.addChild(r1);
		projectRelease.addChild(r2);
		projectRelease.addChild(r3);
		r1.addChild(it1);
		r1.addChild(it2);
		r1.addChild(it3);
		r2.addChild(it4);

		return projectRelease;
	}

}
