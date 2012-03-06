package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
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
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite implements ModelWidget<Scope> {

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	interface ScopeWidgetStyle extends CssResource {
		String panelDone();
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

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeProgress;

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		updateDescription();
		updateProgress();
	}

	@Override
	public boolean update() {
		return updateDescription() | updateProgress();
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

	/**
	 * @return if the progress was updated.
	 */
	private boolean updateProgress() {
		final String description = scope.getProgress().getDescription();
		if (description.equals(currentScopeProgress)) return false;
		currentScopeProgress = description;

		panel.setStyleName(style.panelDone(), scope.getProgress().isDone());

		return true;
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

}
