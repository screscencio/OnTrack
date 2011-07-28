package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleaseMock {

	public static Release getRelease() {
		final Release projectRelease = new Release("project");
		final Release r1 = new Release("R1");
		final Release r2 = new Release("R2");
		final Release r3 = new Release("R3");
		final Release it1 = new Release("It1");
		final Release it2 = new Release("It2");
		final Release it3 = new Release("It3");
		final Release it4 = new Release("It4");

		projectRelease.addRelease(r1);
		projectRelease.addRelease(r2);
		projectRelease.addRelease(r3);
		r1.addRelease(it1);
		r1.addRelease(it2);
		r1.addRelease(it3);
		r2.addRelease(it4);

		return projectRelease;
	}

}
