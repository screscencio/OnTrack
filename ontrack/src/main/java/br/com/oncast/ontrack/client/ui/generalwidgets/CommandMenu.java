package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.List;

import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class CommandMenu extends Composite {

	private static final GlobalNativeEventService GLOBAL_NATIVE_EVENT_SERVICE = GlobalNativeEventService.getInstance();

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	private final NativeEventListener nativeEventListener;

	@UiField
	protected MenuBar menu;

	@UiField
	protected FocusPanel focusPanel;

	private CloseHandler closeHandler;

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		menu.setFocusOnHoverEnabled(true);
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);

		nativeEventListener = new NativeEventListener() {

			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
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
		GLOBAL_NATIVE_EVENT_SERVICE.addClickListener(nativeEventListener);

		this.setVisible(true);
		menu.focus();
	}

	public void hide() {
		GLOBAL_NATIVE_EVENT_SERVICE.removeClickListener(nativeEventListener);
		if (!this.isVisible()) return;

		this.setVisible(false);
		if (closeHandler != null) closeHandler.onClose();
	}

	@UiHandler("focusPanel")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ESCAPE) return;

		event.preventDefault();
		event.stopPropagation();
		hide();
	}

	public void addCloseHandler(final CloseHandler closeHandler) {
		this.closeHandler = closeHandler;
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		GLOBAL_NATIVE_EVENT_SERVICE.removeClickListener(nativeEventListener);
	}
}
