package br.com.oncast.ontrack.client.ui.components;

import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ScopeWidget extends ModelWidget<Scope> {

	void setHighlighted(boolean b);

	void addAssociatedUsers(DraggableMemberWidget draggable);

	boolean isHighlighted();

}
