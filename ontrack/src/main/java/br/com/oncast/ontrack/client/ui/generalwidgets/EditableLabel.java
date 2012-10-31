package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableLabel extends Composite implements HasValueChangeHandlers<String>, HasValue<String> {

	private static EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

	interface EditableLabelUiBinder extends UiBinder<Widget, EditableLabel> {}

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel deckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected Label visualizationLabel;

	@UiField
	@IgnoredByDeepEquality
	protected TextBox editionBox;

	@IgnoredByDeepEquality
	private final EditableLabelEditionHandler editionHandler;

	public EditableLabel(final EditableLabelEditionHandler editionHandler) {
		this(editionHandler, false);
	}

	public EditableLabel(final EditableLabelEditionHandler editionHandler, final boolean wrapText) {
		this.editionHandler = editionHandler;
		initWidget(uiBinder.createAndBindUi(this));
		deckPanel.showWidget(0);
		if (wrapText) visualizationLabel.removeStyleName("v-ellip");
	}

	@Override
	public String getValue() {
		return visualizationLabel.getText();
	}

	@Override
	public void setValue(final String value) {
		editionBox.setValue(value);
		visualizationLabel.setText(value);
	}

	@Override
	public void setValue(final String value, final boolean fireEvents) {
		if (fireEvents) ValueChangeEvent.fireIfNotEqual(this, getValue(), value);
		setValue(value);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@UiHandler("visualizationLabel")
	protected void onDoubleClick(final DoubleClickEvent event) {
		event.preventDefault();
		switchToEdit();
	}

	@UiHandler("editionBox")
	protected void onBlur(final BlurEvent event) {
		switchToVisualization(true);
	}

	@UiHandler("editionBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!isEditionMode()) return;

		event.stopPropagation();

		final boolean isEnter = event.getNativeKeyCode() == KEY_ENTER;
		if (isEnter || event.getNativeKeyCode() == KEY_ESCAPE) {
			event.preventDefault();
			switchToVisualization(isEnter);
		}
		else return;
	}

	private void switchToEdit() {
		if (isEditionMode()) return;

		editionBox.setText(getValue());
		deckPanel.showWidget(1);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				editionBox.setFocus(true);
			}
		});
	}

	private void switchToVisualization(final boolean shouldTryToUpdateChanges) {
		if (!isEditionMode()) return;
		deckPanel.showWidget(0);

		if (!shouldTryToUpdateChanges) editionBox.setText(visualizationLabel.getText());
		else if (!getValue().equals(editionBox.getText()) || editionBox.getText().isEmpty()) {
			if (editionHandler.onEditionRequest(editionBox.getText())) setValue(editionBox.getText(), true);
			else editionBox.setText(getValue());
		}
	}

	private boolean isEditionMode() {
		return deckPanel.getVisibleWidget() == 1;
	}
}
