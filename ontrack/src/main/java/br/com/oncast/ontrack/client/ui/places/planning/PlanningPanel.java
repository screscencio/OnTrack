package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
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
		final int menuHeight = releaseScroll.getElement().getOffsetHeight();
		final int menuBottom = menuTop + menuHeight;

		final Element widgetElement = widget.getElement();
		final int itemTop = getOffisetTop(widget, releaseScroll);
		final int itemHeight = widgetElement.getOffsetHeight();
		final int itemBottom = itemTop + itemHeight;

		int position = Integer.MIN_VALUE;
		if (itemTop < menuTop) position = itemTop;
		else if (itemBottom > menuBottom) position = itemTop - menuHeight + itemHeight;
		if (position != Integer.MIN_VALUE) {
			final int delta = (position > 0 ? position - 2 : position + 2) - menuTop;
			new Animation() {
				@Override
				protected void onUpdate(final double progress) {
					releaseScroll.setVerticalScrollPosition((int) (menuTop + delta * progress));
				}

				@Override
				protected void onComplete() {
					releaseScroll.setVerticalScrollPosition(menuTop + delta);
				}
			}.run(500);
		}
	}

	private int getOffisetTop(final Widget widget, final ScrollPanel scroll) {
		if (widget.getParent() == null) throw new RuntimeException("Widget should be inside the scroll panel");

		if (scroll.equals(widget.getParent())) return widget.getElement().getOffsetTop();

		return getOffisetTop(widget.getParent(), scroll) + widget.getElement().getOffsetTop();
	}
}