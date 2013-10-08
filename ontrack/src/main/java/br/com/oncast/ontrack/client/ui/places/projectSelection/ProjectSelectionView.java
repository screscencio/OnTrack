package br.com.oncast.ontrack.client.ui.places.projectSelection;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public interface ProjectSelectionView {

	Widget asWidget();

	void focus();

	Panel getAlertingContainer();

}
