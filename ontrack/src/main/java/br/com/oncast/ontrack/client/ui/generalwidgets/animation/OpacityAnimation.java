package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public class OpacityAnimation extends Animation implements ShowAnimation, HideAnimation {

	private static final int DEFAULT_ANIMATION_DURATION = 1000;
	private final Widget widget;
	private final int duration;
	private AnimationCallback listener;

	private double opacity_start;
	private double opacity_end;

	public OpacityAnimation(final Widget widget) {
		this(widget, DEFAULT_ANIMATION_DURATION);
	}

	public OpacityAnimation(final Widget widget, final int duration) {
		this.widget = widget;
		this.duration = duration;
	}

	@Override
	public void show(final AnimationCallback listener) {
		this.listener = listener;
		show();
	}

	@Override
	public void hide(final AnimationCallback listener) {
		this.listener = listener;
		hide();
	}

	@Override
	public void show() {
		opacity_start = 0.;
		opacity_end = 1.;

		run(duration);
	}

	@Override
	public void hide() {
		opacity_start = 1.;
		opacity_end = 0.;

		run(duration);
	}

	@Override
	protected void onStart() {
		super.onStart();
		widget.getElement().getStyle().setOpacity(opacity_start);
	}

	@Override
	protected void onUpdate(final double progress) {
		final double percentage = progress;
		onOpacityUpdate(percentage);
	}

	private void onOpacityUpdate(final double progress) {
		final double value = opacity_start + ((opacity_end - opacity_start) * progress);
		widget.getElement().getStyle().setOpacity(value);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		widget.getElement().getStyle().setOpacity(opacity_end);
		if (listener != null) listener.onComplete();
	}
}
