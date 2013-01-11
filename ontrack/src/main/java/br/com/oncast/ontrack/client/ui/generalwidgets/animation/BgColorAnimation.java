package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import br.com.oncast.ontrack.shared.model.color.Color;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public class BgColorAnimation extends Animation {

	private static final double COLOR_FADE_DELIMITER = 0.2;
	private static final int DEFAULT_DURATION = 1000;
	protected final Widget widget;
	private final int duration;
	private final Color color;
	private String initialBackgroundColor;

	public BgColorAnimation(final Widget widget, final Color color, final int duration) {
		this.widget = widget;
		this.duration = duration;
		this.color = color;
	}

	public BgColorAnimation(final Widget widget, final Color color) {
		this(widget, color, DEFAULT_DURATION);
	}

	public void animate() {
		animate(this.duration);
	}

	public void animate(final int duration) {
		run(duration);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initialBackgroundColor = widget.getElement().getStyle().getBackgroundColor();
	}

	@Override
	protected void onUpdate(final double progress) {
		final double percentage = progress;
		if (percentage < COLOR_FADE_DELIMITER) colorFadeIn(percentage / COLOR_FADE_DELIMITER);
		else colorFadeOut(percentage - COLOR_FADE_DELIMITER / (1.0 - COLOR_FADE_DELIMITER));
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		widget.getElement().getStyle().setBackgroundColor(initialBackgroundColor);
	}

	private void colorFadeIn(final double progress) {
		final Color currColor = color.copy();
		currColor.setAlpha(color.getAlpha() * progress);
		widget.getElement().getStyle().setBackgroundColor(currColor.toCssRepresentation());
	}

	private void colorFadeOut(final double progress) {
		final Color currColor = color.copy();
		currColor.setAlpha(color.getAlpha() * (1.0 - progress));
		widget.getElement().getStyle().setBackgroundColor(currColor.toCssRepresentation());
	}
}
