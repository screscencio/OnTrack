package br.com.oncast.ontrack.client.ui.places.progress.details;

import br.com.oncast.ontrack.client.ui.generalwidgets.AddTaskWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.CheckListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.DescriptionWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.RemoveTaskWidget;
import br.com.oncast.ontrack.client.ui.places.progress.details.ProgressDetailMenuWidget.ProgressDetailMenuListener;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDetailPanel extends Composite {

	private static ProgressDetailPanelUiBinder uiBinder = GWT.create(ProgressDetailPanelUiBinder.class);

	@UiField(provided = true)
	protected DescriptionWidget descriptionWidget;

	@UiField(provided = true)
	protected CheckListWidget checklistWidget;

	@UiField(provided = true)
	protected AddTaskWidget addTaskWidget;

	@UiField(provided = true)
	protected RemoveTaskWidget removeTaskWidget;

	@UiField
	protected DeckPanel deckPanel;

	@UiField(provided = true)
	protected ProgressDetailMenuWidget progressMenu;

	interface ProgressDetailPanelUiBinder extends UiBinder<Widget, ProgressDetailPanel> {}

	public ProgressDetailPanel(final Release release) {
		initializeComponents(release);

		initWidget(uiBinder.createAndBindUi(this));
		progressMenu.setSelected(0);
		deckPanel.showWidget(0);

		progressMenu.setEnabled(2, false);
		progressMenu.setEnabled(3, false);
	}

	public void setSelected(final Scope scope) {
		descriptionWidget.setSelected(scope);
		checklistWidget.setSelected(scope);
		addTaskWidget.setSelected(scope);
		removeTaskWidget.setSelected(scope);

		progressMenu.setEnabled(2, scope != null);
		progressMenu.setEnabled(3, scope != null && !scope.isStory());
	}

	private ProgressDetailMenuListener getProgressMenuListener() {
		return new ProgressDetailMenuListener() {
			@Override
			public void onIndexSelected(final int index) {
				deckPanel.showWidget(index);
				final Widget w = deckPanel.getWidget(index);
				if (w instanceof Focusable) ((Focusable) w).setFocus(true);
			}
		};
	}

	private void initializeComponents(final Release release) {
		descriptionWidget = new DescriptionWidget(release);
		checklistWidget = new CheckListWidget(release);
		addTaskWidget = new AddTaskWidget();
		removeTaskWidget = new RemoveTaskWidget();

		progressMenu = new ProgressDetailMenuWidget(getProgressMenuListener());
	}

}
