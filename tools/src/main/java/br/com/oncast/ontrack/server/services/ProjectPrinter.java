package br.com.oncast.ontrack.server.services;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProjectPrinter {
	private final Project project;
	private int level = 0;

	private ProjectPrinter(Project project) {
		this.project = project;
	}

	public static void print(Project project) {
		new ProjectPrinter(project).print();
	}

	private void print() {
		out("Scope:");
		level++;
		printScope(project.getProjectScope());
		level--;
	}

	private void printScope(Scope scope) {
		StringBuilder b = new StringBuilder(scope.getDescription());
		if (scope.getRelease()!=null) {
			b.append(" @").append(scope.getRelease().getDescription());
		}
		out(b.toString());

		level++;
		for (Scope s : scope.getChildren()) {
			printScope(s);
		}
		level--;
	}

	private void out(Object o) {
		for (int i = 0; i < level; i++) {
			if (i!=0) System.out.print("|");
			System.out.print("    ");
		}
		System.out.println(o.toString());
	}
}
