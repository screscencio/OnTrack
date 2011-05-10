package br.com.oncast.ontrack.client.ui.component.scopetree.widget.event;

import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionEvent extends GwtEvent<ScopeTreeItemEditionEventHandler> {

	public static Type<ScopeTreeItemEditionEventHandler> TYPE;
	private final ScopeTreeItem scopeTreeItem;

	public ScopeTreeItemEditionEvent(final ScopeTreeItem scopeTreeItem) {
		this.scopeTreeItem = scopeTreeItem;
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
		handler.onItemUpdate(scopeTreeItem);
	}
}