package br.com.oncast.ontrack.shared.model.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ValueInferenceEngine implements InferenceOverScopeEngine {

	@Override
	public boolean shouldProcess(final ScopeAction action) {
		return action.changesValueInference();
	}

	@Override
	public Set<UUID> process(final Scope scope, final UserRepresentation user, final Date timestamp) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();

		preProcessBottomUp(scope, 2, inferenceInfluencedScopeSet);
		processTopDown(processBottomUp(scope, inferenceInfluencedScopeSet), inferenceInfluencedScopeSet);
		processTopDown(scope, inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private static void preProcessBottomUp(final Scope scope, int recursionIndex, final Set<UUID> inferenceInfluencedScopeSet) {
		recursionIndex -= 1;
		int sum = 0;

		final Value value = scope.getValue();
		boolean hasStronglyDefinedChildren = scope.getChildren().size() > 0;
		for (final Scope child : scope.getChildren()) {
			if (recursionIndex > 0) preProcessBottomUp(child, recursionIndex, inferenceInfluencedScopeSet);

			final Value childValue = child.getValue();
			sum += childValue.getDeclared() > childValue.getBottomUpValue() ? childValue.getDeclared() : childValue.getBottomUpValue();
			hasStronglyDefinedChildren &= childValue.isStronglyDefined();
		}

		if (value.getBottomUpValue() != sum || value.getHasStronglyDefinedChildren() != hasStronglyDefinedChildren) {
			value.setBottomUpValue(sum);
			value.setHasStronglyDefinedChildren(hasStronglyDefinedChildren);
			inferenceInfluencedScopeSet.add(scope.getId());
		}
	}

	private static Scope processBottomUp(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();
		final Value parentValue = parent.getValue();

		final float inferedInitial = parentValue.getInfered();
		final float bottomUpInitial = parentValue.getBottomUpValue();
		preProcessBottomUp(parent, 0, inferenceInfluencedScopeSet);
		final float inferedFinal = parentValue.getInfered();
		final float bottomUpFinal = parentValue.getBottomUpValue();

		if (inferedFinal == inferedInitial && bottomUpInitial == bottomUpFinal) return parent;
		return processBottomUp(parent, inferenceInfluencedScopeSet);
	}

	private static void processTopDown(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		final Value value = scope.getValue();

		final float initialTopDownValue = value.getTopDownValue();
		if (value.isStronglyDefined()) value.setTopDownValue((value.getDeclared() > value.getBottomUpValue()) ? value.getDeclared() : value
				.getBottomUpValue());
		else if (scope.isRoot()) {
			final int stronglyDefinedValueSum = getStronglyDefinedValueSum(scope);
			final float bottomUpValue = value.getBottomUpValue();
			value.setTopDownValue(stronglyDefinedValueSum > bottomUpValue ? stronglyDefinedValueSum : bottomUpValue);
		}
		if (value.getTopDownValue() != initialTopDownValue) inferenceInfluencedScopeSet.add(scope.getId());

		float available = value.getTopDownValue() - getStronglyDefinedValueSum(scope);
		if (available < 0) available = 0;

		final List<Scope> childrenWithNonDeclaredValues = getChildrenWithNonStronglyDefinedValues(scope);
		final float portion = getPortion(available, childrenWithNonDeclaredValues);

		final List<Scope> childrenList = scope.getChildren();
		for (final Scope child : childrenList) {
			final Value childValue = child.getValue();
			final boolean isChildStronglyDefined = childValue.isStronglyDefined();
			final float v = isChildStronglyDefined && (childValue.getDeclared() > childValue.getBottomUpValue()) ? childValue.getDeclared()
					: childValue
							.getBottomUpValue();

			final float childInitialTopDownValue = childValue.getTopDownValue();
			if (v > portion) childValue.setTopDownValue(v);
			else childValue.setTopDownValue(isChildStronglyDefined ? childValue.getDeclared() : portion);

			if (childValue.getTopDownValue() != childInitialTopDownValue) {
				inferenceInfluencedScopeSet.add(child.getId());
				processTopDown(child, inferenceInfluencedScopeSet);
			}
		}
	}

	private static int getStronglyDefinedValueSum(final Scope scope) {
		int sum = 0;
		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			if (child.getValue().isStronglyDefined()) sum += (child.getValue().getDeclared() > child.getValue().getBottomUpValue()) ? child.getValue()
					.getDeclared() : child.getValue().getBottomUpValue();

		return sum;
	}

	private static List<Scope> getChildrenWithNonStronglyDefinedValues(final Scope scope) {
		final List<Scope> childrenWithNonStronglyDefinedValues = new ArrayList<Scope>();
		for (final Scope child : scope.getChildren())
			if (!child.getValue().isStronglyDefined()) childrenWithNonStronglyDefinedValues.add(child);

		return childrenWithNonStronglyDefinedValues;
	}

	private static float getPortion(final float available, final List<Scope> scopeList) {
		if (available <= 0) return 0;
		if (scopeList.size() == 0) return 0;

		final float portion = available / scopeList.size();
		for (final Scope scope : scopeList) {
			final float bottomUpValue = scope.getValue().getBottomUpValue();
			if (bottomUpValue > portion) {
				final List<Scope> list = scopeList;
				list.remove(scope);
				return getPortion(available - bottomUpValue, list);
			}
		}

		return portion;
	}
}
