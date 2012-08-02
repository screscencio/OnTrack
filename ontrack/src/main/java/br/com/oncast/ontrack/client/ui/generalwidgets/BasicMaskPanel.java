package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class BasicMaskPanel implements IsWidget {

	private final FocusPanel maskPanel;
	private HideHandler hideHandler;

	protected BasicMaskPanel() {
		maskPanel = new FocusPanel();
		maskPanel.setStyleName("maskPanel");
		maskPanel.setVisible(false);

		RootPanel.get().add(maskPanel);
		maskPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
	}

	protected void show(final HideHandler hideHandler) {

		if (maskPanel.isVisible()) throw new RuntimeException("The MaskPanel is already visible.");

		this.hideHandler = hideHandler;
		maskPanel.setVisible(true);
	}

	protected void hide() {
		if (hideHandler != null) {
			final HideHandler lastHideHandler = hideHandler;
			hideHandler = null;
			lastHideHandler.onWillHide();
		}

		maskPanel.setVisible(false);
		RootPanel.get().remove(maskPanel);
	}

	protected Style getStyle() {
		return maskPanel.getElement().getStyle();
	}

	protected void setFocus(final boolean focused) {
		maskPanel.setFocus(focused);
	}

	public void add(final Widget widget) {
		maskPanel.add(widget);
	}

	@Override
	public Widget asWidget() {
		return maskPanel;
	}

	public BasicMaskPanel setModal(final boolean isModal) {
		maskPanel.setStyleName("maskPanelModal", isModal);
		return this;
	}

}
