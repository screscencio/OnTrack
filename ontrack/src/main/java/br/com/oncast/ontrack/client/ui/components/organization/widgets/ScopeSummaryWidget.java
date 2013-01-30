package br.com.oncast.ontrack.client.ui.components.organization.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeSummaryWidget extends Composite implements ScopeWidget {

	private static ScopeSummaryWidgetUiBinder uiBinder = GWT.create(ScopeSummaryWidgetUiBinder.class);

	interface ScopeSummaryWidgetUiBinder extends UiBinder<Widget, ScopeSummaryWidget> {}

	interface ScopeSummaryWidgetStyle extends CssResource {
		String headerDone();
	}

	@UiField
	ScopeSummaryWidgetStyle style;

	@UiField
	FocusPanel header;

	@UiField
	SimplePanel progressBar;

	@UiField
	Label title;

	@UiField
	Label progress;

	private final Scope scope;

	private final UUID projectId;

	public ScopeSummaryWidget(final Scope scope, final UUID projectId) {
		this.scope = scope;
		this.projectId = projectId;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("header")
	protected void onHeaderMouseOver(final MouseOverEvent e) {
		ClientServiceProvider.getInstance().getEventBus().fireEvent(new ScopeSelectionEvent(scope, projectId));
	}

	@Override
	public boolean update() {
		title.setText(scope.getDescription());

		progressBar.getElement().getStyle().setRight((100.0 - scope.getEffort().getAccomplishedPercentual()), Unit.PCT);

		final Progress scopeProgress = scope.getProgress();
		header.setStyleName(style.headerDone(), scopeProgress.isDone());

		final String description = scopeProgress.getDescription();
		progress.setText(description.isEmpty() ? ProgressState.UNDER_WORK.getDescription() : description);
		progress.setVisible(scopeProgress.isUnderWork());
		return false;
	}

	@Override
	public Scope getModelObject() {
		return scope;
	}

	@Override
	public void addAssociatedUsers(final DraggableMemberWidget draggable) {}

	@Override
	public void setTargetHighlight(final boolean b) {}

	@Override
	public boolean isTargetHighlight() {
		return false;
	}
}
