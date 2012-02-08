package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.TextInputPopup.EditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnCreateWidget extends Composite {

	private static KanbanColumnCreateWidgetUiBinder uiBinder = GWT.create(KanbanColumnCreateWidgetUiBinder.class);

	interface KanbanColumnCreateWidgetUiBinder extends UiBinder<Widget, KanbanColumnCreateWidget> {}

	private final ProgressPanelWidgetInteractionHandler interactionHandler;

	private final int insertionIndex;

	@UiField
	FocusPanel rootPanel;

	@UiField
	Label create;

	public KanbanColumnCreateWidget(final ProgressPanelWidgetInteractionHandler interactionHandler, final int insertionIndex) {
		this.interactionHandler = interactionHandler;
		this.insertionIndex = insertionIndex;
		initWidget(uiBinder.createAndBindUi(this));
		create.setTitle("Add new column");
	}

	@UiHandler("create")
	protected void onClick(final ClickEvent event) {
		PopupConfig.configPopup().popup(new TextInputPopup("new Description", "New Column", new EditionHandler() {
			@Override
			public boolean onEdition(final String text) {
				final String trimmedText = text.trim();
				if (trimmedText.isEmpty()) return false;
				interactionHandler.onKanbanColumnCreate(text, insertionIndex);
				return true;
			}
		})).alignBelow(create).alignRight(create).pop();
	}
}
