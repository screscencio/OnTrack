package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;

public interface ReleasePanelWidgetInteractionHandler {

	void onReleaseDeletionRequest(Release release);

}
