package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollableCommandMenu extends Composite {

	private static final int MAX_HEIGHT = 300;

	private static final int MAX_WIDTH = 670;

	private static ScrollableCommandMenuUiBinder uiBinder = GWT.create(ScrollableCommandMenuUiBinder.class);

	interface ScrollableCommandMenuUiBinder extends UiBinder<Widget, ScrollableCommandMenu> {}

	@UiField
	protected ScrollPanel scrollPanel;

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected CommandMenu menu;

	private CloseHandler closeHandler;

	private final WidgetVisibilityAssurer visibilityAssurer;

	public ScrollableCommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		visibilityAssurer = new WidgetVisibilityAssurer(scrollPanel);

		menu.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(final KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() != BrowserKeyCodes.KEY_DOWN && event.getNativeEvent().getKeyCode() != BrowserKeyCodes.KEY_UP) return;
				ensureSelectedItemIsVisible();
			}
		});
		menu.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() != BrowserKeyCodes.KEY_DOWN && event.getNativeEvent().getKeyCode() != BrowserKeyCodes.KEY_UP) return;
				ensureSelectedItemIsVisible();
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

	public void show(final Widget relativeWidget) {
		scrollPanel.setVisible(true);
		menu.show();
		ajustDimentions();
		visibilityAssurer.assureVisibilityAround(relativeWidget);
	}

	@UiHandler("focusPanel")
	protected void handleMouseDown(final MouseDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
	}

	private void hide() {
		scrollPanel.setVisible(false);
	}

	private void ensureSelectedItemIsVisible() {
		final int menuTop = scrollPanel.getVerticalScrollPosition();
		final int menuHeight = scrollPanel.getElement().getClientHeight();
		final int menuBottom = menuTop + menuHeight;

		final int itemTop = menu.getSelectedItem().getElement().getOffsetTop();
		final int itemHeight = menu.getSelectedItem().getElement().getOffsetHeight();
		final int itemBottom = menu.getSelectedItem().getElement().getOffsetTop() + itemHeight;

		if (itemTop < menuTop) scrollPanel.setVerticalScrollPosition(itemTop - 1);
		else if (itemBottom > menuBottom) scrollPanel.setVerticalScrollPosition(itemTop - menuHeight + itemHeight + 3);
	}

	private void ajustDimentions() {
		/*
		 * IMPORTANT Do not use max_height CSS property directly in the ui.xml file, because the first time this ScrollabeCommandMenu is
		 * created, the CSS class which this property is set is not being loaded, causing the visibility assurance to act incorrectly.
		 */
		int maxHeight;
		if (menu.getOffsetWidth() > MAX_WIDTH) {
			scrollPanel.setWidth((MAX_WIDTH + 20) + "px");
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.SCROLL);
			maxHeight = MAX_HEIGHT + 10;
		}
		else {
			scrollPanel.setWidth((menu.getElement().getClientWidth() + 20) + "px");
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			maxHeight = MAX_HEIGHT;
		}

		if (scrollPanel.getOffsetHeight() > maxHeight) scrollPanel.setHeight(maxHeight + "px");
	}
}
