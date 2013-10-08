package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

public interface ProjectMessageView extends IsWidget {

	void setMainMessage(String message);

	Panel getAlertingContainer();

}
