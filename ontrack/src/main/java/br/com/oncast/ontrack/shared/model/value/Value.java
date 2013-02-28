package br.com.oncast.ontrack.shared.model.value;

import java.io.Serializable;

import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class Value implements Serializable {

	private static final long serialVersionUID = 1L;

	private float declared;
	private float topDownValue;
	private float bottomUpValue;
	private boolean hasDeclared;
	private float accomplished;

	@IgnoredByDeepEquality
	private boolean hasStronglyDefinedChildren;

	public float getDeclared() {
		return declared;
	}

	public void setDeclared(final float declared) {
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
		final float processedValue = Math.max(bottomUpValue, topDownValue);
		return Math.max(declared, processedValue);
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

	public float getAccomplished() {
		return accomplished;
	}

	public void setAccomplished(final float accomplishedValue) {
		this.accomplished = accomplishedValue;
	}

	public float getAccomplishedPercentual() {
		final float inferedValue = getInfered();
		if (inferedValue == 0) return 0;

		return 100 * getAccomplished() / inferedValue;
	}

	@Override
	public String toString() {
		return "Declared: " + declared + ", TopDownValue: " + topDownValue + ", BottomUpValue: " + bottomUpValue + ", Infered: " + getInfered();
	}

}
