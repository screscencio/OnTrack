package br.com.oncast.ontrack.shared.model.effort;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Effort implements IsSerializable {

	private int declared;
	private boolean hasDeclared;
	private float calculed;

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

	public float getCalculed() {
		return calculed;
	}

	public void setCalculed(final float calculed) {
		this.calculed = calculed;
	}

	public float getInfered() {
		return (declared > calculed) ? declared : calculed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(calculed);
		result = prime * result + declared;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Effort)) return false;

		final Effort other = (Effort) obj;
		if (Float.floatToIntBits(calculed) != Float.floatToIntBits(other.calculed)) return false;
		if (declared != other.declared) return false;
		return true;
	}

}
