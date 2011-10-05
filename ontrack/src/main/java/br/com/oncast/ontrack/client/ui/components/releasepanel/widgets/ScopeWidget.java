package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite implements ModelWidget<Scope> {

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	@UiField
	FocusPanel panel;

	@UiField
	Label descriptionLabel;

	@UiField
	Label progressLabel;

	@UiField
	FocusPanel dragImage;

	@UiField
	protected MouseCommandsMenu mouseActionsMenu;

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeProgress;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	@UiFactory
	protected MouseCommandsMenu createMouseActionMenu() {
		final List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();
		itens.add(new CommandMenuItem("Increase priority", new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onScopeIncreasePriorityRequest(scope);
			}
		}));
		itens.add(new CommandMenuItem("Decrease priority", new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onScopeDecreasePriorityRequest(scope);
			}
		}));

		return new MouseCommandsMenu(itens);
	}

	public ScopeWidget(final Scope scope, final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
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

		progressLabel.setText(currentScopeProgress);
		progressLabel.setVisible(!currentScopeProgress.isEmpty());

		return true;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}

	public Widget getDraggableArea() {
		return dragImage;
	}

	@UiHandler("panel")
	public void onScopeWidgetClick(final ClickEvent e) {
		releasePanelInteractionHandler.onScopeSelectionRequest(scope);
	}

}
