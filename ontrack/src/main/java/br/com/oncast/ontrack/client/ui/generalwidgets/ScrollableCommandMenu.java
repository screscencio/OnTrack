package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollableCommandMenu extends Composite {

	private static ScrollableCommandMenuUiBinder uiBinder = GWT.create(ScrollableCommandMenuUiBinder.class);

	interface ScrollableCommandMenuUiBinder extends UiBinder<Widget, ScrollableCommandMenu> {}

	@UiField
	protected ScrollPanel scrollPanel;

	@UiField
	protected CommandMenu menu;

	private CloseHandler closeHandler;

	private final WidgetVisibilityAssurer visibilityAssurer;

	public ScrollableCommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		visibilityAssurer = new WidgetVisibilityAssurer(this);
		menu.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_DOWN || event.getNativeKeyCode() == BrowserKeyCodes.KEY_UP)
					scrollPanel.ensureVisible(menu.getSelectedItem());
				}
		});
		menu.addCloseHandler(new CloseHandler() {

			@Override
			public void onClose() {
				if (closeHandler != null) closeHandler.onClose();
				hide();
			}
		});
	}

	public void setItems(final List<CommandMenuItem> items) {
		menu.setItems(items);
	}

	public void addCloseHandler(final CloseHandler handler) {
		this.closeHandler = handler;
	}

	public void show() {
		scrollPanel.setVisible(true);
		scrollPanel.setWidth(menu.getElement().getClientWidth() + 30 + "px");
		menu.show();

		scrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		visibilityAssurer.assureVisibility();
	}

	private void hide() {
		scrollPanel.setVisible(false);
	}
}
