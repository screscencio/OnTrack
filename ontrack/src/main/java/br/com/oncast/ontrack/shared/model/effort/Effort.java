package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Effort implements IsSerializable, DeeplyComparable {

	private int declared;
	private boolean hasDeclared;
	private float calculated;

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

	public float getCalculated() {
		return calculated;
	}

	public void setCalculated(final float calculed) {
		this.calculated = calculed;
	}

	public float getInfered() {
		return (declared > calculated) ? declared : calculated;
	}

	@Override
	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Effort)) return false;

		final Effort other = (Effort) obj;
		if (Float.floatToIntBits(calculated) != Float.floatToIntBits(other.calculated)) return false;
		if (declared != other.declared) return false;
		return true;
	}

	@Override
	public String toString() {
		return "Declared: " + declared + ", Calculated: " + calculated + ", Infered: " + getInfered();
	}
}
