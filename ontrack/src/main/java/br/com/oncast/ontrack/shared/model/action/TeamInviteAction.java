package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(TeamInviteActionEntity.class)
public class TeamInviteAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Attribute
	private boolean canInvite;

	@Attribute
	private boolean readOnly;

	protected TeamInviteAction() {}

	public TeamInviteAction(final UUID userId, final boolean canInvite, final boolean readOnly) {
		this.userId = userId;
		this.canInvite = canInvite;
		this.readOnly = readOnly;
	}

	public TeamInviteAction(final UserRepresentation user) {
		this(user.getId(), user.canInvite(), user.isReadOnly());
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		verifyPermission(context, actionContext);

		try {
			context.findUser(userId).setValid(true);
		} catch (final UserNotFoundException e) {
			context.addUser(new UserRepresentation(userId, canInvite, readOnly));
		}

		return new TeamRevogueInvitationAction(userId);
	}

	private void verifyPermission(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (context.getUsers().isEmpty()) return;

		final UserRepresentation invitor = ActionHelper.findUserFrom(actionContext, context, this);
		if (!invitor.canInvite()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.PERMISSION_DENIED);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

}
