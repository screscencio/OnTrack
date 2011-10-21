package br.com.oncast.ontrack.shared.model.effort;

import java.io.Serializable;

import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class Effort implements Serializable {

	private static final long serialVersionUID = 1L;

	private int declared;
	private float topDownValue;
	private float bottomUpValue;
	private boolean hasDeclared;
	private float accomplishedEffort;
	@IgnoredByDeepEquality
	private boolean hasStronglyDefinedChildren;

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
		return hasDeclared || topDownValue > 0 || bottomUpValue > 0;
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
	}

	public float getBottomUpValue() {
		return bottomUpValue;
	}

	public void setBottomUpValue(final float bottomUpValue) {
		this.bottomUpValue = bottomUpValue;
	}

	public float getAccomplishedEffort() {
		return accomplishedEffort;
	}

	public void setAccomplishedEffort(final float accomplishedEffort) {
		this.accomplishedEffort = accomplishedEffort;
	}

	public float getAccomplishedPercentual() {
		final float inferedEffort = getInfered();
		if (inferedEffort == 0) return 0;

		return 100 * getAccomplishedEffort() / inferedEffort;
	}

	@Override
	public String toString() {
		return "Declared: " + declared + ", TopDownValue: " + topDownValue + ", BottomUpValue: " + bottomUpValue + ", Infered: " + getInfered();
	}

}
