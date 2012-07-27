package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ValueTransitionAnimation;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ExtendableTextArea extends Composite implements HasText, HasKeyDownHandlers {

	private static final double VISIBLE_OPACITY = 0.6;

	private static final double INVISIBLE_OPACITY = 0.0;

	private static final int SUBMIT_HELP_TEXT_FADE_DELAY = 2000;

	private static final int SUBMIT_HELP_TEXT_ANIMATION_DURATION = 500;

	private static final int TEXT_LENGHT_TO_HIDE_SUBMIT_HELP = 50;

	private static final int HEIGHT_ANIMATION_DURATION = 200;

	private static ExtendableTextAreaUiBinder uiBinder = GWT.create(ExtendableTextAreaUiBinder.class);

	interface ExtendableTextAreaUiBinder extends UiBinder<Widget, ExtendableTextArea> {}

	@UiField
	TextArea textArea;

	@UiField
	FocusPanel focusPanel;

	@UiField
	Label helpText;

	@UiField
	Label submitHelpText;

	private float maxHeight;
	private float defaultHeight;

	private HeightAnimation heightAnimation;
	private ValueTransitionAnimation fadeAnimation;

	private boolean isHelptTextVisible = true;

	public ExtendableTextArea() {
		initWidget(uiBinder.createAndBindUi(this));
		setSubmitHelpTextVisible(false);
	}

	public ExtendableTextArea(final int maxHeight) {
		this();
		setMaxHeight(maxHeight);
	}

	public void setHelpText(final String text) {
		helpText.setText(text);
	}

	public void setDefaultHeight(final int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	public void setMaxHeight(final int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxHeight(final String maxHeight) {
		this.maxHeight = convertToNumber(maxHeight);
	}

	public void setDefaultHeight(final String defaultHeight) {
		this.defaultHeight = convertToNumber(defaultHeight);
	}

	@UiHandler("helpText")
	protected void onHelpTextFocus(final ClickEvent e) {
		textArea.setFocus(true);
	}

	@UiHandler("submitHelpText")
	protected void onSubmitHelpTextFocus(final ClickEvent e) {
		textArea.setFocus(true);
	}

	@UiHandler("textArea")
	protected void onFocus(final FocusEvent event) {
		if (!textArea.getText().trim().isEmpty()) stretch();
	}

	@UiHandler("textArea")
	public void onKeyUp(final KeyUpEvent event) {
		final String text = textArea.getText();
		final boolean isEmpty = text.trim().isEmpty();

		if (isHelptTextVisible != isEmpty) {
			isHelptTextVisible = !isHelptTextVisible;
			helpText.setVisible(isHelptTextVisible);
			if (isEmpty) shrink();
			else stretch();
		}
		final boolean isSubmitHelpTextVisible = !isEmpty && text.length() < TEXT_LENGHT_TO_HIDE_SUBMIT_HELP;
		setSubmitHelpTextVisible(isSubmitHelpTextVisible);

		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER && !isSubmitHelpTextVisible) {
			setSubmitHelpTextVisible(true);
			new Timer() {
				@Override
				public void run() {
					setSubmitHelpTextVisible(false);
				}
			}.schedule(SUBMIT_HELP_TEXT_FADE_DELAY);
		}
	}

	@UiHandler("textArea")
	protected void onBlur(final BlurEvent event) {
		shrink();
	}

	@Override
	public void setText(final String text) {
		textArea.setText(text);
	}

	@Override
	public String getText() {
		return textArea.getText();
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return textArea.addKeyDownHandler(handler);
	}

	public void setFocus(final boolean b) {
		textArea.setFocus(true);
	}

	private void stretch() {
		animateHeight(getCurrentHeight(), maxHeight);
	}

	private void shrink() {
		if (defaultHeight != 0) animateHeight(getCurrentHeight(), defaultHeight);
	}

	private float getCurrentHeight() {
		return focusPanel.getOffsetHeight();
	}

	private void animateHeight(final float fromHeight, final float toHeight) {
		getHeightAnimation().animate(fromHeight, toHeight);
	}

	private HeightAnimation getHeightAnimation() {
		return heightAnimation == null ? heightAnimation = new HeightAnimation(focusPanel, HEIGHT_ANIMATION_DURATION) : heightAnimation;
	}

	private Float convertToNumber(final String maxHeight) {
		return Float.valueOf(maxHeight.replaceAll("[^0-9]+", ""));
	}

	private void setSubmitHelpTextVisible(final boolean visible) {
		if (visible) getFadeAnimation().animate(INVISIBLE_OPACITY, VISIBLE_OPACITY);
		else getFadeAnimation().animate(VISIBLE_OPACITY, INVISIBLE_OPACITY);
	}

	private ValueTransitionAnimation getFadeAnimation() {
		if (fadeAnimation == null) fadeAnimation = new OpacityAnimation(submitHelpText, SUBMIT_HELP_TEXT_ANIMATION_DURATION);
		return fadeAnimation;
	}

	private class OpacityAnimation extends ValueTransitionAnimation {

		public OpacityAnimation(final Widget widget, final int duration) {
			super(widget, duration);
		}

		@Override
		protected void setValue(final double value) {
			this.widget.getElement().getStyle().setOpacity(value);
		}

	}

	private class HeightAnimation extends ValueTransitionAnimation {
		public HeightAnimation(final Widget widget, final int duration) {
			super(widget, duration);
		}

		@Override
		protected void setValue(final double value) {
			widget.getElement().getStyle().setHeight(value, Unit.PX);
		}
	}

}
