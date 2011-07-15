package br.com.oncast.ontrack.shared.model.effort;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Effort implements IsSerializable {

	private int declared;
	private boolean hasDeclared;
	private float topDownValue;
	private float bottomUpValue;
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

	public boolean hasDeclared() {
		return hasDeclared;
	}

	public void resetDeclared() {
		hasDeclared = false;
		declared = 0;
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

	@Override
	public String toString() {
		return "Declared: " + declared + ", TopDownValue: " + topDownValue + ", BottomUpValue: " + bottomUpValue + ", Infered: " + getInfered();
	}
}
