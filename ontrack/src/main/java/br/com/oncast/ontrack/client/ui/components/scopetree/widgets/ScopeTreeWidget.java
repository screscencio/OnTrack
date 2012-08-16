package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailChangeEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailChangeEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemBindReleaseEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemBindReleaseEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareEffortEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareEffortEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareProgressEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareProgressEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareValueEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareValueEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionStartEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionStartEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.HasInstructions;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeItemAdoptionListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ScopeTreeWidget extends Composite implements HasInstructions, HasFocusHandlers {

	private static ScopeTreeWidgetUiBinder uiBinder = GWT.create(ScopeTreeWidgetUiBinder.class);

	interface ScopeTreeWidgetUiBinder extends UiBinder<Widget, ScopeTreeWidget> {}

	/**
	 * A ClientBundle that provides images for this widget.
	 */
	protected interface Resources extends Tree.Resources {

		/**
		 * An image indicating a closed branch.
		 */
		@Override
		ImageResource treeClosed();

		/**
		 * An image indicating a leaf.
		 */
		@Override
		ImageResource treeLeaf();

		/**
		 * An image indicating an open branch.
		 */
		@Override
		ImageResource treeOpen();
	}

	interface ScopeTreeWidgetStyle extends CssResource {
		String instructionLabel();
	}

	@UiField
	protected ScopeTreeWidgetStyle style;

	@UiField
	protected Tree tree;

	@UiField
	protected Label noScopeLabel;

	@UiField
	protected HTMLPanel instructionPanel;

	private final Map<UUID, ScopeTreeItem> itemMapCache = new HashMap<UUID, ScopeTreeItem>();

	private ScopeTreeItem lastTreeItem = null;

	private boolean disableSelectionEvent = false;

	@UiFactory
	protected Tree createTree() {
		return new Tree((Resources) GWT.create(Resources.class), true);
	}

	public ScopeTreeWidget(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		initWidget(uiBinder.createAndBindUi(this));

		tree.addHandler(new ScopeTreeItemBindReleaseEventHandler() {
			@Override
			public void onBindReleaseRequest(final UUID scopeId, final String releaseDescription) {
				interactionHandler.onBindReleaseRequest(scopeId, releaseDescription);
			}
		}, ScopeTreeItemBindReleaseEvent.getType());

		tree.addHandler(new ScopeTreeItemDeclareEffortEventHandler() {

			@Override
			public void onDeclareEffortRequest(final UUID scopeId, final String effortDescription) {
				interactionHandler.onDeclareEffortRequest(scopeId, effortDescription);
			}

		}, ScopeTreeItemDeclareEffortEvent.getType());

		tree.addHandler(new ScopeTreeItemDeclareValueEventHandler() {

			@Override
			public void onDeclareValueRequest(final UUID scopeId, final String valueDescription) {
				interactionHandler.onDeclareValueRequest(scopeId, valueDescription);
			}
		}, ScopeTreeItemDeclareValueEvent.getType());

		tree.addHandler(new ScopeTreeItemDeclareProgressEventHandler() {

			@Override
			public void onDeclareProgressRequest(final UUID scopeId, final String progressDescription) {
				interactionHandler.onDeclareProgressRequest(scopeId, progressDescription);
			}
		}, ScopeTreeItemDeclareProgressEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionStartEventHandler() {

			@Override
			public void onItemEditionStart(final ScopeTreeItem item) {
				interactionHandler.onItemEditionStart(item);
			}
		}, ScopeTreeItemEditionStartEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionEndEventHandler() {

			@Override
			public void onItemEditionEnd(final ScopeTreeItem item, final String value) {
				interactionHandler.onItemEditionEnd(item, value);
			}
		}, ScopeTreeItemEditionEndEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionCancelEventHandler() {

			@Override
			public void onItemEditionCancel() {
				interactionHandler.onItemEditionCancel();
			}
		}, ScopeTreeItemEditionCancelEvent.getType());

		tree.setTreeItemAdoptionListener(new TreeItemAdoptionListener() {

			@Override
			public void onTreeItemAdopted(final TreeItem treeItem) {
				final ScopeTreeItem scopeTreeItem = ((ScopeTreeItem) treeItem);
				addToCache(scopeTreeItem);
				updateNoScopeLabelVisibility();
			}

			@Override
			public void onTreeItemAbandoned(final TreeItem treeItem) {
				final ScopeTreeItem scopeTreeItem = ((ScopeTreeItem) treeItem);
				removeFromCache(scopeTreeItem);
				updateNoScopeLabelVisibility();
			}

			private void addToCache(final ScopeTreeItem scopeTreeItem) {
				final Scope scope = scopeTreeItem.getReferencedScope();
				if (itemMapCache.containsKey(scope.getId())) throw new RuntimeException(
						"You are trying to Add a widget for Scope '" + scope.getDescription() + "' that the ScopeTreeWidget already has");

				itemMapCache.put(scope.getId(), scopeTreeItem);
			}

			private void removeFromCache(final ScopeTreeItem scopeTreeItem) {
				final Scope scope = scopeTreeItem.getReferencedScope();
				if (!itemMapCache.containsKey(scope.getId())) throw new RuntimeException("You are trying to Remove a widget for Scope '"
						+ scope.getDescription() + "' that is not present in the ScopeTreeWidget");

				itemMapCache.remove(scope.getId());
			}
		});

		final EventBus eventBus = ClientServiceProvider.getInstance().getEventBus();
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(final SelectionEvent<TreeItem> event) {
				if (disableSelectionEvent) return;
				removeBorderFromLastItem();

				final ScopeTreeItem selectedItem = (ScopeTreeItem) event.getSelectedItem();
				if (selectedItem.isRoot()) return;

				addBorderToSelectedItem();
				eventBus.fireEventFromSource(new ScopeSelectionEvent(selectedItem.getReferencedScope()), ScopeTreeWidget.this);
			}
		});

		eventBus.addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {

			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				if (event.getSource() instanceof ScopeTreeWidget) return;

				final Scope scope = event.getTargetScope();
				disableSelectionEvent = true;

				try {
					final ScopeTreeItem item = findScopeTreeItem(scope);

					item.setHierarchicalState(true);

					tree.setSelectedItem(item, false);
					removeBorderFromLastItem();
					addBorderToSelectedItem();
				}
				catch (final ScopeNotFoundException e) {
					throw new RuntimeException("Scope '" + scope.getDescription() + "' not found in ScopeTreeWidget", e);
				}
				finally {
					disableSelectionEvent = false;
				}
			}

		});
		eventBus.addHandler(ScopeDetailChangeEvent.getType(), new ScopeDetailChangeEventHandler() {
			@Override
			public void onScopeDetailChange(final ScopeDetailChangeEvent event) {
				final Scope scope = event.getTargetScope();

				try {
					final ScopeTreeItem item = findScopeTreeItem(scope);
					item.showDetailsIcon(event.hasDetails());
				}
				catch (final ScopeNotFoundException e) {
					throw new RuntimeException("Scope '" + scope.getDescription() + "' not found in ScopeTreeWidget", e);
				}
			}
		});
		tree.addOpenHandler(new OpenHandler<TreeItem>() {
			@Override
			public void onOpen(final OpenEvent<TreeItem> event) {
				final ScopeTreeItem item = (ScopeTreeItem) event.getTarget();
				mountTwoLevels(item);
				tree.setSelectedItem(null, false);
				tree.setSelectedItem(item, false);
			}

		});
	}

	public void add(final ScopeTreeItem item) {
		tree.addItem(item);
	}

	public void add(final int beforeIndex, final ScopeTreeItem item) {
		tree.insertItem(beforeIndex, item.asTreeItem());
	}

	public void remove(final ScopeTreeItem item) {
		tree.removeItem(item);
	}

	public void clear() {
		tree.clear();
	}

	public ScopeTreeItem getSelectedItem() {
		return (ScopeTreeItem) tree.getSelectedItem();
	}

	public void setSelectedItem(final ScopeTreeItem selected) {
		tree.setSelectedItem(selected);
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
		if (tree.getSelectedItem() == null && getItemCount() > 0) setSelectedItem(getItem(0));
	}

	public ScopeTreeItem getItem(final int index) {
		return (ScopeTreeItem) tree.getItem(index);
	}

	public int getItemCount() {
		return tree.getItemCount();
	}

	// TODO+++ improve performance
	public ScopeTreeItem findScopeTreeItem(final Scope scope) throws ScopeNotFoundException {
		final UUID scopeId = scope.getId();

		final ScopeTreeItem scopeTreeItem = itemMapCache.get(scopeId);
		if (scopeTreeItem != null) return scopeTreeItem;

		if (scope.isRoot()) throw new ScopeNotFoundException("No tree item were found for the scope.");

		mountTwoLevels(findScopeTreeItem(scope.getParent()));

		if (!itemMapCache.containsKey(scopeId)) throw new ScopeNotFoundException("No tree item were found for the scope.");

		return itemMapCache.get(scopeId);
	}

	@Override
	public HandlerRegistration addFocusHandler(final FocusHandler handler) {
		return tree.addFocusHandler(handler);
	}

	private void updateNoScopeLabelVisibility() {
		noScopeLabel.setVisible(itemMapCache.size() <= 1);
	}

	private void removeBorderFromLastItem() {
		if (lastTreeItem != null) {
			lastTreeItem.removeStyleName("ScopeTreeItem-selected");
			lastTreeItem = null;
		}
	}

	private void addBorderToSelectedItem() {
		lastTreeItem = (ScopeTreeItem) tree.getSelectedItem();
		lastTreeItem.addStyleName("ScopeTreeItem-selected");
	}

	@Override
	public void addInstruction(final Label instruction) {
		instruction.addStyleName(style.instructionLabel());
		instructionPanel.add(instruction);
	}

	@Override
	public void clearInstructions() {
		instructionPanel.clear();
	}

	private void mountTwoLevels(final ScopeTreeItem item) {
		final boolean wasDisabledBefore = disableSelectionEvent;
		disableSelectionEvent = true;
		item.mountTwoLevels();
		disableSelectionEvent = wasDisabledBefore;
	}

}
