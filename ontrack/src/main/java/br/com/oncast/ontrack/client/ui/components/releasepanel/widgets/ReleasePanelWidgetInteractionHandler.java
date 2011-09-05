package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ReleasePanelWidgetInteractionHandler {

	void onReleaseDeletionRequest(Release release);

	void onScopeIncreasePriorityRequest(Scope scope);

	void onScopeDecreasePriorityRequest(Scope scope);

}
