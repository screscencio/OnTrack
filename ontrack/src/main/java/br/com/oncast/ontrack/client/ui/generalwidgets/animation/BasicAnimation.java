package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public abstract class BasicAnimation extends Animation {

	protected final Widget widget;
	private final int duration;
	private double from = -1.0;
	private double to = -1.0;

	public BasicAnimation(final Widget widget, final int duration) {
		this.widget = widget;
		this.duration = duration;
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

	public void animate(final double from, final double to) {
		if (this.to == to) return;
		this.from = from;
		this.to = to;

		run(duration);
	}

	protected abstract void setValue(final double value);

}
