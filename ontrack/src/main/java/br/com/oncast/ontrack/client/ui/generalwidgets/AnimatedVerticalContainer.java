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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnimatedVerticalContainer extends Composite {

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

	protected VerticalPanel container;

	protected List<IsWidget> widgets = new ArrayList<IsWidget>();

	public AnimatedVerticalContainer() {
		this(new VerticalPanel());
	}

	public AnimatedVerticalContainer(final VerticalPanel verticalPanel) {
		this(verticalPanel, DEFAULT_ANIMATION_FACTORY);
	}

	public AnimatedVerticalContainer(final AnimationFactory animationFactory) {
		this(new VerticalPanel(), animationFactory);
	}

	public AnimatedVerticalContainer(final VerticalPanel verticalPanel, final AnimationFactory animationFactory) {
		initWidget(container = verticalPanel);
		this.animationFactory = animationFactory;
	}

	public void insert(final IsWidget widget, final int index) {
		widgets.add(index, widget);
		container.insert(widget, getContainerIndex(widget));
		animationFactory.createShowAnimation(widget.asWidget()).show();
	}

	public void move(final IsWidget widget, final int index) {
		if (widgets.indexOf(widget) == index) return;

		widgets.remove(widget);
		container.remove(widget);
		widgets.add(index, widget);
		container.insert(widget, getContainerIndex(widget));
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

	public Widget getWidget(final int index) {
		return widgets.get(index).asWidget();
	}

	public CellPanel getCellPanel() {
		return container;
	}
}
