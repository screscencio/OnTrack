package br.com.oncast.ontrack.client.ui.component.scopetree.events;

import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionEvent extends GwtEvent<ScopeTreeItemEditionEventHandler> {

	public static Type<ScopeTreeItemEditionEventHandler> TYPE;
	private final ScopeTreeItem scopeTreeItem;
	private final String newContent;

	public ScopeTreeItemEditionEvent(final ScopeTreeItem scopeTreeItem, final String newContent) {
		this.scopeTreeItem = scopeTreeItem;
		this.newContent = newContent;
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
		handler.onItemUpdate(scopeTreeItem, newContent);
	}
}
