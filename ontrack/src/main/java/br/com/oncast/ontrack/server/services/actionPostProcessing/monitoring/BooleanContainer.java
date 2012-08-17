package br.com.oncast.ontrack.server.services.actionPostProcessing.monitoring;

import java.util.Stack;

class BooleanContainer {

	private final Stack<Boolean> values = new Stack<Boolean>();

	public boolean getValue() {
		if (values.isEmpty()) return false;
		return values.peek();
	}

	public void putValue(final boolean value) {
		values.push(value);
	}

	public boolean popValue() {
		return values.pop();
	}
}
