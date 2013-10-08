package br.com.oncast.ontrack.client.ui.generalwidgets.alerting;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAnimation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class BasicAlert extends Composite implements Alert {

	private static final int ANIMATION_DURATION = 600;

	private static BasicAlertUiBinder uiBinder = GWT.create(BasicAlertUiBinder.class);

	interface BasicAlertUiBinder extends UiBinder<Widget, BasicAlert> {}

	@UiField
	HTMLPanel alertContainer;

	@UiField
	DivElement alertText;

	@UiField
	SpanElement alertIcon;

	@UiField
	DivElement alertDiv;

	public BasicAlert() {
		initWidget(uiBinder.createAndBindUi(this));
		hide(false, new AnimationCallback() {
			@Override
			public void onComplete() {}
		});
	}

	@Override
	public void hide(final AnimationCallback animationCallback) {
		hide(true, animationCallback);
	}

	private void hide(final boolean animate, final AnimationCallback animationCallback) {
		final SlideAnimation animation = new SlideAnimation(false, alertContainer, alertDiv, animationCallback);
		animation.run(animate ? ANIMATION_DURATION : 0);
	}

	@Override
	public void show(final String message, final AlertType type) {
		show(message, type, null);
	}

	@Override
	public void show(final String message, final AlertType type, final AnimationCallback animationCallback) {
		setMessage(message);
		setType(type);
		show(animationCallback);
	}

	private void show(final AnimationCallback animationCallback) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				new SlideAnimation(true, alertContainer, alertDiv, animationCallback).run(ANIMATION_DURATION);

			}
		});
	}

	@Override
	public Alert setMessage(final String message) {
		alertText.setInnerHTML(message);
		return this;
	}

	@Override
	public Alert setType(final AlertType type) {
		alertIcon.addClassName(type.getIconClass());
		alertIcon.getStyle().setColor(type.getColor().toHex());
		return this;
	}
}
