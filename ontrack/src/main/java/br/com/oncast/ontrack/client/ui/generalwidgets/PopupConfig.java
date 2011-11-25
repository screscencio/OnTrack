package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
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
	// FIXME Rodrigo: Create some unit tests.

	/**
	 * Popups that implement this interface are <i>popup-aware</i>. This means that they know how to show and hide themselves.<br />
	 * The {@link PopupConfig} won't change the popup widget visibility, instead it will ask them to show or hide when appropriate.<br />
	 * Being <i>popup-aware</i> is particularly interesting if the popup widget is interested on being notified when it must show or hide, sometimes it needs to
	 * do something on this occasions. Note that if the controller wants to add extra functionality he may use the {@link PopupConfig#onOpen(PopupOpenListener)}
	 * and {@link PopupConfig#onClose(PopupCloseListener)} instead of making the popup widget <i>popup-aware</i>.
	 */
	public interface PopupAware {
		/**
		 * Called when the popup config wants the popup widget to show.
		 */
		public abstract void show();

		/**
		 * Called when the popup config wants the popup widget to hide.
		 */
		public abstract void hide();
	}

	/**
	 * Listener to popup open events. It is designed to be used by the view controller. If you want to let the popup widget itself get notified of such events,
	 * make it implement the {@link PopupAware} interface.
	 */
	public interface PopupOpenListener {
		public abstract void onWillOpen();
	}

	/**
	 * Listener to popup close (hide) events. It is designed to be used by the view controller. If you want to let the popup widget itself get notified of such
	 * events,
	 * make it implement the {@link PopupAware} interface.
	 */
	public interface PopupCloseListener {
		public abstract void onHasClosed();
	}

	private Widget widgetToPopup;
	private Widget alignRight;
	private Widget alignBelow;
	private PopupOpenListener openListener;
	private PopupCloseListener closeListener;

	private boolean leaveWidgetInDomOnClose = true;
	private HandlerRegistration closeHandler;
	private boolean shown;

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
	 * Defines the link to the popup. Note that there is another way to trigger a popup, take a look at the {@link #pop()} method.
	 * @param popupLink the widget that will trigger the popup upon click.
	 * @return the self assistant for in-line call convenience.
	 * @throws IllegalArgumentException in case the link widget does not implement {@link HasClickHandlers}.
	 */
	public PopupConfig link(final Widget popupLink) {
		if (!(popupLink instanceof HasClickHandlers)) throw new IllegalArgumentException("The popup link must accept clicks (Implement HasClickHandlers).");

		((HasClickHandlers) popupLink).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				engagePopup();
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
		// FIXME Rodrigo: Rename method to 'widget'.

		if (this.widgetToPopup != null) throw new IllegalStateException("You cannot set the popup widget twice in a popup configuration.");

		if (!(widgetToPopup instanceof HasCloseHandlers)) throw new IllegalArgumentException(
				"The popup widget must implement HasCloseHandlers interface.");

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

	/**
	 * Makes the popup pop up, i.e., shows the popup to the user. Note that you may link the popup to a click event of a widget, take a look at
	 * {@link #link(Widget)}.
	 */
	public void pop() {
		engagePopup();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addCloseHandlerToPopupWidget() {
		closeHandler = ((HasCloseHandlers) widgetToPopup).addCloseHandler(new CloseHandler() {
			@Override
			public void onClose(final CloseEvent event) {
				shown = false;
				MaskPanel.assureHidden();
			}
		});
	}

	private void engagePopup() {
		if (widgetToPopup == null) throw new IllegalStateException("No popup panel attached to link. Did you forget to call the PopupConfig#popup() method?");
		MaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				disengagePopup();
			}
		});

		if (!widgetToPopup.isAttached()) {
			widgetToPopup.setVisible(false);
			RootPanel.get().add(widgetToPopup);
			leaveWidgetInDomOnClose = false;
		}
		addCloseHandlerToPopupWidget();

		if (openListener != null) openListener.onWillOpen();
		if (widgetToPopup instanceof PopupAware) ((PopupAware) widgetToPopup).show();
		else widgetToPopup.setVisible(true);
		shown = true;

		evalHorizontalPosition();
		evalVerticalPosition();
	}

	private void disengagePopup() {
		if (shown) {
			if (widgetToPopup instanceof PopupAware) ((PopupAware) widgetToPopup).hide();
			else widgetToPopup.setVisible(false);
			shown = false;
		}
		if (closeListener != null) closeListener.onHasClosed();

		closeHandler.removeHandler();

		if (!leaveWidgetInDomOnClose) {
			widgetToPopup.removeFromParent();
		}
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
