package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar.SearchBar;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface PlanningView extends IsWidget {

	ScopeTree getScopeTree();

	ReleasePanel getReleasePanel();

	ApplicationMenu getApplicationMenu();

	SearchBar getSearchBar();

	void setVisible(boolean b);

	boolean ensureWidgetIsVisible(IsWidget widget);

	Widget getNotificationMenu();
}
