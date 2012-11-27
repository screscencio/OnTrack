package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.user.client.ui.Widget;

public class SlideAndFadeAnimation implements ShowAnimation, HideAnimation {

	private final Widget widget;
	private static final int DEFAULT_SLIDE_DURATION = 400;
	private static final int DEFAULT_FADE_DURATION = 600;
	private final int hiddenOpacity = 0;
	private final double shownOpacity;

	public SlideAndFadeAnimation(final Widget widget) {
		this(widget, 1);
	}

	public SlideAndFadeAnimation(final Widget widget, final double shownOpacity) {
		this.widget = widget;
		this.shownOpacity = shownOpacity == 0 ? 1 : shownOpacity;
	}

	@Override
	public void show() {
		show(null);
	}

	@Override
	public void show(final AnimationCallback callback) {
		final JQuery $ = getWidgetJqueryElement();
		$.stop(true).fadeTo(0, hiddenOpacity, new AnimationCallback() {

			@Override
			public void onComplete() {
				$.hide().slideDown(DEFAULT_SLIDE_DURATION, new AnimationCallback() {

					@Override
					public void onComplete() {
						$.fadeTo(DEFAULT_FADE_DURATION, shownOpacity, callback);
					}
				});
			}
		});
	}

	@Override
	public void hide() {
		hide(null);
	}

	@Override
	public void hide(final AnimationCallback callback) {
		final JQuery $ = getWidgetJqueryElement();
		$.stop(true).fadeTo(DEFAULT_FADE_DURATION, hiddenOpacity, new AnimationCallback() {

			@Override
			public void onComplete() {
				$.slideUp(DEFAULT_SLIDE_DURATION, callback);
			}
		});
	}

	private JQuery getWidgetJqueryElement() {
		return JQuery.jquery(widget.getElement());
	}

}
