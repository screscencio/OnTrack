package br.com.oncast.ontrack.client.ui.generalwidgets.notification;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class NotificationContainer extends Composite implements HasWidgets {

	private static NotificationContainerUiBinder uiBinder = GWT.create(NotificationContainerUiBinder.class);

	interface NotificationContainerUiBinder extends UiBinder<Widget, NotificationContainer> {}

	@UiField
	FlowPanel rootPanel;

	public NotificationContainer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void add(final Widget w) {
		rootPanel.add(w);
	}

	@Override
	public void clear() {
		rootPanel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return rootPanel.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return rootPanel.remove(w);
	}
}
