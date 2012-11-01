package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.user.client.ui.Widget;

public class SlideAndFadeAnimation implements ShowAnimation, HideAnimation {

	private final Widget widget;
	private static final int DEFAULT_SLIDE_DURATION = 4000;
	private static final int DEFAULT_FADE_DURATION = 5000;

	public SlideAndFadeAnimation(final Widget widget) {
		this.widget = widget;
	}

	@Override
	public void show() {
		show(null);
	}

	@Override
	public void show(final AnimationCallback callback) {
		getWidgetJqueryElement().clearQueue().hide().fadeTo(1, 0).slideDown(DEFAULT_SLIDE_DURATION).fadeTo(DEFAULT_FADE_DURATION, 1, callback);
	}

	@Override
	public void hide() {
		hide(null);
	}

	@Override
	public void hide(final AnimationCallback callback) {
		getWidgetJqueryElement().clearQueue().fadeTo(DEFAULT_FADE_DURATION, 0).slideUp(DEFAULT_SLIDE_DURATION, callback);
	}

	private JQuery getWidgetJqueryElement() {
		return JQuery.jquery(widget.getElement());
	}

}
