package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareReadOnlyActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(TeamDeclareReadOnlyActionEntity.class)
public class TeamDeclareReadOnlyAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Attribute
	private boolean readOnly;

	protected TeamDeclareReadOnlyAction() {}

	public TeamDeclareReadOnlyAction(final UUID userId, final boolean readOnly) {
		this.userId = userId;
		this.readOnly = readOnly;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (actionContext.getUserId().equals(userId)) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CANT_CHANGE_YOUR_OWN_PERMISSION);

		final UserRepresentation user = ActionHelper.findUser(userId, context, this);
		final boolean wasReadOnly = user.isReadOnly();
		user.setReadOnly(readOnly);
		return new TeamDeclareReadOnlyAction(userId, wasReadOnly);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

}
