package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.SearchScopeFiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.SearchScopeFiltrableCommandMenu.FiltrableCommandMenuListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Tree;

public class ScopeTreeSearchWidget {

	private ScopeTreeSearchWidget() {}

	public static void show(final ScopeTreeWidget tree, final List<Scope> scopeList) {
		final SearchScopeFiltrableCommandMenu searchMenu = new SearchScopeFiltrableCommandMenu(700, 400);

		registerHandlers(searchMenu, tree);

		searchMenu.setOrderedItens(asCommandMenuItens(tree, scopeList));

		asPoupup(searchMenu).pop();
	}

	private static void registerHandlers(final SearchScopeFiltrableCommandMenu searchMenu, final ScopeTreeWidget tree) {
		final HandlerRegistration treeFocusHandler = tree.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				searchMenu.focus();
			}
		});

		final Scope previouslySelectedScope = tree.getSelectedItem().getReferencedScope();
		searchMenu.setListener(new FiltrableCommandMenuListener() {
			@Override
			public void onItemSelected(final CommandMenuItem selectedItem) {
				selectedItem.executeCommand();
			}

			@Override
			public void onCancel() {
				selectItem(tree, previouslySelectedScope);
			}
		});

		searchMenu.addCloseHandler(new CloseHandler<SearchScopeFiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<SearchScopeFiltrableCommandMenu> event) {
				treeFocusHandler.removeHandler();
			}

		});
	}

	private static List<CommandMenuItem> asCommandMenuItens(final ScopeTreeWidget tree, final List<Scope> scopeList) {
		final List<CommandMenuItem> menuItens = new ArrayList<CommandMenuItem>();
		for (final Scope item : scopeList) {
			menuItens.add(new SimpleCommandMenuItem(item.getDescription(), new Command() {
				@Override
				public void execute() {
					selectItem(tree, item);
				}
			}));
		}
		return menuItens;
	}

	private static PopupConfig asPoupup(final SearchScopeFiltrableCommandMenu searchMenu) {
		searchMenu.getElement().getStyle().setTop(10, Unit.PX);
		searchMenu.getElement().getStyle().setRight(10, Unit.PX);
		return PopupConfig.configPopup().popup(searchMenu);
	}

	private static void selectItem(final ScopeTreeWidget treeWidget, final Scope scope) {
		try {
			final ScopeTreeItem item = treeWidget.findScopeTreeItem(scope);
			final Tree tree = item.getTree();
			tree.setSelectedItem(null, false);
			tree.setSelectedItem(item, false);
			tree.ensureSelectedItemVisible();
			tree.setSelectedItem(item);
		}
		catch (final ScopeNotFoundException e) {
			e.printStackTrace();
			ClientNotificationService.showError(e.getMessage());
		}
	}

}
