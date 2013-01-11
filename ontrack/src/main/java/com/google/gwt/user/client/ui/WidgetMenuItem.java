package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;

public class WidgetMenuItem extends MenuItem {

	public WidgetMenuItem(final Widget widget, final Command cmd) {
		super("", true, new Command() {

			@Override
			public void execute() {
				cmd.execute();
				widget.onDetach();
			}
		});
		DOM.appendChild(getElement(), widget.getElement());
		widget.onAttach();
	}
}
