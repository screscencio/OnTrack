package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class MaskPanel {

	private final FocusPanel maskPanel;

	private final List<ClickHandler> clickHandlers;

	public MaskPanel() {
		clickHandlers = new ArrayList<ClickHandler>();
		maskPanel = new FocusPanel();
		maskPanel.setStyleName("maskPanel");
		maskPanel.setVisible(false);
		RootPanel.get().add(maskPanel);
		initializeClickHandler();
	}

	public void hide() {
		maskPanel.setVisible(false);
	}

	public void show() {
		maskPanel.setVisible(true);
	}

	public void addClickHandler(final ClickHandler click) {
		clickHandlers.add(click);
	}

	private void initializeClickHandler() {
		maskPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				notifyClickHandlers(event);
			}
		});
	}

	private void notifyClickHandlers(final ClickEvent event) {
		for (final ClickHandler clickHandler : clickHandlers)
			clickHandler.onClick(event);
	}
}
