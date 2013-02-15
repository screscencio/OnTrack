package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MouseCommandsMenu extends Composite implements HasCloseHandlers<MouseCommandsMenu>, PopupAware {

	private static MouseActionsMenuUiBinder uiBinder = GWT.create(MouseActionsMenuUiBinder.class);

	interface MouseActionsMenuUiBinder extends UiBinder<Widget, MouseCommandsMenu> {}

	@UiField
	protected MenuBarCommandMenu menu;

	public MouseCommandsMenu(final List<CommandMenuItem> items) {
		initWidget(uiBinder.createAndBindUi(this));
		menu.setItems(items);
		menu.setFocusWhenMouseOver(true);
		menu.addCloseHandler(new CloseHandler<MenuBarCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<MenuBarCommandMenu> event) {
				hide();
			}
		});
	}

	@Override
	public void show() {
		if (isVisible()) return;
		menu.show();
		menu.selectFirstItem();
		menu.focus();
	}

	@Override
	public void hide() {
		if (!isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<MouseCommandsMenu> handler) {
		return addHandler(handler, CloseEvent.getType());
	}
}
