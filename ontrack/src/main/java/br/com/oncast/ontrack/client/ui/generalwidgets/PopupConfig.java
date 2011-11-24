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

/**
 * The {@link PopupConfig} allows easy link-popup binding in controllers. It uses a fluent interface syntax and handles popup placement, user interaction mask,
 * auto-closing behavior on leave and general popup style-sheet elements.
 * This is an example of how to use it:
 * 
 * <pre>
 * configPopup().link(changePasswordLabel).popup(new ChangePasswordForm()).alignRight(changePasswordLabel).alignBelow(this);
 * </pre>
 */
public class PopupConfig {
	public interface PopupAware {
		public abstract void show();

		public abstract void hide();
	}

	public interface PopupOpenListener {
		public abstract void onWillOpen();
	}

	public interface PopupCloseListener {
		public abstract void onHasClosed();
	}

	private Widget widgetToPopup;
	private Widget alignRight;
	private Widget alignBelow;
	private PopupOpenListener openListener;
	private PopupCloseListener closeListener;

	private PopupConfig() {}

	/**
	 * Starts a popup configuration.<br />
	 * A convenient way to use this method is to import it statically.
	 * @return a new popup configuration assistant.
	 */
	public static PopupConfig configPopup() {
		return new PopupConfig();
	}

	/**
	 * Defines the link to the popup. Currently popup may only be triggered by something the user may click on, in the future programatically dispatching popups
	 * may also be provided.
	 * TODO+ Allow programatically dispatching popups.
	 * @param popupLink the widget that will trigger the popup upon click.
	 * @return the self assistant for in-line call convenience.
	 * @throws IllegalArgumentException in case the link widget does not implement {@link HasClickHandlers}.
	 */
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

	/**
	 * Defines the widget to popup, i.e., the popup widget itself. The popup widget may be any widget that accepts {@link CloseHandler CloseHandlers} (i.e.,
	 * implements {@link HasCloseHandlers}). This implementation is needed to hide the mask panel blocking user input in the
	 * main UI. <br />
	 * The widget may also implement the {@link PopupAware} interface. In this case the popup widget will be asked to show and hide, instead of simply setting
	 * the visible property to true. This gives the widget the advantage to do actions during show (like cleaning it up) and hiding (like aborting an operation
	 * that is under process).<br />
	 * <b>Important:</b> Note that the same instance of the widget will be reused every time, so be sure to prepare it for the next call when it gets closed.<br />
	 * In the future a factory to popup widgets may be passed instead of a sole instance, therefore removing the need to prepare the widget for the next call.
	 * TODO+ Make PopupConfig accept popup widget factories, not just single instances.
	 * @param widgetToPopup the instance of the widget to popup.
	 * @return the self assistant for in-line call convenience.
	 * @throws IllegalStateException in case the popup widget was set more than once.
	 * @throws IllegalArgumentException in case the provided widget does not implement {@link HasCloseHandlers} nor {@link PopupAware}.
	 */
	public PopupConfig popup(final Widget widgetToPopup) {
		if (this.widgetToPopup != null) throw new IllegalStateException("You cannot set the popup widget twice in a popup configuration.");

		if (!(widgetToPopup instanceof HasCloseHandlers)) throw new IllegalArgumentException(
				"The popup widget must implement HasCloseHandlers interface.");

		if (!widgetToPopup.isAttached()) {
			widgetToPopup.setVisible(false);
			RootPanel.get().add(widgetToPopup);
		}

		addCloseHandlerToPopupWidget(widgetToPopup);

		this.widgetToPopup = widgetToPopup;
		DOM.setStyleAttribute(widgetToPopup.getElement(), "position", "absolute");
		return this;
	}

	/**
	 * Defines that the popup widget must have its right margin matching the provided reference widget's right margin.<br />
	 * Note that the popup configuration may override this definition in case it realizes the popup widget will not fit. The popup configuration will try the
	 * following to determine the popup widget horizontal alignment:
	 * <ol>
	 * <li>Match popup widget and reference widget right margins;</li>
	 * <li>Match popup widget and reference widget left margins, in case the first rule does not apply;</li>
	 * <li>Use the closes possible placement to the first rule in case none of the two first apply.</li>
	 * </ol>
	 * @param widget the reference widget.
	 * @return the self assistant for in-line call convenience.
	 */
	public PopupConfig alignRight(final Widget widget) {
		this.alignRight = widget;
		return this;
	}

	/**
	 * Defines that the popup widget must be placed bellow a reference widget.<br />
	 * Just like {@link #alignRight(Widget)}, the popup configuration may override this definition and place the popup on a more convenient place in case it
	 * does not fit bellow the reference widget. These are the rules to determining the popup vertical alignment:
	 * <ol>
	 * <li>Put popup widget immediately bellow the reference widget;</li>
	 * <li>Put popup widget immediately above the reference widget, in case the first rule does not apply;</li>
	 * <li>Use the closes possible placement to the first rule in case none of the two first apply.</li>
	 * </ol>
	 * @param widget the reference widget.
	 * @return the self assistant for in-line call convenience.
	 */
	public PopupConfig alignBelow(final Widget widget) {
		this.alignBelow = widget;
		return this;
	}

	/**
	 * Defines a listener that will be notified when the popup opens.<br />
	 * Each popup configuration allows just one open listener.
	 * @param openListener the open listener to be notified.
	 * @return the self assistant for in-line call convenience.
	 * @throws IllegalStateException in case there is already a listener set.
	 */
	public PopupConfig onOpen(final PopupOpenListener openListener) {
		if (this.openListener != null) throw new IllegalStateException("Another open listener already set.");
		this.openListener = openListener;
		return this;
	}

	/**
	 * Defines a listener that will be notified when the popup closes.<br />
	 * Each popup configuration allows just one close listener.
	 * @param closeListener the close listener to be notified.
	 * @return the self assistant for in-line call convenience.
	 * @throws IllegalStateException in case there is already a listener set.
	 */
	public PopupConfig onClose(final PopupCloseListener closeListener) {
		if (this.closeListener != null) throw new IllegalStateException("Another close listener already set.");
		this.closeListener = closeListener;
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
		MaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				if (widgetToPopup instanceof PopupAware) ((PopupAware) widgetToPopup).hide();
				else widgetToPopup.setVisible(false);
				if (closeListener != null) closeListener.onHasClosed();
			}
		});

		if (openListener != null) openListener.onWillOpen();
		if (widgetToPopup instanceof PopupAware) ((PopupAware) widgetToPopup).show();
		else widgetToPopup.setVisible(true);

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
}
