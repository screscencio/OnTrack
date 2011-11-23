package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupConfig {
	private Widget widgetToPopup;
	private Widget alignRight;
	private Widget alignBelow;

	private PopupConfig() {}

	// FIXME Rodrigo: Provide documentation.

	public static PopupConfig configPopup() {
		return new PopupConfig();
	}

	public PopupConfig link(final Widget popupLink) {
		if (!(popupLink instanceof HasClickHandlers)) throw new IllegalArgumentException("The popup link must accept clicks (Implement HasClickHandlers).");

		((HasClickHandlers) popupLink).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				linkClicked();
			}
		});

		if (popupLink instanceof Button) {
			DOM.setStyleAttribute(popupLink.getElement(), "cursor", "default");
		}
		else {
			DOM.setStyleAttribute(popupLink.getElement(), "cursor", "pointer");
		}

		return this;
	}

	public PopupConfig popup(final Widget widgetToPopup) {
		if (!(widgetToPopup instanceof HasCloseHandlers)) throw new IllegalArgumentException(
				"The popup widget must be able to notify close event (Implement HasCloseHandlers interface).");

		if (!widgetToPopup.isAttached()) {
			widgetToPopup.setVisible(false);
			RootPanel.get().add(widgetToPopup);
		}

		addCloseHandlerToPopupWidget(widgetToPopup);

		this.widgetToPopup = widgetToPopup;
		DOM.setStyleAttribute(widgetToPopup.getElement(), "position", "absolute");
		return this;
	}

	public PopupConfig alignRight(final Widget widget) {
		this.alignRight = widget;
		return this;
	}

	public PopupConfig alignBelow(final Widget widget) {
		this.alignBelow = widget;
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addCloseHandlerToPopupWidget(final Widget widgetToPopup) {
		((HasCloseHandlers) widgetToPopup).addCloseHandler(new CloseHandler() {
			@Override
			public void onClose(final CloseEvent event) {
				popupWidgetClosed();
			}
		});
	}

	private void popupWidgetClosed() {
		MaskPanel.assureHidden();
	}

	private void linkClicked() {
		if (widgetToPopup == null) throw new IllegalStateException("No popup panel attached to link. Did you forget to call the PopupConfig#popup() method?");
		MaskPanel.show(createPanelHider());
		widgetToPopup.setVisible(true);

		evalHorizontalPosition();
		evalVerticalPosition();
	}

	private void evalVerticalPosition() {
		if (alignBelow == null) return;

		final int desiredTop = alignBelow.getAbsoluteTop() + alignBelow.getOffsetHeight();
		if (newTopFits(desiredTop)) {
			DOM.setStyleAttribute(widgetToPopup.getElement(), "top", desiredTop + "px");
			return;
		}

		final int acceptedTop = alignBelow.getAbsoluteTop() - widgetToPopup.getOffsetHeight();
		if (newTopFits(acceptedTop)) {
			DOM.setStyleAttribute(widgetToPopup.getElement(), "top", acceptedTop + "px");
			return;
		}

		final int constrainedTop = Math.max(0, Math.min(Window.getClientHeight() - widgetToPopup.getOffsetHeight(), desiredTop));
		DOM.setStyleAttribute(widgetToPopup.getElement(), "top", constrainedTop + "px");
	}

	private boolean newTopFits(final int newTop) {
		if (newTop < 0) return false;
		if (newTop + widgetToPopup.getOffsetHeight() > Window.getClientHeight()) return false;
		return true;
	}

	private void evalHorizontalPosition() {
		if (alignRight == null) return;

		final int desiredLeft = alignRight.getAbsoluteLeft() + alignRight.getOffsetWidth() - widgetToPopup.getOffsetWidth();
		if (newLeftFits(desiredLeft)) {
			DOM.setStyleAttribute(widgetToPopup.getElement(), "left", desiredLeft + "px");
			return;
		}

		final int acceptedLeft = alignRight.getAbsoluteLeft();
		if (newLeftFits(acceptedLeft)) {
			DOM.setStyleAttribute(widgetToPopup.getElement(), "left", acceptedLeft + "px");
			return;
		}

		final int constrainedLeft = Math.max(0, Math.min(Window.getClientWidth() - widgetToPopup.getOffsetWidth(), desiredLeft));
		DOM.setStyleAttribute(widgetToPopup.getElement(), "left", constrainedLeft + "px");
	}

	private boolean newLeftFits(final int newLeft) {
		if (newLeft < 0) return false;
		if (newLeft + widgetToPopup.getOffsetWidth() > Window.getClientWidth()) return false;
		return true;
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
