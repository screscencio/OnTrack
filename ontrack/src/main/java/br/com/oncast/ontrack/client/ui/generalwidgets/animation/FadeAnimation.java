package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.user.client.ui.Widget;

public class FadeAnimation extends ValueTransitionAnimation {

	private static final int DURATION = 700;
	private static final int HIDDEN = 0;
	private static final int VISIBLE = 1;

	private final AnimationCompletedListener listener;

	public FadeAnimation(final Widget widget, final AnimationCompletedListener listener) {
		super(widget, DURATION);
		this.listener = listener == null ? new AnimationCompletedListener() {
			@Override
			public void onCompleted(final boolean isHidden) {}
		} : listener;
	}

	@Override
	protected void setValue(final double value) {
		this.widget.getElement().getStyle().setOpacity(value);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		listener.onCompleted(getTo() == HIDDEN);
	};

	public void hide() {
		this.animate(VISIBLE, HIDDEN);
	}

	public void show() {
		this.animate(HIDDEN, VISIBLE);
	}

	public interface AnimationCompletedListener {
		void onCompleted(boolean isHidden);
	}

}
