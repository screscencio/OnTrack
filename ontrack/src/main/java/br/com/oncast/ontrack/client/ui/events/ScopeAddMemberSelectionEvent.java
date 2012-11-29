package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeAddMemberSelectionEvent extends GwtEvent<ScopeAddMemberSelectionEventHandler> implements ScopeMemberSelectionEvent {

	public static Type<ScopeAddMemberSelectionEventHandler> TYPE;
	private final Scope scope;
	private final UserRepresentation member;
	private final Color selectionColor;

	public ScopeAddMemberSelectionEvent(final UserRepresentation member, final Scope scope, final Color color) {
		this.member = member;
		this.scope = scope;
		this.selectionColor = color;
	}

	public static Type<ScopeAddMemberSelectionEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeAddMemberSelectionEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeAddMemberSelectionEventHandler> getAssociatedType() {
		return ScopeAddMemberSelectionEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeAddMemberSelectionEventHandler handler) {
		handler.onMemberSelectedScope(this);
	}

	@Override
	public Scope getTargetScope() {
		return scope;
	}

	@Override
	public UserRepresentation getMember() {
		return member;
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

}
