package br.com.oncast.ontrack.client.ui.generalwidgets.animation;

import com.google.gwt.user.client.ui.Widget;

public interface AnimationFactory {

	ShowAnimation createShowAnimation(Widget widget);

	HideAnimation createHideAnimation(Widget widget);

}
