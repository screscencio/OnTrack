package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupConfig {
	private final static class PopupActivatorClickHandler implements ClickHandler {
		private Widget widgetToPopup;
		private Widget alignRight;
		private Widget alignBelow;

		@Override
		public void onClick(final ClickEvent event) {
			if (widgetToPopup == null) throw new RuntimeException("No popup panel attached to link. Did you forget to call the PopupConfig#popup() method?");
			MaskPanel.show(createPanelHider());
			widgetToPopup.setVisible(true);

			evalHorizontalPosition();
			evalVerticalPosition();
		}

		private void evalVerticalPosition() {
			if (alignBelow == null) return;

			final int desiredTop = alignBelow.getAbsoluteTop() + alignBelow.getOffsetHeight();
			final int constrainedTop = Math.max(0, Math.min(Window.getClientHeight() - widgetToPopup.getOffsetHeight(), desiredTop));

			// FIXME Rodrigo: Auto-align above in case there is no space to align below.
			// FIXME Rodrigo: Auto-align the closest possible to first desiredTop in case none of the two vertical alignment rules fit.

			DOM.setStyleAttribute(widgetToPopup.getElement(), "top", constrainedTop + "px");
		}

		private void evalHorizontalPosition() {
			if (alignRight == null) return;

			final int desiredLeft = alignRight.getAbsoluteLeft() + alignRight.getOffsetWidth() - widgetToPopup.getOffsetWidth();
			final int constrainedLeft = Math.max(0, Math.min(Window.getClientWidth() - widgetToPopup.getOffsetWidth(), desiredLeft));

			// FIXME Rodrigo: Auto-align from left in case there is no space from right.
			// FIXME Rodrigo: Auto-align closer to first desiredLeft in case none of the two horizontal alignment rules fit.

			DOM.setStyleAttribute(widgetToPopup.getElement(), "left", constrainedLeft + "px");
		}

		private HideHandler createPanelHider() {
			return new HideHandler() {
				@Override
				public void onWillHide() {
					widgetToPopup.setVisible(false);
				}
			};
		}
	}

	private final PopupActivatorClickHandler activatorClickHandler = new PopupActivatorClickHandler();

	private PopupConfig() {}

	// FIXME Rodrigo: Provide documentation.

	public static PopupConfig link(final HasClickHandlers popupLink) {
		final PopupConfig config = new PopupConfig();
		popupLink.addClickHandler(config.activatorClickHandler);
		return config;
	}

	public PopupConfig popup(final Widget widgetToPopup) {
		if (!widgetToPopup.isAttached()) RootPanel.get().add(widgetToPopup);
		activatorClickHandler.widgetToPopup = widgetToPopup;
		DOM.setStyleAttribute(widgetToPopup.getElement(), "position", "absolute");
		return this;
	}

	public PopupConfig alignPopupRight(final Widget widget) {
		activatorClickHandler.alignRight = widget;
		return this;
	}

	public PopupConfig alignBelow(final Widget widget) {
		activatorClickHandler.alignBelow = widget;
		return this;
	}
}
