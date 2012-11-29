package br.com.oncast.ontrack.server.model.scope;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

public class ScopeBuilder {
	private final Scope scope;

	private ScopeBuilder(final UserRepresentation author, final Date timestamp) {
		scope = new Scope("", author, timestamp);
	}

	public static ScopeBuilder scope(final UserRepresentation author, final Date timestamp) {
		return new ScopeBuilder(author, timestamp);
	}

	public static ScopeBuilder scope(final String description, final UserRepresentation author, final Date timestamp) {
		return scope(author, timestamp).description(description);
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

	public ScopeBuilder declaredValue(final float value) {
		scope.getValue().setDeclared(value);
		return this;
	}

	public ScopeBuilder topDownValue(final float value) {
		scope.getValue().setTopDownValue(value);
		return this;
	}

	public ScopeBuilder bottomUpValue(final float value) {
		scope.getValue().setBottomUpValue(value);
		return this;
	}

	public ScopeBuilder accomplishedValue(final float value) {
		scope.getValue().setAccomplished(value);
		return this;
	}

	public ScopeBuilder declaredEffort(final float value) {
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

	public ScopeBuilder accomplishedEffort(final float value) {
		scope.getEffort().setAccomplished(value);
		return this;
	}

	public ScopeBuilder declaredProgress(final String value, final UserRepresentation author, final Date timestamp) {
		scope.getProgress().setDescription(value, author, timestamp);
		return this;
	}

	public Scope getScope() {
		return scope;
	}
}
