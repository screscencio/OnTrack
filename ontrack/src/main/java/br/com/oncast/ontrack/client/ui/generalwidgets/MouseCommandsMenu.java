package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class MouseCommandsMenu extends Composite {

	private static MouseActionsMenuUiBinder uiBinder = GWT.create(MouseActionsMenuUiBinder.class);

	interface MouseActionsMenuUiBinder extends UiBinder<Widget, MouseCommandsMenu> {}

	@UiField
	protected CommandMenu menu;

	@UiField
	protected Image menuImage;

	private final WidgetVisibilityAssurer visibilityAssurer;

	public MouseCommandsMenu(final List<CommandMenuItem> items) {
		initWidget(uiBinder.createAndBindUi(this));
		menu.hide();
		menu.setItems(items);
		menu.setFocusWhenMouseOver(true);
		visibilityAssurer = new WidgetVisibilityAssurer(menu);
	}

	@UiHandler("menuImage")
	protected void onClick(final ClickEvent e) {
		showMenu();
	}

	private void showMenu() {
		menu.show();
		menu.selectFirstItem();
		visibilityAssurer.assureVisibility();
	}
}
