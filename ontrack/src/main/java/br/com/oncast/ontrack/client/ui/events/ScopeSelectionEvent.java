package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeSelectionEvent extends GwtEvent<ScopeSelectionEventHandler> {

	public static Type<ScopeSelectionEventHandler> TYPE;
	private final Scope scope;
	private final UUID projectId;

	public ScopeSelectionEvent(final Scope scope) {
		this(scope, null);
	}

	public ScopeSelectionEvent(final Scope scope, final UUID projectId) {
		this.scope = scope;
		this.projectId = projectId;
	}

	public static Type<ScopeSelectionEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeSelectionEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeSelectionEventHandler> getAssociatedType() {
		return ScopeSelectionEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeSelectionEventHandler handler) {
		handler.onScopeSelectionRequest(this);
	}

	public Scope getTargetScope() {
		return scope;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
