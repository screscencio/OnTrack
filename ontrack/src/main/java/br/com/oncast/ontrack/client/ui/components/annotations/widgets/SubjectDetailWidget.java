package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import com.google.gwt.user.client.ui.IsWidget;

public interface SubjectDetailWidget<T> extends IsWidget {

	void setSubject(T scope);

	void setStyleName(String name);

}
