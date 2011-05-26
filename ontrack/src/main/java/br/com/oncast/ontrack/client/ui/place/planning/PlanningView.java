package br.com.oncast.ontrack.client.ui.place.planning;

import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.ui.IsWidget;

public interface PlanningView extends IsWidget {
	void setScope(final Scope scope);

	void setReleases(List<Release> releases);
}
