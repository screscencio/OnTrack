package br.com.oncast.ontrack.client.ui.generalwidgets.notification;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAnimation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Notification extends Composite {

	private static final int ANIMATION_DURATION = 600;

	private static NotificationUiBinder uiBinder = GWT.create(NotificationUiBinder.class);

	interface NotificationUiBinder extends UiBinder<Widget, Notification> {}

	@UiField
	HTMLPanel notificationContainer;

	@UiField
	SpanElement notificationText;

	@UiField
	DivElement notificationDiv;

	public Notification() {
		initWidget(uiBinder.createAndBindUi(this));
		hide(false, new AnimationCallback() {

			@Override
			public void onComplete() {}
		});
	}

	public void hide(final AnimationCallback animationCallback) {
		hide(true, animationCallback);
	}

	protected void hide(final boolean animate, final AnimationCallback animationCallback) {
		final SlideAnimation animation = new SlideAnimation(false, notificationContainer, notificationDiv, animationCallback);
		animation.run(animate ? ANIMATION_DURATION : 0);
	}

	public void show(final String message, final NotificationType type) {
		show(message, type, null);
	}

	public void show(final String message, final NotificationType type, final AnimationCallback animationCallback) {
		setMessage(message);
		setBackground(type);
		new Timer() {

			@Override
			public void run() {
				final SlideAnimation animation = new SlideAnimation(true, notificationContainer, notificationDiv, animationCallback);
				animation.run(ANIMATION_DURATION);
			}
		}.schedule(1);
	}

	private void setMessage(final String message) {
		notificationText.setInnerText(message);
	}

	private void setBackground(final NotificationType type) {
		getElement().getFirstChildElement().getStyle().setBackgroundImage("url('" + type.getIconSafeUri().asString() + "')");
	}
}
