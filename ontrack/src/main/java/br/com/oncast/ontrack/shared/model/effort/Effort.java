package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Effort implements IsSerializable, DeeplyComparable {

	private int declared;
	private boolean hasDeclared;
	private float topDownValue;
	private float bottomUpValue;

	public int getDeclared() {
		return declared;
	}

	public void setDeclared(final int declared) {
		this.declared = declared;
		this.hasDeclared = true;
	}

	public boolean hasDeclared() {
		return hasDeclared;
	}

	public void resetDeclared() {
		hasDeclared = false;
		declared = 0;
	}

	public float getInfered() {
		final float processedValue = getProcessedValue();
		return (declared > processedValue) ? declared : processedValue;
	}

	public float getProcessedValue() {
		return (bottomUpValue > topDownValue) ? bottomUpValue : topDownValue;
		// return (bottomUpValue != 0) ? bottomUpValue : topDownValue; // FIXME
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
	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Effort)) return false;

		final Effort other = (Effort) obj;
		if (bottomUpValue != other.bottomUpValue) return false;
		if (topDownValue != other.topDownValue) return false;
		if (declared != other.declared) return false;
		return true;
	}

	@Override
	public String toString() {
		return "Declared: " + declared + ", TopDownValue: " + topDownValue + ", BottomUpValue: " + bottomUpValue + ", Infered: " + getInfered();
	}
}
