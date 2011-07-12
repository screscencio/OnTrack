package br.com.oncast.ontrack.server.util.mindmapconverter.scope;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeBuilder {
	private final Scope scope = new Scope("");

	private ScopeBuilder() {}

	public static ScopeBuilder scope(final String description) {
		return new ScopeBuilder().description(description);
	}

	public static ScopeBuilder scope() {
		return new ScopeBuilder();
	}

	public ScopeBuilder add(final Scope child) {
		scope.add(child);
		return this;
	}

	public ScopeBuilder add(final ScopeBuilder builder) {
		scope.add(builder.getScope());
		return this;
	}

	public ScopeBuilder description(final String description) {
		scope.setDescription(description);
		return this;
	}

	public ScopeBuilder declaredEffort(final int value) {
		scope.getEffort().setDeclared(value);
		return this;
	}

	public ScopeBuilder topDownEffort(final float value) {
		scope.getEffort().setTopDownValue(value);
		return this;
	}

	public ScopeBuilder bottomUpEffort(final float value) {
		scope.getEffort().setBottomUpValue(value);
		return this;
	}

	public Scope getScope() {
		return scope;
	}
}
