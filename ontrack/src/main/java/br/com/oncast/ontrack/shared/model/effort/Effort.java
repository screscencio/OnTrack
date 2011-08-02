package br.com.oncast.ontrack.shared.model.effort;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Effort implements IsSerializable {

	private int declared;
	private float topDownValue;
	private float bottomUpValue;
	private boolean hasDeclared;
	private boolean hasTopDownValue;
	private boolean hasBottomUpValue;
	private boolean hasStronglyDefinedChildren;
	private float computedEffort;

	public int getDeclared() {
		return declared;
	}

	public void setDeclared(final int declared) {
		this.declared = declared;
		this.hasDeclared = true;
	}

	public boolean isStronglyDefined() {
		return this.hasStronglyDefinedChildren || this.hasDeclared;
	}

	public void setHasStronglyDefinedChildren(final boolean stronglyDefined) {
		this.hasStronglyDefinedChildren = stronglyDefined;
	}

	public boolean getHasStronglyDefinedChildren() {
		return hasStronglyDefinedChildren;
	}

	public boolean hasDeclared() {
		return hasDeclared;
	}

	public void resetDeclared() {
		hasDeclared = false;
		declared = 0;
	}

	public boolean hasInfered() {
		return hasDeclared || hasBottomUpValue || hasTopDownValue;
	}

	public float getInfered() {
		final float processedValue = (bottomUpValue > topDownValue) ? bottomUpValue : topDownValue;
		return (declared > processedValue) ? declared : processedValue;
	}

	public float getTopDownValue() {
		return topDownValue;
	}

	public void setTopDownValue(final float topDownValue) {
		this.topDownValue = topDownValue;
		hasTopDownValue = true;
	}

	public float getBottomUpValue() {
		return bottomUpValue;
	}

	public void setBottomUpValue(final float bottomUpValue) {
		this.bottomUpValue = bottomUpValue;
		hasBottomUpValue = true;
	}

	public float getComputedEffort() {
		return computedEffort;
	}

	public void setComputedEffort(final float computedEffort) {
		this.computedEffort = computedEffort;
	}

	@Override
	public String toString() {
		return "Declared: " + declared + ", TopDownValue: " + topDownValue + ", BottomUpValue: " + bottomUpValue + ", Infered: " + getInfered();
	}

	public float getComputedPercentual() {
		final float inferedEffort = getInfered();
		if (inferedEffort == 0) return 0;

		return 100 * getComputedEffort() / inferedEffort;
	}

}
