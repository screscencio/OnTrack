package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class CommandMenu extends Composite {

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	interface Style extends CssResource {
		String menuItemStyle();
	}

	@UiField
	protected MenuBar menu;

	@UiField
	protected Style style;

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		menu.setFocusOnHoverEnabled(true);
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);
	}

	public void setItens(final List<CommandMenuItem> itens) {
		menu.clearItems();
		for (final CommandMenuItem item : itens) {
			final MenuItem menuItem = new MenuItem(item.getText(), true, new Command() {

				@Override
				public void execute() {
					hide();
					item.getCommand().execute();
				}
			});
			menu.addItem(menuItem);
			menuItem.addStyleName(style.menuItemStyle());
		}
	}

	public void show() {
		this.setVisible(true);
	}

	public void hide() {
		this.setVisible(false);
	}
}
