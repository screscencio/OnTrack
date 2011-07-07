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
}
