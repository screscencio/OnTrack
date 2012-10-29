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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenuItem extends Composite implements HasText {

	private static ApplicationMenuItemUiBinder uiBinder = GWT.create(ApplicationMenuItemUiBinder.class);

	interface ApplicationMenuItemUiBinder extends UiBinder<Widget, ApplicationMenuItem> {}

	interface ApplicationMenuItemStyle extends CssResource {
		String arrowUp();

		String menuItemSelected();

		String biggerFont();
	}

	@UiField
	ApplicationMenuItemStyle style;

	public ApplicationMenuItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ApplicationMenuItem(final String text, final boolean hasBiggerFont) {
		initWidget(uiBinder.createAndBindUi(this));
		setText(text);
		setBiggerFont(hasBiggerFont);
	}

	@UiField
	FocusPanel rootPanel;

	@UiField
	Label textLabel;

	@UiField
	Label arrow;

	private PopupConfig popup;

	private boolean isOpen;

	@UiHandler("rootPanel")
	void onClick(final ClickEvent e) {
		toggleMenu();
	}

	public void toggleMenu() {
		if (isOpen) popup.pop();
		else popup.hidePopup();
	}

	@Override
	public void setText(final String text) {
		textLabel.setText(text);
	}

	@Override
	public String getText() {
		return textLabel.getText();
	}

	public void setPopupConfig(final PopupConfig popup) {
		this.popup = popup;
		popup.onOpen(new PopupOpenListener() {
			@Override
			public void onWillOpen() {
				arrow.addStyleName(style.arrowUp());
				textLabel.addStyleName(style.menuItemSelected());
				isOpen = true;
			}
		});
		popup.onClose(new PopupCloseListener() {

			@Override
			public void onHasClosed() {
				arrow.removeStyleName(style.arrowUp());
				textLabel.removeStyleName(style.menuItemSelected());
				isOpen = false;
			}
		});
		popup.setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
	}

	public void setBiggerFont(final boolean hasBiggerFont) {
		textLabel.setStyleName(style.biggerFont(), hasBiggerFont);
	}

}
