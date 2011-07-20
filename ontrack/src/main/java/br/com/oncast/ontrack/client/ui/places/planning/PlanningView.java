package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.IsWidget;

public interface PlanningView extends IsWidget {
	void setScope(final Scope scope);

	void setRelease(Release release);

	List<ActionExecutionListener> getActionExecutionSuccessListeners();

	void setActionExecutionRequestHandler(ActionExecutionRequestHandler actionHandler);

	void setExporterPath(String href);
}
