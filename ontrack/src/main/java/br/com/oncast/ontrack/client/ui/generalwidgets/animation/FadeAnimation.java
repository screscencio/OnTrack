package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.user.client.ui.Widget;

public class FadeAnimation extends ValueTransitionAnimation implements ShowAnimation, HideAnimation {

	private static final int DURATION = 700;
	private static final int HIDDEN = 0;
	private static final int VISIBLE = 1;

	private AnimationCallback listener;

	public FadeAnimation(final Widget widget) {
		super(widget, DURATION);
	}

	@Override
	protected void setValue(final double value) {
		this.widget.getElement().getStyle().setOpacity(value);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		if (listener != null) listener.onComplete();
	};

	@Override
	public void hide() {
		this.animate(VISIBLE, HIDDEN);
	}

	@Override
	public void show() {
		this.animate(HIDDEN, VISIBLE);
	}

	@Override
	public void hide(final AnimationCallback listener) {
		this.listener = listener;
		hide();
	}

	@Override
	public void show(final AnimationCallback listener) {
		this.listener = listener;
		show();
	}

}
