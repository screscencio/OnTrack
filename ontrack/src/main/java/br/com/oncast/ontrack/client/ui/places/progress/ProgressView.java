package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.IsWidget;

public interface ProgressView extends IsWidget {

	ApplicationMenu getApplicationMenu();

	void addItem(Progress progress, int priority, Scope scope);
}
