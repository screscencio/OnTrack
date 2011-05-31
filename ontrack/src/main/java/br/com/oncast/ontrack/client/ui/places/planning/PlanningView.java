package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.ui.IsWidget;

public interface PlanningView extends IsWidget {
	void setScope(final Scope scope);

	void setRelease(Release release);

	List<ActionExecutionListener> getActionExecutionSuccessListeners();

	void setActionExecutionRequestHandler(ActionExecutionRequestHandler actionHandler);
}
