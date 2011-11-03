package br.com.oncast.ontrack.shared.model.actions;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;

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
	@Element
	private UUID referenceId;

	@ConversionAlias("newScopeId")
	@Element
	private UUID newScopeId;

	@ConversionAlias("scopeUpdateAction")
	@Element
	private ScopeUpdateAction scopeUpdateAction;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertChildAction() {}

	public ScopeInsertChildAction(final UUID parentScopeId, final String pattern) {
		this(parentScopeId, new UUID(), pattern);
	}

	public ScopeInsertChildAction(final UUID parentScopeId, final UUID newScopeId, final String pattern) {
		this.referenceId = parentScopeId;
		this.newScopeId = newScopeId;
		scopeUpdateAction = new ScopeUpdateAction(newScopeId, pattern);
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		if (selectedScope.isLeaf()) subActionRollbackList.add(new ScopeDeclareProgressAction(selectedScope.getId(), "").execute(context));

		selectedScope.add(new Scope("", newScopeId));

		subActionRollbackList.add(scopeUpdateAction.execute(context));
		return new ScopeInsertChildRollbackAction(newScopeId, subActionRollbackList);
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
