package br.com.oncast.ontrack.client.ui.keyeventhandler;

import java.util.LinkedHashSet;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.ShortcutLabel;
import br.com.oncast.ontrack.client.ui.keyeventhandlers.ShortcutHelpPanelShortcutMappings;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ShortcutHelpPanel extends Composite implements PopupAware, HasCloseHandlers<ShortcutHelpPanel> {

	private static ShortcutHelpPanelUiBinder uiBinder = GWT.create(ShortcutHelpPanelUiBinder.class);

	interface ShortcutHelpPanelUiBinder extends UiBinder<Widget, ShortcutHelpPanel> {}

	interface ShortcutHelpPanelStyle extends CssResource {
		String shortcutLabel();
	}

	@UiField
	ShortcutHelpPanelStyle style;

	@UiField
	FocusPanel rootPanel;

	@UiField
	FlexTable shortcutsContainer;

	public ShortcutHelpPanel(final LinkedHashSet<ShortcutMapping<?>> registeredShortcuts) {
		initWidget(uiBinder.createAndBindUi(this));
		updateShortcuts(registeredShortcuts);
	}

	private void updateShortcuts(final LinkedHashSet<ShortcutMapping<?>> registeredShortcuts) {
		shortcutsContainer.setVisible(false);
		shortcutsContainer.clear();
		shortcutsContainer.setText(0, 0, "start");
		shortcutsContainer.setText(registeredShortcuts.size() - 1, 2, "end");
		final FlexCellFormatter formatter = shortcutsContainer.getFlexCellFormatter();
		final HorizontalAlignmentConstant right = HorizontalAlignmentConstant.startOf(Direction.RTL);

		int row = 0;
		for (final ShortcutMapping<?> sm : registeredShortcuts) {
			final ShortcutLabel shortcutLabel = new ShortcutLabel(sm.getShortcuts());
			shortcutLabel.addStyleName(style.shortcutLabel());
			shortcutsContainer.setWidget(row, 0, shortcutLabel);
			shortcutsContainer.setText(row, 1, ":");
			final Label description = new Label(sm.getDescription());
			shortcutsContainer.setWidget(row, 2, description);

			formatter.setColSpan(row, 0, 1);
			formatter.setColSpan(row, 1, 1);

			formatter.setHorizontalAlignment(row, 0, right);
			row++;
		}
		shortcutsContainer.setVisible(true);
	}

	@UiHandler("rootPanel")
	void onKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE ||
				ShortcutHelpPanelShortcutMappings.SHOW_SHORTCUT_HELP_PANEL.getShortcuts().accepts(e.getNativeEvent())) hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ShortcutHelpPanel> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		rootPanel.setFocus(true);
	}

	@Override
	public void hide() {
		if (!isVisible()) return;

		CloseEvent.fire(ShortcutHelpPanel.this, ShortcutHelpPanel.this);
	}

}
