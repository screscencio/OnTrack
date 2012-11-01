package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnimatedVerticalContainer extends AnimatedCellContainer<VerticalPanel> {

	public AnimatedVerticalContainer() {
		this(new VerticalPanel());
	}

	public AnimatedVerticalContainer(final VerticalPanel container) {
		super(container);
	}

	// TODO It's being used only for Tests remove it if possible
	AnimatedVerticalContainer(final AnimationFactory animationFactory) {
		super(new VerticalPanel(), animationFactory);
	}

	@Override
	protected void insertIntoContainer(final IsWidget w, final int beforeIndex) {
		container.insert(w, beforeIndex);
	}

}
