package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class BasicMaskPanel {

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

		configureToCleanOnAnyNavigationEvent();
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
	}

	protected Style getStyle() {
		return maskPanel.getElement().getStyle();
	}

	protected void setFocus(final boolean focused) {
		maskPanel.setFocus(focused);
	}
	
	private void configureToCleanOnAnyNavigationEvent() {
		maskPanel.addHandler(new PlaceChangeRequestEvent.Handler() {
			@Override
			public void onPlaceChangeRequest(final PlaceChangeRequestEvent event) {
				maskPanel.clear();
			}
		}, PlaceChangeRequestEvent.TYPE);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(final ValueChangeEvent<String> event) {
				maskPanel.clear();
			}
		});
	}

}
