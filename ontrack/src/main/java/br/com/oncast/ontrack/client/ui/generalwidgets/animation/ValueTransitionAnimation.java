package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValueTransitionAnimation extends Animation {

	private static final int DEFAULT_DURATION = 500;
	protected final Widget widget;
	private final int duration;
	private double from = -1.0;
	private double to = -1.0;

	public ValueTransitionAnimation(final Widget widget, final int duration) {
		this.widget = widget;
		this.duration = duration;
	}

	public ValueTransitionAnimation(final Widget widget) {
		this.widget = widget;
		this.duration = DEFAULT_DURATION;
	}

	@Override
	protected void onStart() {
		super.onStart();
		setValue(from);
	}

	@Override
	protected void onUpdate(final double progress) {
		setValue(from + ((to - from) * interpolate(progress)));
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		setValue(to);
	}

	protected double getTo() {
		return to;
	}

	public void animate(final double from, final double to) {
		animate(from, to, this.duration);
	}

	public void animate(final double from, final double to, final int duration) {
		if (this.to == to) return;
		this.from = from;
		this.to = to;

		run(duration);
	}

	protected abstract void setValue(final double value);

}
