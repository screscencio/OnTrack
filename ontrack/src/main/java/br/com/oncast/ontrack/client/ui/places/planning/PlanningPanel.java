package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanningPanel extends Composite implements PlanningView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, PlanningPanel> {}

	@UiField
	protected ScrollPanel releaseScroll;

	@UiField
	protected ReleasePanel releasePanel;

	@UiField
	protected ScopeTree scopeTree;

	@UiField
	protected ApplicationMenu applicationMenu;

	@UiField
	protected Anchor exportMapLink;

	private final ScrollAnimation animation = new ScrollAnimation();

	@UiFactory
	protected ScrollPanel createReleaseScrollPanel() {
		final ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.getElement().setAttribute("style", "");
		return scrollPanel;
	}

	public PlanningPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setExporterPath(final String href) {
		exportMapLink.setHref(href);
	}

	@Override
	public ScopeTree getScopeTree() {
		return scopeTree;
	}

	@Override
	public ReleasePanel getReleasePanel() {
		return releasePanel;
	}

	@Override
	public ApplicationMenu getApplicationMenu() {
		return applicationMenu;
	}

	@Override
	// FIXME Mats review this method
	public void ensureWidgetIsVisible(final IsWidget isWidget) {
		final Widget widget = isWidget.asWidget();

		final int menuTop = releaseScroll.getVerticalScrollPosition();
		final int menuHeight = releaseScroll.getElement().getClientHeight();
		final int menuBottom = menuTop + menuHeight;

		final Element widgetElement = widget.getElement();
		final int itemTop = getOffisetTop(widget, releaseScroll);
		final int itemHeight = widgetElement.getOffsetHeight();
		final int itemBottom = itemTop + itemHeight;

		if (itemTop < menuTop) animation.scroll(menuTop, itemTop, 500);
		else if (itemBottom > menuBottom) animation.scroll(menuTop, itemTop - menuHeight + itemHeight, 500);
	}

	private int getOffisetTop(final Widget widget, final ScrollPanel scrollPanel) {
		final Widget parent = widget.getParent();
		if (parent == null) throw new RuntimeException("Widget should be inside the scroll panel");

		if (parent == scrollPanel) return widget.getElement().getOffsetTop();

		return getOffisetTop(parent, scrollPanel) + widget.getElement().getOffsetTop();
	}

	private class ScrollAnimation extends Animation {
		private int endPosition;
		private int startPosition;

		@Override
		protected void onComplete() {
			releaseScroll.setVerticalScrollPosition(endPosition);
		}

		@Override
		protected void onUpdate(final double progress) {
			final double delta = (endPosition - startPosition) * progress;
			final int newPosition = (int) (startPosition + delta);
			releaseScroll.setVerticalScrollPosition(newPosition);
		}

		void scroll(final int startPosition, final int endPosition, final int duration) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			if (duration == 0) {
				onComplete();
				return;
			}
			run(duration);
		}
	}
}