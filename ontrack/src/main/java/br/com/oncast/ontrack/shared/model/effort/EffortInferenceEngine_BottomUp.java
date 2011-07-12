package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngine_BottomUp {

	private static int opCount = 0;

	public static void process(final Scope scope) {
		preProcessBottomUp(scope, 2);
		final Scope topMostProcessedScope = processBottomUp(scope);
		// processTopDown(topMostProcessedScope);
	}

	private static void preProcessBottomUp(final Scope scope, int i) {
		i -= 1;
		int sum = 0;
		for (final Scope child : scope.getChildren()) {
			if (i > 0) preProcessBottomUp(child, i);
			sum += child.getEffort().getInfered();
		}
		scope.getEffort().setBottomUpValue(sum);
		log("preProcessBottomUp", scope);
	}

	private static void log(final String descrip, final Scope scope) {
		System.out.println("Operation " + opCount++ + ": \t" + descrip + "\t - \t" + scope.getDescription());
	}

	private static Scope processBottomUp(final Scope scope) {
		log("processBottomUp", scope);
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();

		final float inferedInitial = parent.getEffort().getInfered();
		preProcessBottomUp(parent, 0);
		final float inferedFinal = parent.getEffort().getInfered();

		if (inferedFinal == inferedInitial) return parent;
		return processBottomUp(parent);
	}

	// private static void processTopDown(final Scope scope) {
	//
	// final float available = scope.getEffort().getTopDownValue() - scope.getEffort().getBottomUpValue();
	//
	// final List<Scope> children = scope.getChildren();
	// for (final Scope child : children) {
	// if ()
	// }
	//
	// final List<Scope> childrenWithNonDeclaredEfforts = getChildrenWithNonDeclaredEfforts(scope);
	// final int size = childrenWithNonDeclaredEfforts.size();
	// if (size == 0) return;
	//
	// final float portion = difference / size;
	// for (final Scope child : childrenWithNonDeclaredEfforts) {
	// // TODO Review the method of calculation - could pre calculate it once using the (infered - sumOfDeclaratedChildren / size)
	// child.getEffort().setCalculated(child.getEffort().getCalculated() + portion);
	// processTopDown(child);
	// }
	// }
	//
	// private static List<Scope> getChildrenWithNonDeclaredEfforts(final Scope scope) {
	// final List<Scope> childrenWithNonDeclaredEfforts = new ArrayList<Scope>();
	// for (final Scope child : scope.getChildren())
	// if (!child.getEffort().hasDeclared()) childrenWithNonDeclaredEfforts.add(child);
	//
	// return childrenWithNonDeclaredEfforts;
	// }
	//
	// private static int calculateEffort(final Scope scope, int i) {
	// i -= 1;
	// int sum = 0;
	// for (final Scope child : scope.getChildren()) {
	// if (i > 0) calculateEffort(child, i);
	// sum += child.getEffort().getInfered();
	// }
	// scope.getEffort().setCalculated(sum);
	//
	// return sum;
	// }
}
