package br.com.oncast.ontrack.client.ui.components.footerbar;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.feedback.SendFeedbackCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FeedbackPopup extends Composite implements HasCloseHandlers<FeedbackPopup>, PopupAware {

	private static final String NEW_LINE = "\n";

	private static FeedbackPopupUiBinder uiBinder = GWT.create(FeedbackPopupUiBinder.class);

	private static FeedbackPopupMessages messages = GWT.create(FeedbackPopupMessages.class);

	interface FeedbackPopupUiBinder extends UiBinder<Widget, FeedbackPopup> {}

	@UiField
	TextArea feedbackArea;

	public FeedbackPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("feedbackArea")
	void onKeyDown(final KeyDownEvent event) {
		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hide();

		else if (new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(event.getNativeEvent())) submit();

		event.stopPropagation();
	}

	private void submit() {
		final String feedbackMessage = feedbackArea.getText();
		if (feedbackMessage.trim().isEmpty()) {
			feedbackArea.setFocus(true);
			return;
		}

		hide();
		ClientServices.get().feedback().sendFeedback(feedbackMessage.replaceAll(NEW_LINE, "<br/>"), new SendFeedbackCallback() {
			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				ClientServices.get().alerting().showError(messages.feedbackNotSent());
			}

			@Override
			public void onFeedbackSentSucessfully() {
				ClientServices.get().alerting().showSuccess(messages.feedbackSent());
			}
		});
		ClientServices.get().alerting().showInfo(messages.processingYourFeedback());
	}

	@Override
	public void show() {
		adjustDimensions();
		feedbackArea.setFocus(true);
	}

	// TODO++ remove this workaround
	private void adjustDimensions() {
		final Style s = this.getElement().getStyle();
		s.setTop(Window.getClientHeight() - 206, Unit.PX);
		s.setRight(440, Unit.PX);
	}

	@Override
	public void hide() {
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<FeedbackPopup> handler) {
		return super.addHandler(handler, CloseEvent.getType());
	}

}
