package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAndFadeAnimation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class AnimatedContainer extends Composite {

	private final static AnimationFactory DEFAULT_ANIMATION_FACTORY = new AnimationFactory() {

		@Override
		public ShowAnimation createShowAnimation(final Widget widget) {
			return new SlideAndFadeAnimation(widget);
		}

		@Override
		public HideAnimation createHideAnimation(final Widget widget) {
			return new SlideAndFadeAnimation(widget);
		}
	};

	private final AnimationFactory animationFactory;

	protected List<IsWidget> widgets;

	protected final ComplexPanel container;

	public AnimatedContainer(final ComplexPanel container) {
		this(container, DEFAULT_ANIMATION_FACTORY);
	}

	public AnimatedContainer(final ComplexPanel container, final AnimationFactory animationFactory) {
		if (!(container instanceof InsertPanel.ForIsWidget)) throw new IllegalArgumentException(
				"Only widget that implements InsertPanel.ForIsWidget is accepted");

		initWidget(container);

		this.container = container;
		this.animationFactory = animationFactory;
		widgets = new ArrayList<IsWidget>();
	}

	public Widget getWidget(final int index) {
		return widgets.get(index).asWidget();
	}

	public void insert(final IsWidget widget, final int index) {
		widgets.add(index, widget);
		insertIntoContainer(widget, getContainerIndex(widget));
		animationFactory.createShowAnimation(widget.asWidget()).show();
	}

	public void move(final IsWidget widget, final int index) {
		if (getWidgetIndex(widget) == index) return;

		widgets.remove(widget);
		container.remove(widget);
		widgets.add(index, widget);
		insertIntoContainer(widget, getContainerIndex(widget));
		if (!widget.asWidget().isVisible()) animationFactory.createShowAnimation(widget.asWidget()).show();
	}

	private int getContainerIndex(final IsWidget widget) {
		final int containerSize = container.getWidgetCount();
		final int listIndex = widgets.indexOf(widget);
		final int size = widgets.size();

		if (containerSize == 0) return 0;
		if (listIndex + 1 == size) return containerSize;

		return container.getWidgetIndex(widgets.get(listIndex + 1));
	}

	public void remove(final int index) {
		final IsWidget removed = widgets.remove(index);
		removeWidgetFromContainer(removed);
	}

	public void remove(final IsWidget widget) {
		if (!widgets.remove(widget)) return;

		removeWidgetFromContainer(widget);
	}

	private void removeWidgetFromContainer(final IsWidget widget) {
		animationFactory.createHideAnimation(widget.asWidget()).hide(new AnimationCallback() {
			@Override
			public void onComplete() {
				if (widgets.contains(widget)) return;

				container.remove(widget);
			}
		});
	}

	public ComplexPanel getContainningPanel() {
		return container;
	}

	public void clear() {
		widgets.clear();
		container.clear();
	}

	public int getWidgetCount() {
		return widgets.size();
	}

	public int getWidgetIndex(final IsWidget widget) {
		return widgets.indexOf(widget);
	}

	void addToWidgetMapping(final IsWidget widget) {
		widgets.add(getCacheIndex(widget), widget);
	}

	private int getCacheIndex(final IsWidget widget) {
		int containerIndex = container.getWidgetIndex(widget);
		while (containerIndex-- > 0) {
			final Widget beforeWidget = container.getWidget(containerIndex);
			if (widgets.contains(beforeWidget)) return widgets.indexOf(beforeWidget) + 1;
		}

		return 0;
	}

	void removeFromWidgetMapping(final IsWidget widget) {
		widgets.remove(widget);
	}

	private void insertIntoContainer(final IsWidget widget, final int beforeIndex) {
		((InsertPanel.ForIsWidget) container).insert(widget, beforeIndex);
	}
}
