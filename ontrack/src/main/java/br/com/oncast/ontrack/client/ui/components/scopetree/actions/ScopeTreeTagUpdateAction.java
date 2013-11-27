package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import java.util.List;

public class ScopeTreeTagUpdateAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ModelAction action;

	public ScopeTreeTagUpdateAction(final ScopeTreeWidget tree, final ModelAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ModelBeanNotFoundException {
		final List<TagAssociationMetadata> metadataList = context.getAllMetadata(TagAssociationMetadata.getType());

		for (final TagAssociationMetadata metadata : metadataList) {
			if (!metadata.getTag().equals(action.getReferenceId())) continue;

			final Scope scope = (Scope) metadata.getSubject();
			final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
			treeItem.getScopeTreeItemWidget().updateTagsDisplay();
		}
	}
}
