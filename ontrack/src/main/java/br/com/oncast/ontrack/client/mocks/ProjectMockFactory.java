package br.com.oncast.ontrack.client.mocks;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

public class ProjectMockFactory {

	public static Project createProjectMock() {
		return new Project(getScope(), getProjectRelease());
	}

	private static Release getProjectRelease() {
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

	private static Scope getScope() {
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
}
