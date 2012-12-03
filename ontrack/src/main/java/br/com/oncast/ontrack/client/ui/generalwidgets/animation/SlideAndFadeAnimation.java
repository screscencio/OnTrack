package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.user.client.ui.Widget;

public class SlideAndFadeAnimation implements ShowAnimation, HideAnimation {

	private final Widget widget;
	private static final int DEFAULT_SLIDE_DURATION = 400;
	private static final int DEFAULT_FADE_DURATION = 600;
	private final int hiddenOpacity = 0;
	private final double shownOpacity = 1;
	private final boolean shouldResetElementOpacity;

	public SlideAndFadeAnimation(final Widget widget) {
		this(widget, false);
	}

	public SlideAndFadeAnimation(final Widget widget, final boolean shouldResetElementOpacity) {
		this.widget = widget;
		this.shouldResetElementOpacity = shouldResetElementOpacity;
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
				$.slideUp(DEFAULT_SLIDE_DURATION, new AnimationCallback() {

					@Override
					public void onComplete() {
						if (shouldResetElementOpacity) widget.getElement().getStyle().clearOpacity();
						if (callback != null) callback.onComplete();
					}
				});
			}
		});
	}

	private JQuery getWidgetJqueryElement() {
		return JQuery.jquery(widget.getElement());
	}

}
