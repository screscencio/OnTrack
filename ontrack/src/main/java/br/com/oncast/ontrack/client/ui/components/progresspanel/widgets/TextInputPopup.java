package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextInputPopup extends Composite implements HasCloseHandlers<TextInputPopup>, PopupAware {

	private static TextInputPopupUiBinder uiBinder = GWT.create(TextInputPopupUiBinder.class);

	interface TextInputPopupUiBinder extends UiBinder<Widget, TextInputPopup> {}

	@UiField
	protected FocusPanel rootPanel;

	@UiField
	protected TextBox inputTextBox;

	@UiField
	protected Label descriptionLabel;

	private EditionHandler editionHandler;

	public TextInputPopup() {}

	public TextInputPopup(final String inputDescription, final String defaultText, final EditionHandler editionHandler) {
		initWidget(uiBinder.createAndBindUi(this));

		descriptionLabel.setText(inputDescription);
		inputTextBox.setText(defaultText);
		this.editionHandler = editionHandler;
	}

	@UiHandler("inputTextBox")
	protected void onKeyUp(final KeyUpEvent event) {
		final int keyCode = event.getNativeKeyCode();
		if (keyCode == BrowserKeyCodes.KEY_ESCAPE) hide();
		if (keyCode == BrowserKeyCodes.KEY_ENTER) onSubmit(null);
	}

	@UiHandler("submitButton")
	protected void onSubmit(final ClickEvent event) {
		editionHandler.onEdition(inputTextBox.getText());
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<TextInputPopup> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		this.setVisible(true);
		inputTextBox.setFocus(true);
		inputTextBox.selectAll();
		alignCenter();
	}

	@Override
	public void hide() {
		this.setVisible(false);
	}

	private void alignCenter() {
		// final int width = (Window.getClientWidth() - getOffsetWidth()) / 2;
		// final int height = (Window.getClientHeight() - getOffsetHeight()) / 2;
		//
		// getElement().getStyle().setLeft(width, Unit.PX);
		// getElement().getStyle().setTop(25, Unit.PX);
	}

	public interface EditionHandler {
		boolean onEdition(String text);
	}
}
