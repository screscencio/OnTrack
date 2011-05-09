package br.com.oncast.ontrack.client.ui.component.scopetree;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {

				final ScopeTreeItem selected = tree.getSelected();
				if (selected == null) return;

				if (event.isControlKeyDown()) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {

						// - Executa ação sobre o escopo
						// -- Caso resultado indique sucesso
						// --- Recupera ação de arvore equivalente
						// --- Executa ação sobre a arvore
						// ---- Caso ação sobre arvore tenha sucesso empilha na pilha de controle e envia ao servidor
						// ---- Caso ação sobre arvore tenha falha
						// ----- executa rollback sobre o escopo
						// ----- exibe mensagem na tela
						// -- Caso resultado indique falha exibe mensagem na tela

						new MoveDownScopeAction<ScopeTreeItem>(selected).execute();
						new MoveDownScopeAction<Scope>((Scope) selected.getUserObject()).execute();

						tree.setSelected(selected);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
						new MoveUpScopeAction<ScopeTreeItem>(selected).execute();
						new MoveUpScopeAction<Scope>((Scope) selected.getUserObject()).execute();

						tree.setSelected(selected);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
						new MoveLeftScopeAction<ScopeTreeItem>(selected).execute();
						new MoveLeftScopeAction<Scope>((Scope) selected.getUserObject()).execute();

						tree.setSelected(selected);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
						new MoveRightScopeAction<ScopeTreeItem>(selected).execute();
						new MoveRightScopeAction<Scope>((Scope) selected.getUserObject()).execute();

						tree.setSelected(selected);
					}
				}
			}
		});
	}

	public void setScope(final Scope scope) {
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(scope);

		tree.add(rootItem);
		tree.setSelected(rootItem);
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}
}
