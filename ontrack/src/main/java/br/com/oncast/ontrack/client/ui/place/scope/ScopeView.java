package br.com.oncast.ontrack.client.ui.place.scope;

import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.IsWidget;

public interface ScopeView extends IsWidget {
	void setScope(final Scope scope);
}
