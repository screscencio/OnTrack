package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.List;

import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class CommandMenu extends Composite {

	private static final int MOUSEOUT_HIDE_DELAY_MILLIS = 2000;

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	@UiField
	protected MenuBar menu;

	@UiField
	protected FocusPanel focusPanel;

	private final Timer hiddingTimer;

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		menu.setFocusOnHoverEnabled(true);
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);

		hiddingTimer = new Timer() {

			@Override
			public void run() {
				hide();
			}
		};
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
		}
	}

	public void show() {
		this.setVisible(true);
	}

	public void hide() {
		this.setVisible(false);
		cancelHiddingTimer();
	}

	private void scheduleHiddingTimer() {
		hiddingTimer.schedule(MOUSEOUT_HIDE_DELAY_MILLIS);
	}

	private void cancelHiddingTimer() {
		hiddingTimer.cancel();
	}

	@UiHandler("focusPanel")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (BrowserKeyCodes.isArrowKey(event.getNativeKeyCode())) cancelHiddingTimer();
		if (event.getNativeKeyCode() != KEY_ESCAPE) return;

		event.preventDefault();
		event.stopPropagation();
		hide();
	}

	@UiHandler("focusPanel")
	protected void handleMouseOut(final MouseOutEvent event) {
		scheduleHiddingTimer();
	}

	@UiHandler("focusPanel")
	protected void handleMouseOver(final MouseOverEvent event) {
		cancelHiddingTimer();
	}
}
