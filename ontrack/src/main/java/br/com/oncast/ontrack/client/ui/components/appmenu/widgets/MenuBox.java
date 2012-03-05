package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class MenuBox extends Composite implements HasWidgets {

	private static MenuBoxUiBinder uiBinder = GWT.create(MenuBoxUiBinder.class);

	interface MenuBoxUiBinder extends UiBinder<Widget, MenuBox> {}

	@UiField
	HTMLPanel content;

	private MenuBoxItem selectedItem;

	public MenuBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public MenuBox(final String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void selectUp() {
		final int index = content.getWidgetIndex(selectedItem);
		selectItem(index - 1);
	}

	public void selectDown() {
		final int index = content.getWidgetIndex(selectedItem);
		selectItem(index + 1);
	}

	public void selectItem(final int i) {
		setSelectedItemSelection(false);
		final int count = content.getWidgetCount();
		if (count == 0) return;

		int index = 0;
		if (i < 0) index = count - 1;
		else if (count > i) index = i;

		selectedItem = (MenuBoxItem) content.getWidget(index);
		setSelectedItemSelection(true);
		ensureSelectedItemIsVisible();
	}

	public void ensureSelectedItemIsVisible() {
		if (selectedItem == null) return;

		final Element scroll = getElement();
		final int menuTop = scroll.getScrollTop();
		final int menuHeight = scroll.getClientHeight();
		final int menuBottom = menuTop + menuHeight;

		final Element selectedElement = selectedItem.getElement();
		final int itemTop = selectedElement.getOffsetTop() - scroll.getOffsetTop();
		final int itemHeight = selectedElement.getOffsetHeight();
		final int itemBottom = itemTop + itemHeight;

		if (itemTop < menuTop) scroll.setScrollTop(itemTop - 1);
		else if (itemBottom > menuBottom) scroll.setScrollTop(itemTop - menuHeight + itemHeight + 3);
	}

	private void setSelectedItemSelection(final boolean b) {
		if (selectedItem != null) selectedItem.setSelected(b);
	}

	@Override
	public void add(final Widget w) {
		content.add(w);
	}

	@Override
	public void clear() {
		content.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return content.iterator();
	}

	@Override
	public boolean remove(final Widget w) {
		return content.remove(w);
	}

	public MenuBoxItem getSelectedItem() {
		return selectedItem;
	}
}
