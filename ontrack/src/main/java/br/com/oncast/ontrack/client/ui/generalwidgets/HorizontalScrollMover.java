package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Element;

public class HorizontalScrollMover extends Animation {

	private static final int ANIMATION_DURATION = 1000;
	private static final int SCROLL_MOVE_STEP = 2;

	private int step;

	private final Element scroll;

	public HorizontalScrollMover(final Element element) {
		this.scroll = element;
	}

	public void moveLeft() {
		step = -SCROLL_MOVE_STEP;
		run();
	}

	public void moveRight() {
		step = SCROLL_MOVE_STEP;
		run();
	}

	private void run() {
		run(ANIMATION_DURATION);
	}

	@Override
	protected void onUpdate(final double progress) {
		scroll.setScrollLeft(scroll.getScrollLeft() + step);
	}

	@Override
	protected void onComplete() {
		run();
	}

	@Override
	protected void onCancel() {
		// IMPORTANT left empty to override default behavior of calling onComplete after onCancel
	}
}