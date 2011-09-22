package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollableCommandMenu extends Composite {

	private static ScrollableCommandMenuUiBinder uiBinder = GWT.create(ScrollableCommandMenuUiBinder.class);

	interface ScrollableCommandMenuUiBinder extends UiBinder<Widget, ScrollableCommandMenu> {}

	interface Style extends CssResource {
		String panel();

		String panelWithHorizontalScroll();
	}

	@UiField
	protected Style style;

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
				if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_DOWN || event.getNativeKeyCode() == BrowserKeyCodes.KEY_UP) {
					ensureSelectedItemIsVisible(event);
				}
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
		ajustSize();
		menu.show();
		visibilityAssurer.assureVisibility();
	}

	public boolean grewDown() {
		if (this.getElement().getStyle().getMarginTop().isEmpty()) return true;
		return false;
	}

	private void hide() {
		scrollPanel.setVisible(false);
	}

	private void ensureSelectedItemIsVisible(final KeyUpEvent event) {
		final int scrollAbsoluteTop = scrollPanel.getElement().getAbsoluteTop();
		final int menuAbsoluteTop = menu.getElement().getAbsoluteTop();
		final int scrollHeight = scrollPanel.getElement().getOffsetHeight();
		final int itemTop = menu.getSelectedItem().getElement().getOffsetTop();
		final int itemHeight = menu.getSelectedItem().getElement().getOffsetHeight();

		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_DOWN) {
			if (itemTop < 5) {
				scrollPanel.scrollToTop();
				return;
			}
			if ((scrollAbsoluteTop - menuAbsoluteTop + scrollHeight) <= (itemTop + itemHeight)) {
				scrollPanel.setVerticalScrollPosition(scrollPanel.getVerticalScrollPosition() + itemHeight + 2);
			}
		}
		else if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_UP) {
			if ((itemTop + itemHeight) >= (menu.getOffsetHeight() - 5)) {
				scrollPanel.scrollToBottom();
				return;
			}
			if ((scrollAbsoluteTop - menuAbsoluteTop) > itemTop) {
				scrollPanel.setVerticalScrollPosition(scrollPanel.getVerticalScrollPosition() - itemHeight - 2);
			}
		}
	}

	private void ajustSize() {
		if (menu.getOffsetWidth() > 670) {
			scrollPanel.setStyleName(style.panelWithHorizontalScroll());
			scrollPanel.setWidth("675px");
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.SCROLL);
		}
		else {
			scrollPanel.setStyleName(style.panel());
			scrollPanel.setWidth(menu.getElement().getClientWidth() + 30 + "px");
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		}
	}
}
