package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ExtendableTextArea extends Composite implements HasText, HasKeyDownHandlers {

	private static final int ANIMATION_DURATION = 250;

	private static ExtendableTextAreaUiBinder uiBinder = GWT.create(ExtendableTextAreaUiBinder.class);

	interface ExtendableTextAreaUiBinder extends UiBinder<Widget, ExtendableTextArea> {}

	@UiField
	TextArea textArea;

	@UiField
	FocusPanel focusPanel;

	private float maxHeight;
	private float defaultHeight;

	private HeightAnimation heightAnimation;

	public ExtendableTextArea() {
		initWidget(uiBinder.createAndBindUi(this));

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ExtendableTextArea.this.defaultHeight = getCurrentHeight();
			}
		});
	}

	public ExtendableTextArea(final int maxHeight) {
		this();
		setMaxHeight(maxHeight);
	}

	public void setMaxHeight(final int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxHeight(final String maxHeight) {
		this.maxHeight = Float.valueOf(maxHeight.replaceAll("[^0-9]+", ""));
	}

	@UiHandler("textArea")
	protected void onFocus(final FocusEvent event) {
		if (!textArea.getText().trim().isEmpty()) stretch();
	}

	@UiHandler("textArea")
	public void onKeyUp(final KeyUpEvent event) {
		if (textArea.getText().trim().isEmpty()) shrink();
		else stretch();
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
		animateHeight(defaultHeight, maxHeight);
	}

	private void shrink() {
		animateHeight(maxHeight, defaultHeight);
	}

	private void animateHeight(final float fromHeight, final float toHeight) {
		getHeightAnimation().grow(fromHeight, toHeight);
	}

	private HeightAnimation getHeightAnimation() {
		return heightAnimation == null ? heightAnimation = new HeightAnimation(focusPanel, ANIMATION_DURATION) : heightAnimation;
	}

	private float getCurrentHeight() {
		return focusPanel.getOffsetHeight();
	}

	private class HeightAnimation extends Animation {

		private final Widget widget;
		private final int duration;

		private float startHeight = -1;
		private float endHeight = -1;

		public HeightAnimation(final Widget widget, final int duration) {
			this.widget = widget;
			this.duration = duration;
		}

		@Override
		protected void onUpdate(final double progress) {
			setHeight(startHeight + (endHeight - startHeight) * progress);
		}

		@Override
		protected void onStart() {
			super.onStart();
			setHeight(startHeight);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			setHeight(endHeight);
		}

		protected void setHeight(final double height) {
			widget.getElement().getStyle().setHeight(height, Unit.PX);
		}

		public void grow(final float startHeight, final float endHeight) {
			if (startHeight == endHeight || endHeight == this.endHeight) return;

			this.startHeight = startHeight;
			this.endHeight = endHeight;

			this.run(duration);
		}
	}

}
