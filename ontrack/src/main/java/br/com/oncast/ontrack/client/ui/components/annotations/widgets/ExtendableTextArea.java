package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedTextBox;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;

public class ExtendableTextArea extends Composite implements HasText, HasKeyDownHandlers {

	private static ExtendableTextAreaUiBinder uiBinder = GWT.create(ExtendableTextAreaUiBinder.class);

	interface ExtendableTextAreaUiBinder extends UiBinder<Widget, ExtendableTextArea> {}

	@UiField
	FocusPanel focusPanel;

	@UiField(provided = true)
	RichTextArea textArea;

	@UiField(provided = true)
	RichTextToolbar toolbar;

	@UiField
	DeckPanel deckPanel;

	@UiField
	PaddedTextBox paddedTextBox;

	@UiField
	FocusPanel richTextArea;

	private final NativeEventListener clickListener;

	private ExpansionListener expansionListener;

	public ExtendableTextArea() {
		textArea = new RichTextArea();
		textArea.ensureDebugId("cwRichText-area");
		toolbar = new RichTextToolbar(textArea);
		toolbar.ensureDebugId("cwRichText-toolbar");
		toolbar.setWidth("100%");

		textArea.setFocus(false);

		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.setAnimationEnabled(true);

		shrink();

		clickListener = new NativeEventListener() {

			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				if (isInsideFocusPanel(Element.as(nativeEvent.getEventTarget()))) return;

				hideRichTextArea();
			}

			private boolean isInsideFocusPanel(final Element element) {
				Element parent = element;

				while ((parent = parent.getParentElement()) != null) {
					if (parent == focusPanel.getElement()) return true;
				}

				return false;

			}
		};
	}

	public HandlerRegistration registerExpansionListener(final ExpansionListener listener) {
		this.expansionListener = listener;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				expansionListener = null;
			}
		};
	}

	@UiHandler("paddedTextBox")
	public void onPaddedTextBoxFocus(final FocusEvent event) {
		expand();
	}

	private void expand() {
		deckPanel.showWidget(0);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				textArea.setFocus(true);
				if (expansionListener != null) expansionListener.onExpandded();
			}
		});
	}

	@UiHandler("textArea")
	public void onTextAreaKeyPress(final KeyPressEvent event) {
		expand();
	}

	@UiHandler("richTextArea")
	public void onRichTextAreaMouseOut(final MouseOutEvent event) {
		GlobalNativeEventService.getInstance().addMouseUpListener(clickListener);
	}

	@UiHandler("richTextArea")
	public void onRichTextAreaMouseOver(final MouseOverEvent event) {
		GlobalNativeEventService.getInstance().removeMouseUpListener(clickListener);
	}

	@UiHandler("textArea")
	public void onTextAreaKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) {
			hideRichTextArea();
			focusPanel.setFocus(true);
		}
	}

	@Override
	public void setText(final String text) {
		textArea.setHTML(text);
	}

	@Override
	public String getText() {
		return textArea.getHTML();
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return textArea.addKeyDownHandler(handler);
	}

	public void setFocus(final boolean b) {
		textArea.setFocus(true);
	}

	public void hideRichTextArea() {
		shrink();
		GlobalNativeEventService.getInstance().removeMouseUpListener(clickListener);
	}

	private void shrink() {
		deckPanel.showWidget(1);
		if (expansionListener != null) expansionListener.onShrinked();
	}

	public interface ExpansionListener {
		void onExpandded();

		void onShrinked();
	}

}
