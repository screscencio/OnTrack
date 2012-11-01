package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.SlideAndFadeAnimation;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public abstract class AnimatedCellContainer<T extends CellPanel> extends Composite {

	private final static AnimationFactory DEFAULT_ANIMATION_FACTORY = new AnimationFactory() {

		@Override
		public ShowAnimation createShowAnimation(final Widget widget) {
			return new SlideAndFadeAnimation(widget.asWidget());
		}

		@Override
		public HideAnimation createHideAnimation(final Widget widget) {
			return new SlideAndFadeAnimation(widget.asWidget());
		}
	};

	private final AnimationFactory animationFactory;

	protected List<IsWidget> widgets = new ArrayList<IsWidget>();

	protected final T container;

	public AnimatedCellContainer(final T container) {
		this(container, DEFAULT_ANIMATION_FACTORY);
	}

	public AnimatedCellContainer(final T container, final AnimationFactory animationFactory) {
		initWidget(this.container = container);
		this.animationFactory = animationFactory;
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
		if (widgets.indexOf(widget) == index) return;

		widgets.remove(widget);
		container.remove(widget);
		widgets.add(index, widget);
		insertIntoContainer(widget, getContainerIndex(widget));
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

	public CellPanel getCellPanel() {
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

	protected abstract void insertIntoContainer(final IsWidget widget, final int beforeIndex);
}
