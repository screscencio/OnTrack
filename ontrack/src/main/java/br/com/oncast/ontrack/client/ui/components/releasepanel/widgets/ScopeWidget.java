package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.UNDER_WORK;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite implements ModelWidget<Scope> {

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	interface ScopeWidgetStyle extends CssResource {
		String progressIconDone();

		String progressIconNotStarted();

		String progressIconUnderwork();

		String selected();

		String statusBarOpenImpediment();
	}

	@UiField
	ScopeWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	// TODO use FastLabel
	Label descriptionLabel;

	@UiField
	FocusPanel draggableAnchor;

	@UiField
	SimplePanel progressIcon;

	@UiField
	FocusPanel statusBar;

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeProgress;

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		update();
		setHasOpenImpediments(ClientServiceProvider.getInstance().getAnnotationService().hasOpenImpediment(scope.getId()));
	}

	@Override
	public boolean update() {
		return updateDescription() | updateProgress() | updateTitle();
	}

	private boolean updateTitle() {
		final String title = buildLineageRepresentationText();
		if (title.isEmpty() || title.equals(panel.getTitle())) return false;

		descriptionLabel.setTitle(title);
		return true;
	}

	private String buildLineageRepresentationText() {
		if (scope.isRoot()) return "";

		final StringBuilder builder = new StringBuilder();
		Scope current = scope.getParent();
		while (!current.isRoot()) {
			builder.insert(0, current.getDescription());
			builder.insert(0, " > ");
			current = current.getParent();
		}
		builder.insert(0, current.getDescription());
		final String title = builder.toString();
		return title;
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		final String description = scope.getDescription();
		if (description.equals(currentScopeDescription)) return false;
		currentScopeDescription = description;

		descriptionLabel.setText(currentScopeDescription);

		return true;
	}

	// TODO+++ centralize progress calculations and removing from here.
	/**
	 * @return if the progress was updated.
	 */
	private boolean updateProgress() {
		final Progress progress = scope.getProgress();
		final String description = progress.getDescription();
		if (!description.isEmpty() && description.equals(currentScopeProgress)) return false;
		currentScopeProgress = description;

		final boolean done = progress.isDone();
		final boolean notStarted = progress.getState() == ProgressState.NOT_STARTED && scope.getEffort().getAccomplishedEffort() == 0
				&& !isAnyDescendantUnderWork();

		progressIcon.setStyleName(style.progressIconDone(), done);
		progressIcon.setStyleName(style.progressIconNotStarted(), notStarted);
		progressIcon.setStyleName(style.progressIconUnderwork(), progress.getState() == UNDER_WORK || (!done && !notStarted));

		return true;
	}

	private boolean isAnyDescendantUnderWork() {
		for (final Scope descendant : scope.getAllDescendantScopes()) {
			if (descendant.getProgress().getState() == ProgressState.UNDER_WORK) return true;
		}
		return false;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}

	public Widget getDraggableAnchor() {
		return draggableAnchor;
	}

	@UiHandler("panel")
	public void onScopeWidgetClick(final ClickEvent e) {
		ClientServiceProvider.getInstance().getEventBus().fireEventFromSource(new ScopeSelectionEvent(scope), this);
	}

	public void setSelected(final boolean shouldSelect) {
		panel.setStyleName(style.selected(), shouldSelect);
	}

	public void setHasOpenImpediments(final boolean hasOpenImpediments) {
		statusBar.setStyleName(style.statusBarOpenImpediment(), hasOpenImpediments);
	}

}
