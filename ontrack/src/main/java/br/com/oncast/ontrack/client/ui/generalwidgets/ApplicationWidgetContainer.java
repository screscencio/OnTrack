package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationWidgetContainer extends Composite implements HasWidgets {

	private static ApplicationWidgetContainerUiBinder uiBinder = GWT.create(ApplicationWidgetContainerUiBinder.class);

	interface ApplicationWidgetContainerUiBinder extends UiBinder<Widget, ApplicationWidgetContainer> {}

	@UiField
	protected Label messageLabel;

	@UiField
	protected FlowPanel widgetContainer;

	public ApplicationWidgetContainer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ApplicationWidgetContainer(final String text, final Widget widget) {
		initWidget(uiBinder.createAndBindUi(this));
		setText(text);
	}

	public String getText() {
		return messageLabel.getText();
	}

	public void setText(final String text) {
		messageLabel.setText(text);
	}

	@Override
	public void add(final Widget w) {
		widgetContainer.add(w);
	}

	@Override
	public void clear() {
		widgetContainer.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return widgetContainer.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return widgetContainer.remove(w);
	}
}
