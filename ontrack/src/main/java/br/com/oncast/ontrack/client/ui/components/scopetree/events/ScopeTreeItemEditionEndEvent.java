package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionEndEvent extends GwtEvent<ScopeTreeItemEditionEndEventHandler> {

	public static Type<ScopeTreeItemEditionEndEventHandler> TYPE;
	private final ScopeTreeItem scopeTreeItem;
	private final String value;

	public ScopeTreeItemEditionEndEvent(final ScopeTreeItem scopeTreeItem, final String value) {
		this.scopeTreeItem = scopeTreeItem;
		this.value = value;
	}

	public static Type<ScopeTreeItemEditionEndEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemEditionEndEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemEditionEndEventHandler> getAssociatedType() {
		return ScopeTreeItemEditionEndEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemEditionEndEventHandler handler) {
		handler.onItemEditionEnd(scopeTreeItem, value);
	}
}
