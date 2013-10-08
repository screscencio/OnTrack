package br.com.oncast.ontrack.client.ui.generalwidgets.annotation;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class QuickAnnotationWidget extends Composite implements PopupAware, HasCloseHandlers<QuickAnnotationWidget> {

	private static final QuickAnnotationWidgetMessages MESSAGES = GWT.create(QuickAnnotationWidgetMessages.class);

	private static QuickAnnotationWidgetUiBinder uiBinder = GWT.create(QuickAnnotationWidgetUiBinder.class);

	interface QuickAnnotationWidgetUiBinder extends UiBinder<Widget, QuickAnnotationWidget> {}

	@UiField
	TextArea text;

	@UiField
	Button button;

	private final UUID subjectId;

	public QuickAnnotationWidget(final UUID subjectId) {
		this.subjectId = subjectId;
		initWidget(uiBinder.createAndBindUi(this));
		text.getElement().setAttribute("placeholder", MESSAGES.annotationTextPlaceholder());
	}

	@UiHandler("text")
	void onTextKeyDown(final KeyDownEvent e) {
		e.stopPropagation();
		if (new Shortcut(BrowserKeyCodes.KEY_ENTER).with(ControlModifier.PRESSED).accepts(e.getNativeEvent())) createAnnotation();
	}

	@UiHandler("button")
	void onClick(final ClickEvent e) {
		createAnnotation();
	}

	private void createAnnotation() {
		final String message = text.getText();
		if (message.trim().isEmpty()) return;

		ClientServices.get().details().createAnnotationFor(subjectId, message, null);
		hide();
	}

	@Override
	public void show() {
		text.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<QuickAnnotationWidget> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

}
