package br.com.oncast.ontrack.server.services;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProjectPrinter {
	private final Project project;
	private int level = 0;

	private ProjectPrinter(final Project project) {
		this.project = project;
	}

	public static void print(final Project project) {
		new ProjectPrinter(project).print();
	}

	private void print() {
		out("Scope:");
		level++;
		printScope(project.getProjectScope(), true);
		level--;
		out("Release:");
		level++;
		printRelease(project.getProjectRelease());
		level--;
	}

	private void printRelease(final Release release) {
		out("-> "+release.getDescription());

		level++;
		for (final Release child : release.getChildren()) {
			printRelease(child);
		}

		for (final Scope boundScope : release.getScopeList()) {
			printScope(boundScope, false);
		}
		level--;
	}

	private void printScope(final Scope scope, final boolean propagate) {
		final StringBuilder b = new StringBuilder(scope.getDescription());
		if (scope.getRelease()!=null) {
			b.append(" @").append(scope.getRelease().getDescription());
		}
		if (scope.getEffort().hasDeclared()) {
			b.append(" #").append(scope.getEffort().getDeclared());
		}
		if (scope.getProgress().hasDeclared()) {
			b.append(" %").append(scope.getProgress().getDescription());
		}

		out(b.toString());

		level++;
		if (propagate) for (final Scope s : scope.getChildren()) {
			printScope(s, propagate);
		}
		level--;
	}

	private void out(final Object o) {
		for (int i = 0; i < level; i++) {
			if (i!=0) System.out.print("|");
			System.out.print("    ");
		}
		System.out.println(o.toString());
	}
}
