package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupOpenListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenuItem extends Composite implements HasText {

	private static ApplicationMenuItemUiBinder uiBinder = GWT.create(ApplicationMenuItemUiBinder.class);

	interface ApplicationMenuItemUiBinder extends UiBinder<Widget, ApplicationMenuItem> {}

	interface ApplicationMenuItemStyle extends CssResource {
		String menuItemSelected();

		String biggerFont();
	}

	@UiField
	ApplicationMenuItemStyle style;

	public ApplicationMenuItem() {
		initWidget(uiBinder.createAndBindUi(this));
		content.setVisible(false);
		menuHeader.setVisible(false);
	}

	public ApplicationMenuItem(final String text, final boolean hasBiggerFont) {
		initWidget(uiBinder.createAndBindUi(this));
		content.setVisible(false);
		setText(text);
		setBiggerFont(hasBiggerFont);
	}

	@UiField
	FocusPanel rootPanel;

	@UiField
	HorizontalPanel container;

	@UiField
	SimplePanel menuHeader;

	@UiField
	Label arrow;

	@UiField
	SimplePanel content;

	private PopupConfig popup;

	private boolean isOpen = false;

	private HasText textWidget;

	@UiHandler("rootPanel")
	void onClick(final ClickEvent e) {
		toggleMenu();
	}

	public void toggleMenu() {
		if (!isOpen) popup.pop();
		else popup.hidePopup();
	}

	public void setMenuHeaderWidget(final Widget headerWidget) {
		if (!(headerWidget instanceof HasText)) throw new IllegalArgumentException();
		textWidget = (HasText) headerWidget;
		menuHeader.setWidget(headerWidget);
		menuHeader.setVisible(headerWidget != null);
	}

	@Override
	public void setText(final String text) {
		setMenuHeaderWidget(new Label(text));
		menuHeader.setVisible(text != null && !text.isEmpty());
	}

	public void setContentStyle(final String style) {
		content.setStyleName(style);
		content.setVisible(style != null && !style.isEmpty());
	}

	@Override
	public String getText() {
		if (textWidget == null) return "";

		return textWidget.getText();
	}

	public void setPopupConfig(final PopupConfig popup) {
		this.popup = popup;
		popup.onOpen(new PopupOpenListener() {
			@Override
			public void onWillOpen() {
				arrow.addStyleName("icon-caret-up");
				container.addStyleName(style.menuItemSelected());
				isOpen = true;
			}
		});
		popup.onClose(new PopupCloseListener() {

			@Override
			public void onHasClosed() {
				arrow.removeStyleName("icon-caret-up");
				container.removeStyleName(style.menuItemSelected());
				isOpen = false;
			}
		});
		popup.setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
	}

	public void setBiggerFont(final boolean hasBiggerFont) {
		menuHeader.setStyleName(style.biggerFont(), hasBiggerFont);
	}

}
