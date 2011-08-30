package br.com.oncast.ontrack.shared.model.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertChildActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertChildActionEntity.class)
public class ScopeInsertChildAction implements ScopeInsertAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newScopeId")
	private UUID newScopeId;

	@ConversionAlias("pattern")
	private String pattern;

	@ConversionAlias("subActionList")
	private List<ModelAction> subActionList;

	public ScopeInsertChildAction(final UUID referenceId, final String pattern) {
		this.referenceId = referenceId;
		this.pattern = pattern;
		this.newScopeId = new UUID();
		this.subActionList = new ArrayList<ModelAction>();
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertChildAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		if (selectedScope.isLeaf()) subActionRollbackList.add(removeDeclaredProgress(selectedScope, context));

		selectedScope.add(new Scope("", newScopeId));

		new ScopeUpdateAction(newScopeId, pattern).execute(context);
		// TODO Analyze the possibility of substituting the ScopeRemoveAction with a more specialized rollback action. Multiple undo and redo, starting from
		// a ScopeInsertChildAction that acts upon a scope with 'Done' progress.
		return new ScopeRemoveAction(newScopeId, subActionRollbackList);
	}

	private ModelAction removeDeclaredProgress(final Scope scope, final ProjectContext context) throws UnableToCompleteActionException {
		final ScopeDeclareProgressAction declareProgressAction = new ScopeDeclareProgressAction(scope.getId(), "");
		subActionList.add(declareProgressAction);

		return declareProgressAction.execute(context);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}
}
