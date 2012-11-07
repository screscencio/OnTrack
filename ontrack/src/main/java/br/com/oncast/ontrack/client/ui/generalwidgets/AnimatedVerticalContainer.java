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

	public AnimatedVerticalContainer(final AnimationFactory animationFactory) {
		this(new VerticalPanel(), animationFactory);
	}

	public AnimatedVerticalContainer(final VerticalPanel container, final AnimationFactory animationFactory) {
		super(container, animationFactory);
	}

	@Override
	protected void insertIntoContainer(final IsWidget w, final int beforeIndex) {
		container.insert(w, beforeIndex);
	}

}
