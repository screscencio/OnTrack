package br.com.oncast.ontrack.shared.model.scope.actions;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveRollbackActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeRemoveRollbackActionEntity.class)
public class ScopeRemoveRollbackAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("parentScopeId")
	private UUID parentScopeId;

	@ConversionAlias("index")
	private int index;

	@ConversionAlias("description")
	private String description;

	@ConversionAlias("releaseId")
	private UUID releaseId;

	@ConversionAlias("childActionList")
	private List<ScopeRemoveRollbackAction> childActionList;

	public ScopeRemoveRollbackAction(final UUID parentScopeId, final UUID selectedScopeId, final String description, final UUID releaseId, final int index,
			final List<ScopeRemoveRollbackAction> childActionList) {
		this.parentScopeId = parentScopeId;
		this.referenceId = selectedScopeId;
		this.index = index;
		this.description = description;
		this.releaseId = releaseId;
		this.childActionList = childActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeRemoveRollbackAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope parent = context.findScope(parentScopeId);
		final Scope newScope = new Scope(description, referenceId);
		final Release release = releaseId != null ? context.findRelease(releaseId) : null;

		parent.add(index, newScope);

		newScope.setRelease(release);
		if (release != null) release.addScope(newScope);

		for (final ScopeRemoveRollbackAction childAction : childActionList)
			childAction.execute(context);

		return new ScopeRemoveAction(referenceId);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

}
