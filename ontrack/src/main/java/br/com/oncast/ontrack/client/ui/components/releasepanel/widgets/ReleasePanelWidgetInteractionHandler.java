package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ReleasePanelWidgetInteractionHandler {

	void onReleaseDeletionRequest(Release release);

	void onReleaseIncreasePriorityRequest(Release release);

	void onReleaseDecreasePriorityRequest(Release release);

	void onScopeDragAndDropRequest(Scope scope, Release targetRelease, int newPriority);

	void onScopeIncreasePriorityRequest(Scope scope);

	void onScopeDecreasePriorityRequest(Scope scope);

	void onScopeSelectionRequest(Scope scope);

	void onOpenReleaseBurnUpChart(ReleaseChartPanel progressChartPanel);
}
