package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeAddMemberSelectionEvent extends GwtEvent<ScopeAddMemberSelectionEventHandler> implements ScopeMemberSelectionEvent {

	public static Type<ScopeAddMemberSelectionEventHandler> TYPE;
	private final Scope scope;
	private final User member;
	private final String selectionColor;

	public ScopeAddMemberSelectionEvent(final User member, final Scope scope, final String selectionColor) {
		this.member = member;
		this.scope = scope;
		this.selectionColor = selectionColor;
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
	public User getMember() {
		return member;
	}

	public String getSelectionColor() {
		return selectionColor;
	}

}
