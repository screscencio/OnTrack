package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngine {

	public static void process(final Scope scope) {

		// if (scope.isRoot()) return;
		// final Scope parent = scope.getParent();
		processBottomUp(scope);
		processTopDown(scope);
	}

	private static void processTopDown(final Scope scope) {

	}

	private static void processBottomUp(final Scope scope) {
		final float inferedInitial = scope.getEffort().getInfered();
		scope.getEffort().setCalculed(calculateEffort(scope));
		final float inferedFinal = scope.getEffort().getInfered();

		if (inferedFinal == inferedInitial) return;

		if (scope.isRoot()) return;
		processBottomUp(scope.getParent());
	}

	private static int calculateEffort(final Scope scope) {
		int sum = 0;
		for (final Scope child : scope.getChildren())
			sum += child.getEffort().getInfered();

		return sum;
	}

}
