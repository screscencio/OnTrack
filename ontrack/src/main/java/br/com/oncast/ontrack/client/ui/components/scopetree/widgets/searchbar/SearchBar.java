package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ActivateTagFilterEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ActivateTagFilterEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ClearTagFilterEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ClearTagFilterEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn.VisibilityChangeListener;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SearchBar extends Composite implements ActionExecutionListener {

	private static SearchBarUiBinder uiBinder = GWT.create(SearchBarUiBinder.class);

	interface SearchBarUiBinder extends UiBinder<Widget, SearchBar> {}

	@UiField
	FocusPanel root;

	@UiField
	SearchScopeFiltrableCommandMenu search;

	@UiField
	HorizontalPanel container;

	@UiField
	SimplePanel columnContainer;

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

	private boolean mouseOver;

	private boolean focus;

	private UUID filterTagId;

	final Set<HandlerRegistration> handlerRegistrations = new HashSet<HandlerRegistration>();

	public SearchBar() {
		initWidget(uiBinder.createAndBindUi(this));
		registerScopeTreeColumnVisibilityChangeListeners();
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
		focus = true;
		updateContainerVisibility();
	}

	@UiHandler("search")
	void onSearchBlur(final BlurEvent e) {
		focus = false;
		updateContainerVisibility();
	}

	@UiHandler("root")
	void onMouseOver(final MouseOverEvent e) {
		mouseOver = true;
		updateContainerVisibility();
	}

	@UiHandler("root")
	void onMouseOut(final MouseOutEvent e) {
		mouseOver = false;
		updateContainerVisibility();
	}

	@Override
	protected void onLoad() {
		if (!handlerRegistrations.isEmpty()) return;
		handlerRegistrations.add(ClientServices.get().eventBus().addHandler(ActivateTagFilterEvent.getType(), new ActivateTagFilterEventHandler() {
			@Override
			public void onFilterByTagRequested(final UUID tagId) {
				if (filterTagId == tagId) return;
				shouldUpdate = true;
				filterTagId = tagId;
			}
		}));

		handlerRegistrations.add(ClientServices.get().eventBus().addHandler(ClearTagFilterEvent.getType(), new ClearTagFilterEventHandler() {
			@Override
			public void onClearTagFilterRequested() {
				if (filterTagId == null) return;
				shouldUpdate = true;
				filterTagId = null;
			}
		}));
	}

	@Override
	protected void onUnload() {
		for (final HandlerRegistration reg : handlerRegistrations) {
			reg.removeHandler();
		}
	}

	private void updateContainerVisibility() {
		if (focus) {
			search.setVisible(true);
			JQuery.jquery(columnContainer).stop(true).slideLeftHide(400, new AnimationCallback() {

				@Override
				public void onComplete() {}
			});
			if (!shouldUpdate) return;

			updateItems();
			shouldUpdate = false;
		}
		else {
			if (mouseOver) return;
			JQuery.jquery(columnContainer).slideRightShow(300, new AnimationCallback() {

				@Override
				public void onComplete() {
					search.clear();
				}
			});
		}
	}

	@UiHandler("search")
	protected void onKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) tree.setFocus(true);
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
		final ProjectContext context = ClientServices.getCurrentProjectContext();
		final List<Scope> scopes;
		if (filterTagId != null) scopes = getScopesWithTag(context, filterTagId);
		else scopes = context.getProjectScope().getAllDescendantScopes();
		search.setItems(asCommandMenuItens(scopes));
	}

	private List<Scope> getScopesWithTag(final ProjectContext context, final UUID filterTagId) {
		final List<Scope> list = new ArrayList<Scope>();
		for (final TagAssociationMetadata metadata : context.<TagAssociationMetadata> getAllMetadata(MetadataType.TAG)) {
			if (metadata.getTag().getId().equals(filterTagId)) list.add((Scope) metadata.getSubject());
		}
		return list;
	}

	private List<CommandMenuItem> asCommandMenuItens(final List<Scope> scopeList) {
		final List<CommandMenuItem> menuItens = new ArrayList<CommandMenuItem>();
		for (final Scope scope : scopeList) {
			menuItens.add(new SearchScopeResultCommandMenuItem(scope, new Command() {
				@Override
				public void execute() {
					selectItem(tree, scope);
				}
			}));
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
			final ActionExecutionContext executionContext, final boolean isUserAction) {
		if (action instanceof ScopeUpdateAction || action instanceof ScopeInsertAction || action instanceof ScopeRemoveAction
				|| action instanceof ScopeMoveAction) shouldUpdate = true;
	}
}
