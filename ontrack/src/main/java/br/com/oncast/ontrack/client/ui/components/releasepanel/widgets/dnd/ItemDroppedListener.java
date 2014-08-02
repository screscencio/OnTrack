package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ItemDroppedListener {

	void onItemDropped(Scope droppedScope, Release targetRelease, int newScopePosition);

	void onItemDropped(Scope droppedScope, String newProgress);

}
