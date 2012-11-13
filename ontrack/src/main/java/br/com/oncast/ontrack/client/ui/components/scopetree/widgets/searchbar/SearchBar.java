package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn.VisibilityChangeListener;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class SearchBar extends Composite implements ActionExecutionListener {

	private static SearchBarUiBinder uiBinder = GWT.create(SearchBarUiBinder.class);

	interface SearchBarUiBinder extends UiBinder<Widget, SearchBar> {}

	@UiField
	SearchScopeFiltrableCommandMenu search;

	@UiField
	HorizontalPanel container;

	@UiField
	Label valueColumnActiveIndicatorLabel;
	@UiField
	Label valueColumnInactiveIndicatorLabel;

	@UiField
	Label effortColumnActiveIndicatorLabel;
	@UiField
	Label effortColumnInactiveIndicatorLabel;

	@UiField
	Label progressColumnActiveIndicatorLabel;
	@UiField
	Label progressColumnInactiveIndicatorLabel;

	@UiField
	Label releaseColumnActiveIndicatorLabel;
	@UiField
	Label releaseColumnInactiveIndicatorLabel;

	private ScopeTreeWidget tree;

	private boolean shouldUpdate = true;

	private final Map<Scope, SearchScopeResultCommandMenuItem> scopeMenuCache;

	private MultipleWidgetsWidthExpandAnimation animation;

	public SearchBar() {
		initWidget(uiBinder.createAndBindUi(this));
		registerScopeTreeColumnVisibilityChangeListeners();
		scopeMenuCache = new HashMap<Scope, SearchScopeResultCommandMenuItem>();
	}

	@UiHandler({ "valueColumnActiveIndicatorLabel", "valueColumnInactiveIndicatorLabel" })
	void onToggleValueClick(final ClickEvent event) {
		ScopeTreeColumn.VALUE.toggle();
	}

	@UiHandler({ "effortColumnActiveIndicatorLabel", "effortColumnInactiveIndicatorLabel" })
	void onToggleEffortClick(final ClickEvent event) {
		ScopeTreeColumn.EFFORT.toggle();
	}

	@UiHandler({ "progressColumnActiveIndicatorLabel", "progressColumnInactiveIndicatorLabel" })
	void onToggleProgressClick(final ClickEvent event) {
		ScopeTreeColumn.PROGRESS.toggle();
	}

	@UiHandler({ "releaseColumnActiveIndicatorLabel", "releaseColumnInactiveIndicatorLabel" })
	void onToggleReleaseClick(final ClickEvent event) {
		ScopeTreeColumn.RELEASE.toggle();
	}

	@UiHandler("search")
	void onSearchFocus(final FocusEvent e) {
		getScopeTreeColumnsExpandAnimation().shrink();
		if (!shouldUpdate) return;

		updateItems();
		shouldUpdate = false;
	}

	@UiHandler("search")
	void onSearchBlur(final BlurEvent e) {
		getScopeTreeColumnsExpandAnimation().expand();
	}

	@UiHandler("search")
	protected void onKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) tree.setFocus(true);
	}

	private MultipleWidgetsWidthExpandAnimation getScopeTreeColumnsExpandAnimation() {
		if (animation == null) animation = new MultipleWidgetsWidthExpandAnimation(300,
				releaseColumnActiveIndicatorLabel,
				releaseColumnInactiveIndicatorLabel,
				valueColumnActiveIndicatorLabel,
				valueColumnInactiveIndicatorLabel,
				effortColumnActiveIndicatorLabel,
				effortColumnInactiveIndicatorLabel,
				progressColumnActiveIndicatorLabel,
				progressColumnInactiveIndicatorLabel);
		return animation;
	}

	private class MultipleWidgetsWidthExpandAnimation extends Animation {

		private final HashMap<Widget, Integer> initialWidthMap;
		private final HashMap<Widget, Boolean> initialVisibilityMap;
		private final ArrayList<Widget> widgetsList;
		private final int duration;
		private boolean expanding;

		public MultipleWidgetsWidthExpandAnimation(final int duration, final Widget... widgets) {
			this.duration = duration;
			initialWidthMap = new HashMap<Widget, Integer>();
			initialVisibilityMap = new HashMap<Widget, Boolean>();
			widgetsList = new ArrayList<Widget>();

			for (final Widget widget : widgets) {
				final boolean visible = widget.isVisible();
				widget.setVisible(true);
				initialWidthMap.put(widget, widget.getOffsetWidth());
				widget.setVisible(visible);

				widgetsList.add(widget);
			}
		}

		@Override
		protected void onUpdate(double progress) {
			if (!expanding) progress = 1 - progress;

			for (final Widget widget : widgetsList) {
				widget.getElement().getStyle().setWidth(initialWidthMap.get(widget) * interpolate(progress), Unit.PX);
			}
		}

		@Override
		protected void onStart() {
			if (!expanding) return;

			for (final Widget widget : widgetsList) {
				widget.setVisible(initialVisibilityMap.get(widget));
			}
		}

		@Override
		protected void onComplete() {
			if (expanding) return;

			for (final Widget widget : widgetsList) {
				widget.getElement().getStyle().setWidth(initialWidthMap.get(widget), Unit.PX);
				widget.setVisible(false);
			}
		}

		public void expand() {
			this.expanding = true;
			this.run(duration);
		}

		public void shrink() {
			for (final Widget widget : widgetsList) {
				initialVisibilityMap.put(widget, widget.isVisible());
			}

			this.expanding = false;
			this.run(duration);
		}

	}

	private void registerScopeTreeColumnVisibilityChangeListeners() {
		ScopeTreeColumn.RELEASE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				releaseColumnActiveIndicatorLabel.setVisible(isVisible);
				releaseColumnInactiveIndicatorLabel.setVisible(!isVisible);
			}

		});

		ScopeTreeColumn.PROGRESS.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				progressColumnActiveIndicatorLabel.setVisible(isVisible);
				progressColumnInactiveIndicatorLabel.setVisible(!isVisible);
			}
		});
		ScopeTreeColumn.EFFORT.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				effortColumnActiveIndicatorLabel.setVisible(isVisible);
				effortColumnInactiveIndicatorLabel.setVisible(!isVisible);
			}
		});
		ScopeTreeColumn.VALUE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				valueColumnActiveIndicatorLabel.setVisible(isVisible);
				valueColumnInactiveIndicatorLabel.setVisible(!isVisible);
			}
		});
	}

	public void setTree(final ScopeTree scopeTree) {
		tree = (ScopeTreeWidget) scopeTree.asWidget();
	}

	private void updateItems() {
		final List<Scope> allScopes = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext().getProjectScope()
				.getAllDescendantScopes();
		search.setItems(asCommandMenuItens(allScopes));
	}

	private List<CommandMenuItem> asCommandMenuItens(final List<Scope> scopeList) {
		final List<CommandMenuItem> menuItens = new ArrayList<CommandMenuItem>();
		for (final Scope scope : scopeList) {
			if (!scopeMenuCache.containsKey(scope)) scopeMenuCache.put(scope, new SearchScopeResultCommandMenuItem(scope, new Command() {

				@Override
				public void execute() {
					selectItem(tree, scope);
				}
			}));
			menuItens.add(scopeMenuCache.get(scope));
		}
		return menuItens;
	}

	private static void selectItem(final ScopeTreeWidget treeWidget, final Scope scope) {
		final ScopeTreeItem item = treeWidget.findAndMountScopeTreeItem(scope);
		final Tree tree = item.getTree();
		tree.setSelectedItem(null, false);
		item.setHierarchicalState(true);
		tree.setSelectedItem(item);
	}

	public void focus() {
		search.focus();
	}

	public void setActionExecutionRequestHandler(final ActionExecutionService actionExecutionService) {
		actionExecutionService.addActionExecutionListener(this);
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
		if (action instanceof ScopeUpdateAction || action instanceof ScopeInsertAction || action instanceof ScopeRemoveAction) shouldUpdate = true;
	}
}
