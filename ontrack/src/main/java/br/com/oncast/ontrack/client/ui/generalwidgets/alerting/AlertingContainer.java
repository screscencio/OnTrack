package br.com.oncast.ontrack.client.ui.generalwidgets.alerting;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class AlertingContainer extends Composite implements HasWidgets {

	private static AlertingContainerUiBinder uiBinder = GWT.create(AlertingContainerUiBinder.class);

	interface AlertingContainerUiBinder extends UiBinder<Widget, AlertingContainer> {}

	@UiField
	FlowPanel rootPanel;

	public AlertingContainer() {
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
