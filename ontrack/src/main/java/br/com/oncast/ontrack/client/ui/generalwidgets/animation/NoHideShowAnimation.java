package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.user.client.ui.Widget;

public class NoHideShowAnimation implements ShowAnimation, HideAnimation {

	private final Widget widget;

	public NoHideShowAnimation(final Widget widget) {
		this.widget = widget;
	}

	@Override
	public void hide() {
		getWidgetJqueryElement().stop(true).hide();
	}

	@Override
	public void hide(final AnimationCallback listener) {
		hide();
		listener.onComplete();
	}

	@Override
	public void show() {
		getWidgetJqueryElement().stop(true).show();
	}

	@Override
	public void show(final AnimationCallback listener) {
		show();
		listener.onComplete();
	}

	private JQuery getWidgetJqueryElement() {
		return JQuery.jquery(widget.getElement());
	}
}
