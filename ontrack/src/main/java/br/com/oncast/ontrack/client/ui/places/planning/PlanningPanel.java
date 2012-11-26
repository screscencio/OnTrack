package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.footerbar.FooterBar;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseScopeWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ScopeWidgetDropController;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar.SearchBar;
import br.com.oncast.ontrack.client.ui.generalwidgets.DraggableMembersListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.client.ui.places.planning.dnd.UserAssociationDragHandler;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanningPanel extends Composite implements PlanningView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, PlanningPanel> {}

	interface PlanningPanelStyle extends CssResource {
		String showReleaseIcon();

		String mainContainerExpanded();
	}

	@UiField
	protected PlanningPanelStyle style;

	@UiField
	protected ScrollPanel releaseScroll;

	@UiField(provided = true)
	protected ReleasePanel releasePanel;

	@UiField
	protected ScopeTree scopeTree;

	@UiField
	protected SearchBar searchBar;

	@UiField
	protected ApplicationMenuAndWidgetContainer rootPanel;

	@UiField
	protected FocusPanel toggleReleaseBtn;

	@UiField
	FooterBar footerBar;

	@UiField
	Panel mainContainer;

	@UiField(provided = true)
	DraggableMembersListWidget members;

	private final ScrollAnimation animation = new ScrollAnimation();

	@UiFactory
	protected ScrollPanel createReleaseScrollPanel() {
		final ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.getElement().setAttribute("style", "");
		return scrollPanel;
	}

	public PlanningPanel() {
		final DragAndDropManager userDragAndDropManager = new DragAndDropManager();
		userDragAndDropManager.configureBoundaryPanel(RootPanel.get());
		userDragAndDropManager.addDragHandler(new UserAssociationDragHandler());
		final DropControllerFactory userDropControllerFactory = new DropControllerFactory() {
			@Override
			public DropController create(final Widget panel) {
				return new ScopeWidgetDropController((ReleaseScopeWidget) panel);
			}
		};
		releasePanel = new ReleasePanel(userDragAndDropManager, userDropControllerFactory);
		members = new DraggableMembersListWidget(userDragAndDropManager);
		initWidget(uiBinder.createAndBindUi(this));

		searchBar.setTree(scopeTree);
	}

	@UiHandler("toggleReleaseBtn")
	protected void onShowReleasePanelClick(final ClickEvent e) {
		toggleReleasePanel();
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
		return rootPanel.getMenu();
	}

	@Override
	public SearchBar getSearchBar() {
		return searchBar;
	}

	@Override
	public Widget getAlertingMenu() {
		return rootPanel.getContentPanelWidget();
	}

	/**
	 * Makes the widget visible by moving the scroll position<br>
	 * If the widget is already completely visible it does nothing.
	 * If the widget is higher than the scroll then it scrolls to top of the widget letting the bottom part hidden.
	 * If the widget is occupying all the scroll space then it does nothing.
	 * @return true if the widget fits the scroll been able to be completely visible false otherwise.
	 */
	@Override
	public boolean ensureWidgetIsVisible(final IsWidget isWidget) {
		final Widget widget = isWidget.asWidget();

		final int menuTop = releaseScroll.getVerticalScrollPosition();
		final int menuHeight = releaseScroll.getElement().getClientHeight();
		final int menuBottom = menuTop + menuHeight;

		final Element widgetElement = widget.getElement();
		final int itemTop = getOffisetTop(widgetElement, releaseScroll.getElement());
		final int itemHeight = widgetElement.getParentElement().getOffsetHeight();
		final int itemBottom = itemTop + itemHeight;

		if (itemTop < menuTop && itemBottom > menuBottom) return false;

		if (itemTop < menuTop || itemHeight > menuHeight) animation.scroll(menuTop, itemTop - 5, 500);
		else if (itemBottom > menuBottom) animation.scroll(menuTop, itemTop - menuHeight + itemHeight, 500);

		return itemHeight <= menuHeight;
	}

	@Override
	public void toggleReleasePanel() {
		final boolean wasVisible = releasePanel.isVisible();
		releasePanel.setVisible(!wasVisible);
		members.setVisible(!wasVisible);
		toggleReleaseBtn.setStyleName(style.showReleaseIcon(), wasVisible);
		mainContainer.setStyleName(style.mainContainerExpanded(), wasVisible);

	}

	private int getOffisetTop(final Element widget, final Element scrollPanel) {
		final Element parent = widget.getOffsetParent();
		if (parent == null) return 0;

		if (parent == scrollPanel) return widget.getOffsetTop();

		return getOffisetTop(parent, scrollPanel) + widget.getOffsetTop();
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
			final double delta = (endPosition - startPosition) * interpolate(progress);
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