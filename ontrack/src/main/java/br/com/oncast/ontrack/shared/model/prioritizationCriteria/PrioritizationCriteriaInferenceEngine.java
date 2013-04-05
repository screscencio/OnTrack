package br.com.oncast.ontrack.shared.model.prioritizationCriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public abstract class PrioritizationCriteriaInferenceEngine implements InferenceOverScopeEngine {

	protected abstract PrioritizationCriteria getCriteria(Scope scope);

	@Override
	public Set<UUID> process(final Scope scope, final UserRepresentation user, final Date timestamp) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();

		inferenceInfluencedScopeSet.add(scope.getId());
		preProcessBottomUp(scope, 2, inferenceInfluencedScopeSet);
		processTopDown(processBottomUp(scope, inferenceInfluencedScopeSet), inferenceInfluencedScopeSet);
		processTopDown(scope, inferenceInfluencedScopeSet);
		processTopDown(scope.isRoot() ? scope : scope.getParent(), inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private void preProcessBottomUp(final Scope scope, int recursionIndex, final Set<UUID> inferenceInfluencedScopeSet) {
		recursionIndex -= 1;
		float sum = 0;

		final PrioritizationCriteria criteria = getCriteria(scope);
		boolean hasStronglyDefinedChildren = scope.getChildren().size() > 0;
		for (final Scope child : scope.getChildren()) {
			if (recursionIndex > 0) preProcessBottomUp(child, recursionIndex, inferenceInfluencedScopeSet);

			final PrioritizationCriteria childCriteria = getCriteria(child);
			sum += Math.max(childCriteria.getDeclared(), childCriteria.getBottomUpValue());
			hasStronglyDefinedChildren &= childCriteria.isStronglyDefined();
		}

		if (criteria.getBottomUpValue() != sum || criteria.getHasStronglyDefinedChildren() != hasStronglyDefinedChildren) {
			criteria.setBottomUpValue(sum);
			criteria.setHasStronglyDefinedChildren(hasStronglyDefinedChildren);
			inferenceInfluencedScopeSet.add(scope.getId());
		}
	}

	private Scope processBottomUp(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();
		final PrioritizationCriteria parentCriteria = getCriteria(parent);

		final float inferedInitial = parentCriteria.getInfered();
		final float bottomUpInitial = parentCriteria.getBottomUpValue();
		preProcessBottomUp(parent, 0, inferenceInfluencedScopeSet);
		final float inferedFinal = parentCriteria.getInfered();
		final float bottomUpFinal = parentCriteria.getBottomUpValue();

		if (inferedFinal == inferedInitial && bottomUpInitial == bottomUpFinal) return parent;
		return processBottomUp(parent, inferenceInfluencedScopeSet);
	}

	private void processTopDown(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		final PrioritizationCriteria criteria = getCriteria(scope);

		final float initialTopDownValue = criteria.getTopDownValue();
		if (criteria.isStronglyDefined()) criteria.setTopDownValue(Math.max(criteria.getDeclared(), criteria.getBottomUpValue()));
		else if (scope.isRoot()) {
			final float stronglyDefinedCriteriaSum = getStronglyDefinedCriteriaSum(scope);
			final float bottomUpValue = criteria.getBottomUpValue();
			criteria.setTopDownValue(stronglyDefinedCriteriaSum > bottomUpValue ? stronglyDefinedCriteriaSum : bottomUpValue);
		}
		if (criteria.getTopDownValue() != initialTopDownValue) inferenceInfluencedScopeSet.add(scope.getId());

		float available = criteria.getTopDownValue() - getStronglyDefinedCriteriaSum(scope);
		if (available < 0) available = 0;

		final float portion = getPortion(available, getChildrenWithNonStronglyDefinedCriterias(scope));

		final List<Scope> childrenList = scope.getChildren();
		for (final Scope child : childrenList) {
			final PrioritizationCriteria childCriteria = getCriteria(child);
			final boolean isChildStronglyDefined = childCriteria.isStronglyDefined();
			final float value = isChildStronglyDefined && (childCriteria.getDeclared() > childCriteria.getBottomUpValue()) ? childCriteria.getDeclared()
					: childCriteria
							.getBottomUpValue();

			final float childInitialTopDownValue = childCriteria.getTopDownValue();
			if (value > portion) childCriteria.setTopDownValue(value);
			else childCriteria.setTopDownValue(isChildStronglyDefined ? childCriteria.getDeclared() : portion);

			if (childCriteria.getTopDownValue() != childInitialTopDownValue) {
				inferenceInfluencedScopeSet.add(child.getId());
				processTopDown(child, inferenceInfluencedScopeSet);
			}
		}
	}

	private float getStronglyDefinedCriteriaSum(final Scope scope) {
		float sum = 0;
		final List<Scope> children = scope.getChildren();
		for (final Scope child : children) {
			final PrioritizationCriteria criteria = getCriteria(child);
			if (criteria.isStronglyDefined()) sum += Math.max(criteria.getDeclared(), criteria.getBottomUpValue());
		}

		return sum;
	}

	private List<Scope> getChildrenWithNonStronglyDefinedCriterias(final Scope scope) {
		final List<Scope> childrenWithNonStronglyDefinedCriterias = new ArrayList<Scope>();
		for (final Scope child : scope.getChildren())
			if (!getCriteria(child).isStronglyDefined()) childrenWithNonStronglyDefinedCriterias.add(child);

		return childrenWithNonStronglyDefinedCriterias;
	}

	private float getPortion(final float available, final List<Scope> scopeList) {
		if (available <= 0) return 0;
		if (scopeList.size() == 0) return 0;

		final float portion = available / scopeList.size();
		for (final Scope scope : scopeList) {
			final float bottomUpValue = getCriteria(scope).getBottomUpValue();
			if (bottomUpValue > portion) {
				final List<Scope> list = scopeList;
				list.remove(scope);
				return getPortion(available - bottomUpValue, list);
			}
		}

		return portion;
	}
}
