package br.com.oncast.ontrack.client.ui.place.scope;

import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTree;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.ui.Widget;

public class ScopeViewImpl implements ScopeView {

	private final ScopeTree scopeTree;

	public ScopeViewImpl() {
		scopeTree = new ScopeTree();
	}

	@Override
	public Widget asWidget() {
		return scopeTree.asWidget();
	}

	@Override
	public void setScope(final Scope scope) {
		scopeTree.setScope(scope);
		scopeTree.setFocus(true);
	}
}