package br.com.oncast.ontrack.shared.model.action;

import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findScope;
import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findUserFrom;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareTimeSpentActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDeclareTimeSpentActionEntity.class)
public class ScopeDeclareTimeSpentAction implements ScopeAction, TimesheetAction {

	private static final long serialVersionUID = 1L;

	private UUID scopeId;

	private Float timeSpent;

	protected ScopeDeclareTimeSpentAction() {}

	public ScopeDeclareTimeSpentAction(final UUID scopeId, final Float timeSpent) {
		this.scopeId = scopeId;
		this.timeSpent = timeSpent;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		findScope(scopeId, context);
		final UUID currentUserId = findUserFrom(actionContext, context).getId();

		final Float previousTimespent = context.getDeclaredTimeSpent(scopeId, currentUserId);
		context.declareTimeSpent(scopeId, currentUserId, timeSpent);

		return new ScopeDeclareTimeSpentAction(scopeId, previousTimespent);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}
}