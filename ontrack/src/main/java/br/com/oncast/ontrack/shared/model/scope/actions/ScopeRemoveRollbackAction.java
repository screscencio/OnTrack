package br.com.oncast.ontrack.shared.model.scope.actions;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveRollbackActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ScopeRemoveRollbackActionEntity.class)
public class ScopeRemoveRollbackAction implements ScopeAction {

	private UUID parentScopeId;
	private int index;
	private String description;
	private UUID releaseId;
	private List<ScopeRemoveRollbackAction> childActionList;
	private UUID selectedScopeId;

	public ScopeRemoveRollbackAction(final UUID parentScopeId, final UUID selectedScopeId, final String description, final UUID releaseId, final int index,
			final List<ScopeRemoveRollbackAction> childActionList) {
		this.parentScopeId = parentScopeId;
		this.selectedScopeId = selectedScopeId;
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
		final Scope newScope = new Scope(description, selectedScopeId);
		final Release release = releaseId != null ? context.findRelease(releaseId) : null;

		parent.add(index, newScope);

		newScope.setRelease(release);
		if (release != null) release.addScope(newScope);

		for (final ScopeRemoveRollbackAction childAction : childActionList)
			childAction.execute(context);

		return new ScopeRemoveAction(selectedScopeId);
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}

}
