package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Panel;

public class SlideAnimation extends Animation {

	private static final int BOTTOM_ELEMENT_SPACCING = 20;

	private final Panel container;
	private final Element element;
	private final int initialHeight;
	private final int desiredHeight;
	private final AnimationCallback callback;

	public SlideAnimation(final boolean show, final Panel container, final Element element, final AnimationCallback callback) {
		this.container = container;
		this.element = element;
		this.callback = callback;

		final int containerHeight = element.getOffsetHeight() + BOTTOM_ELEMENT_SPACCING;
		initialHeight = show ? 0 : containerHeight;
		desiredHeight = show ? containerHeight : 0;
		setUpElement();
		setUpContainer(initialHeight);
	}

	private void setUpContainer(final int height) {
		final Style containerStyle = container.getElement().getStyle();
		final String position = containerStyle.getPosition();
		if (position.equals(Style.Position.RELATIVE.getCssName()) || position.equals(Style.Position.ABSOLUTE.getCssName())) return;
		containerStyle.setPosition(Style.Position.RELATIVE);
		containerStyle.setHeight(height, Unit.PX);
		containerStyle.setOverflow(Overflow.HIDDEN);
	}

	private void setUpElement() {
		final Style elementStyle = element.getStyle();
		elementStyle.setPosition(Style.Position.ABSOLUTE);
	}

	@Override
	protected void onUpdate(final double progress) {
		final Style containerStyle = container.getElement().getStyle();
		final Style elementStyle = element.getStyle();

		final int heightDiff = desiredHeight - initialHeight;
		final double height = initialHeight + (progress * heightDiff);
		containerStyle.setHeight(height, Unit.PX);

		final double bottom = (heightDiff < 0 ? 1 - progress : progress) * BOTTOM_ELEMENT_SPACCING;
		containerStyle.setMarginBottom(-1 * bottom, Unit.PX);
		elementStyle.setBottom(bottom, Unit.PX);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		if (callback != null) callback.onComplete();
	}
}
