package br.com.oncast.ontrack.client.ui.generalwidgets.layout;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationPopupBoxContainer extends Composite implements HasWidgets, HasClickHandlers, HasKeyUpHandlers, HasMouseUpHandlers {

	private static ApplicationPopupBoxContainerUiBinder uiBinder = GWT.create(ApplicationPopupBoxContainerUiBinder.class);

	interface ApplicationPopupBoxContainerUiBinder extends UiBinder<Widget, ApplicationPopupBoxContainer> {}

	protected interface Style extends CssResource {
		String boxPadding();
	}

	@UiField
	Style style;

	@UiField
	FocusPanel rootPanel;

	@UiField
	HTMLPanel content;

	@UiField
	SimplePanel background;

	protected ApplicationPopupBoxContainer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	// IMPORTANT needed to put setAdd... in method name to use it correctly on ui.xml files
	public void setAddBackgroundStyleNames(final String styleNames) {
		background.addStyleName(styleNames);
	}

	@Override
	public void add(final Widget w) {
		content.add(w);
	}

	@Override
	public void clear() {
		content.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return content.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return content.remove(w);
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return rootPanel.addClickHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(final KeyUpHandler handler) {
		return rootPanel.addKeyUpHandler(handler);
	}

	public void setPaddingEnabled(final boolean padding) {
		this.setStyleName(style.boxPadding(), padding);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(final MouseUpHandler handler) {
		return rootPanel.addMouseUpHandler(handler);
	}
}
