package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;

import com.google.gwt.user.client.ui.IsWidget;

public interface PlanningView extends IsWidget {

	ScopeTree getScopeTree();

	ReleasePanel getReleasePanel();

	ApplicationMenu getApplicationMenu();

	void setVisible(boolean b);

	void ensureWidgetIsVisible(IsWidget widget);
}
