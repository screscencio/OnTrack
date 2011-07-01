package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertChildActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertChildActionEntity.class)
public class ScopeInsertChildAction implements ScopeInsertAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newScopeId")
	private UUID newScopeId;

	@ConversionAlias("pattern")
	private String pattern;

	public ScopeInsertChildAction(final UUID referenceId, final String pattern) {
		this.referenceId = referenceId;
		this.pattern = pattern;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertChildAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		selectedScope.add(newScope);

		new ScopeUpdateAction(newScopeId, pattern).execute(context);
		return new ScopeRemoveAction(newScopeId);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}
}
