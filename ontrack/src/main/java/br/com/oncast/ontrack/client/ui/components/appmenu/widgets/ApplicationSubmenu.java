package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationSubmenu extends Composite implements HasCloseHandlers<ApplicationSubmenu>, PopupAware {

	private static ApplicationSubMenuUiBinder uiBinder = GWT.create(ApplicationSubMenuUiBinder.class);

	interface ApplicationSubMenuUiBinder extends UiBinder<Widget, ApplicationSubmenu> {}

	protected interface Style extends CssResource {
		String menuItem();
	}

	private HandlerRegistration closeHandler = null;

	@UiField
	MenuBar submenu;

	@UiField
	Style style;

	public ApplicationSubmenu() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void addItem(final String string, final Command command) {
		final MenuItem menuItem = new MenuItem(string, new Command() {

			@Override
			public void execute() {
				hide();
				command.execute();
			}
		});
		menuItem.setStyleName(style.menuItem());
		submenu.addItem(menuItem);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (closeHandler != null) {
			closeHandler.removeHandler();
			closeHandler = null;
		}
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ApplicationSubmenu> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}
}
