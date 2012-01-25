package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.HashMap;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.ProgressStateContainer;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanPanel extends Composite {

	private static final String DEFAULT_NOT_STARTED_NAME = "Not Started";

	private static KanbanPanelUiBinder uiBinder = GWT.create(KanbanPanelUiBinder.class);

	interface KanbanPanelUiBinder extends UiBinder<Widget, KanbanPanel> {}

	@UiField
	protected HorizontalPanel board;

	private final HashMap<String, ProgressStateContainer> columnMap;

	public KanbanPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		columnMap = new HashMap<String, ProgressStateContainer>();
	}

	public void setRelease(final Release release) {
		for (final Scope scope : release.getAllScopesIncludingChildrenReleases())
			addItem(scope.getProgress(), release.getScopeIndex(scope), scope);
	}

	public void addItem(final Progress progress, final int priority, final Scope scope) {
		final String trim = progress.getDescription().trim();
		if (!columnMap.containsKey(trim)) {
			final ProgressStateContainer progressStateContainer = new ProgressStateContainer(trim.isEmpty() ? DEFAULT_NOT_STARTED_NAME : trim);
			columnMap.put(trim, progressStateContainer);
			board.add(progressStateContainer);
		}
		columnMap.get(trim).add(scope);
	}
}
