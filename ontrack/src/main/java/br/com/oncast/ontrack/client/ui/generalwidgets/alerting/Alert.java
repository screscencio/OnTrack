package br.com.oncast.ontrack.client.ui.generalwidgets.alerting;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;

import com.google.gwt.user.client.ui.IsWidget;

public interface Alert extends IsWidget {

	void hide(AnimationCallback animationCallback);

	void show(String message, AlertType type);

	void show(String message, AlertType type, AnimationCallback animationCallback);

	Alert setMessage(String message);

	Alert setType(AlertType type);

}
