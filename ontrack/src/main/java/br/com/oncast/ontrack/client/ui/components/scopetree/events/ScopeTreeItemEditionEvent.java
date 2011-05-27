package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionEvent extends GwtEvent<ScopeTreeItemEditionEventHandler> {

	public static Type<ScopeTreeItemEditionEventHandler> TYPE;
	private final ScopeTreeItem scopeTreeItem;
	private final String pattern;

	public ScopeTreeItemEditionEvent(final ScopeTreeItem scopeTreeItem, final String pattern) {
		this.scopeTreeItem = scopeTreeItem;
		this.pattern = pattern;
	}

	public static Type<ScopeTreeItemEditionEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemEditionEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemEditionEventHandler> getAssociatedType() {
		return ScopeTreeItemEditionEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemEditionEventHandler handler) {
		handler.onItemUpdate(scopeTreeItem, pattern);
	}
}
