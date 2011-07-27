package br.com.oncast.ontrack.server.model.scope;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopePrinter {
	private ScopePrinter() {}

	public static void print(final Scope scope) {
		System.out.println("Printing scope tree...");
		print(0, scope);
	}

	private static void print(final int level, final Scope scope) {
		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < level - 1; i++)
			b.append("|");
		if (level != 0) b.append("+ ");
		b.append(scope.getDescription());
		b.append(" [d:" + questionForNull(scope.getEffort().getDeclared()) + ", i:" + questionForNull(scope.getEffort().getInfered()) + "]");

		System.out.println(b.toString());

		for (final Scope child : scope.getChildren()) {
			print(level + 1, child);
		}
	}

	private static Object questionForNull(final Object effort) {
		return effort == null ? "?" : effort;
	}
}
