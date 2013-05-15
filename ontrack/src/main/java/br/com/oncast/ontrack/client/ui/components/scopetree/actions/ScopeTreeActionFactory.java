package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareDueDateAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TagCreateAction;
import br.com.oncast.ontrack.shared.model.action.TagRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

// TODO ++Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeActionFactory {

	private static final ScopeTreeAction IGNORE_ACTION = new ScopeTreeAction() {
		@Override
		public void execute(final ProjectContext context, final ActionContext actionContext, final boolean isUserInteraction)
				throws ModelBeanNotFoundException {}
	};

	private final ScopeTreeWidget tree;

	public ScopeTreeActionFactory(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	public ScopeTreeAction createEquivalentActionFor(final ModelAction action) throws ScopeNotFoundException {

		if (action instanceof ScopeMoveAction) return new ScopeTreeMoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeInsertSiblingAction) return new ScopeTreeInsertSiblingAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertSiblingUpRollbackAction) return new ScopeTreeRemoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeInsertSiblingDownRollbackAction) return new ScopeTreeRemoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeInsertChildAction) return new ScopeTreeInsertChildAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertChildRollbackAction) return new ScopeTreeRemoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeInsertParentAction) return new ScopeTreeInsertParentAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertParentRollbackAction) return new ScopeTreeParentRollbackAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeRemoveAction) return new ScopeTreeRemoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeRemoveRollbackAction) return new ScopeTreeRemoveRollbackAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeUpdateAction || action instanceof ScopeBindReleaseAction) return new ScopeTreeUpdateAction(tree, action);
		else if (action instanceof ReleaseAction) return new ScopeTreeReleaseAction(tree);
		else if (action instanceof ScopeDeclareProgressAction) return new ScopeTreeUpdateAction(tree, action);
		else if (action instanceof ScopeDeclareEffortAction) return new ScopeTreeUpdateAction(tree, action);
		else if (action instanceof ScopeDeclareValueAction) return new ScopeTreeUpdateAction(tree, action);
		else if (action instanceof ReleaseRenameAction) return new ScopeTreeReleaseUpdateAction(tree, (ReleaseAction) action);
		else if (action instanceof KanbanAction) return new ScopeTreeUpdateProgressAction(tree, (KanbanAction) action);
		else if (action instanceof ScopeAddTagAssociationAction || action instanceof ScopeRemoveTagAssociationAction) return new ScopeTreeTagAssociationAction(
				tree, action);
		else if (action instanceof TagRemoveAction) return new ScopeTreeTagRemoveUpdateAction(tree, (TagRemoveAction) action);
		else if (action instanceof TagCreateAction) return new ScopeTreeTagUpdateAction(tree, action);
		else if (action instanceof AnnotationCreateAction) return new ScopeTreeUpdateAction(tree, action);
		else if (action instanceof ScopeDeclareDueDateAction) return IGNORE_ACTION;

		throw new RuntimeException("It was not possible to find the desired action.");
	}
}
