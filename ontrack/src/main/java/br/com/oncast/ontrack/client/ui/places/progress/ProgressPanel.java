package br.com.oncast.ontrack.client.ui.places.progress;

import java.util.HashMap;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.ProgressStateContainer;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite implements ProgressView {

	private static final String DEFAULT_NOT_STARTED_NAME = "Not Started";

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, ProgressPanel> {}

	@UiField
	protected ApplicationMenu applicationMenu;

	@UiField
	protected HorizontalPanel board;

	private final HashMap<String, ProgressStateContainer> columnMap;

	public ProgressPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		columnMap = new HashMap<String, ProgressStateContainer>();
	}

	@Override
	public ApplicationMenu getApplicationMenu() {
		return applicationMenu;
	}

	@Override
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