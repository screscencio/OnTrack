package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareTimeSpentActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findScope;
import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findActionAuthor;

@ConvertTo(ScopeDeclareTimeSpentActionEntity.class)
public class ScopeDeclareTimeSpentAction implements ScopeAction, TimesheetAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Attribute
	private Float timeSpent;

	public ScopeDeclareTimeSpentAction() {}

	public ScopeDeclareTimeSpentAction(final UUID scopeId, final Float timeSpent) {
		this.scopeId = scopeId;
		this.timeSpent = timeSpent;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		findScope(scopeId, context, this);
		final UUID currentUserId = findActionAuthor(actionContext, context, this).getId();

		final Float previousTimespent = context.getDeclaredTimeSpent(scopeId, currentUserId);
		context.declareTimeSpent(scopeId, currentUserId, timeSpent);

		return new ScopeDeclareTimeSpentAction(scopeId, previousTimespent);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	public UUID getScopeId() {
		return scopeId;
	}

	public void setScopeId(final UUID scopeId) {
		this.scopeId = scopeId;
	}

	public Float getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(final Float timeSpent) {
		this.timeSpent = timeSpent;
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
