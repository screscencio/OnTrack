package br.com.oncast.ontrack.client.ui.component.editableLabel.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableLabel extends Composite implements HasValue<String> {

	private static EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

	interface EditableLabelUiBinder extends UiBinder<Widget, EditableLabel> {}

	@UiField
	protected Label editLabel;

	@UiField
	protected DeckPanel deckPanel;

	@UiField
	protected TextBox editBox;

	@UiField
	protected FocusPanel focusPanel;

	private final EditionHandler editionHandler;

	public EditableLabel(final String text, final EditionHandler editionHandler) {

		this.editionHandler = editionHandler;
		initWidget(uiBinder.createAndBindUi(this));

		editLabel.setText(text);
		deckPanel.showWidget(0);

		focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				switchToEditionMode();
			}
		});
		editBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				switchToVisualization();
			}
		});
		editBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (!isEditing()) return;

				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					event.preventDefault();
					event.stopPropagation();
					switchToVisualization();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					event.preventDefault();
					event.stopPropagation();
					editBox.setText(editLabel.getText());
					switchToVisualization();
				}
			}
		});
	}

	public void switchToEditionMode() {
		if (isEditing()) return;
		editBox.setText(getValue());
		deckPanel.showWidget(1);
		new Timer() {

			@Override
			public void run() {
				editBox.setFocus(true);
			}
		}.schedule(300);
	}

	public void switchToVisualization() {
		if (!isEditing()) return;
		deckPanel.showWidget(0);
		if (!editLabel.getText().equals(editBox.getText())) editionHandler.onEdit(editBox.getText());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return editLabel.getText();
	}

	@Override
	public void setValue(final String value) {
		editLabel.setText(value);
		editBox.setText(value);
	}

	@Override
	public void setValue(final String value, final boolean fireEvents) {
		if (fireEvents) ValueChangeEvent.fireIfNotEqual(this, getValue(), value);
		setValue(value);
	}

	private boolean isEditing() {
		return deckPanel.getVisibleWidget() == 1;
	}
}
