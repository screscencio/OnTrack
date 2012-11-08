package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class AnimatedHorizontalContainer extends AnimatedCellContainer<HorizontalPanel> {

	public AnimatedHorizontalContainer() {
		super(new HorizontalPanel());
	}

	@Override
	protected void insertIntoContainer(final IsWidget widget, final int beforeIndex) {
		container.insert(widget, beforeIndex);
	}

}
