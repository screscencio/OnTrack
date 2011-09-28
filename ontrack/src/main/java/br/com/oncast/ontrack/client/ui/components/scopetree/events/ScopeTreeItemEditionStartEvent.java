package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionStartEvent extends GwtEvent<ScopeTreeItemEditionStartEventHandler> {

	public static Type<ScopeTreeItemEditionStartEventHandler> TYPE;
	private final ScopeTreeItem scopeTreeItem;

	public ScopeTreeItemEditionStartEvent(final ScopeTreeItem scopeTreeItem) {
		this.scopeTreeItem = scopeTreeItem;
	}

	public static Type<ScopeTreeItemEditionStartEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemEditionStartEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemEditionStartEventHandler> getAssociatedType() {
		return ScopeTreeItemEditionStartEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemEditionStartEventHandler handler) {
		handler.onItemEditionStart(scopeTreeItem);
	}
}
