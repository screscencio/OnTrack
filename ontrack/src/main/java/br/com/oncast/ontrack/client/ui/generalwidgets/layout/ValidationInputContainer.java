package br.com.oncast.ontrack.client.ui.generalwidgets.layout;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;

public class ValidationInputContainer extends Composite implements HasWidgets, HasText, HasKeyDownHandlers, HasKeyUpHandlers {

	private static ValidationInputContainerUiBinder uiBinder = GWT.create(ValidationInputContainerUiBinder.class);

	interface ValidationInputContainerUiBinder extends UiBinder<Widget, ValidationInputContainer> {}

	interface ValidationInputContainerStyle extends CssResource {

		String textBox();

		String error();

	}

	public ValidationInputContainer() {
		initWidget(uiBinder.createAndBindUi(this));
		textBox = new NullTextBox();
		handler = new NullValidationHandler();
	}

	@UiField
	HTMLPanel container;

	@UiField
	Label defaultText;

	public ValidationInputContainer(final HasText inputWidget) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	FocusPanel focusPanel;

	@UiField
	ValidationInputContainerStyle style;

	private TextBox textBox;
	private ValidationHandler handler;

	public void update(final boolean isValid) {
		container.setStyleName(style.error(), !isValid);
	}

	public void setDefaultText(final String text) {
		defaultText.setText(text);
	}

	public void setDefaultTextAlign(final String align) {
		defaultText.getElement().getStyle().setProperty("textAlign", align);
	}

	@UiHandler("focusPanel")
	public void onFocus(final FocusEvent e) {
		textBox.setFocus(true);
	}

	@Override
	public void add(final Widget w) {
		if (!(textBox instanceof NullTextBox)) throw new RuntimeException(
				"This Widget only accepts one widget, please remove the current before adding another.");
		if (!(w instanceof TextBox)) throw new RuntimeException("ValidationInputContainer accepts TextBox or PasswordTextBox only");

		textBox = (TextBox) w;
		textBox.addStyleName(style.textBox());

		textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				updateDefaultText();
				if (event.getNativeKeyCode() == KEY_ENTER && validate())
				handler.onSubmit();
			}
		});

		textBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				validate();
			}
		});

		container.add(textBox);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateDefaultText();
			}
		});
	}

	private void updateDefaultText() {
		defaultText.setVisible(textBox.getText().isEmpty());
	}

	@Override
	public void clear() {
		container.clear();
		textBox = new NullTextBox();
	}

	@Override
	public Iterator<Widget> iterator() {
		return container.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		final boolean removed = container.remove(w);
		if (removed) textBox = new NullTextBox();
		return removed;
	}

	@Override
	public void setText(final String text) {
		textBox.setText(text);
		validate();
	}

	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return textBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
		return textBox.addKeyUpHandler(handler);
	}

	public void setCursorPos(final int pos) {
		textBox.setCursorPos(pos);
	}

	public void setFocus(final boolean focused) {
		textBox.setFocus(focused);
	}

	public void selectAll() {
		textBox.selectAll();
	}

	public void setEnabled(final boolean enabled) {
		textBox.setEnabled(enabled);
	}

	private boolean validate() {
		final boolean isValid = handler.isValid(textBox.getText());
		update(isValid);
		return isValid;
	}

	public void setHandler(final ValidationHandler handler) {
		this.handler = handler;
	}

	public interface ValidationHandler {

		boolean isValid(String value);

		void onSubmit();

	}

	private class NullValidationHandler implements ValidationHandler {

		@Override
		public boolean isValid(final String value) {
			throwException();
			return false;
		}

		@Override
		public void onSubmit() {
			throwException();
		}

		private void throwException() {
			throw new IllegalStateException("Validation Handler not set yet. Did you forgot to call setHandler(ValidationHandler)?");
		}

	}

	private class NullTextBox extends TextBox {

		@Override
		public void setEnabled(final boolean enabled) {
			throwException();
		}

		@Override
		public void selectAll() {
			throwException();
		}

		@Override
		public void setFocus(final boolean focused) {
			throwException();
		}

		@Override
		public void setCursorPos(final int pos) {
			throwException();
		}

		@Override
		public String getText() {
			throwException();
			return null;
		}

		@Override
		public void setText(final String text) {
			throwException();
		}

		@Override
		public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
			throwException();
			return null;
		}

		@Override
		public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
			throwException();
			return null;
		}

		private void throwException() {
			throw new IllegalStateException("You should add a TextBox subclass instance before any interactions");
		}
	}

}
