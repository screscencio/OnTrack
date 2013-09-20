package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(TeamInviteActionEntity.class)
public class TeamInviteAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Element
	private Profile projectProfile;

	protected TeamInviteAction() {}

	public TeamInviteAction(final UUID userId, final Profile projectProfile) {
		this.userId = userId;
		this.projectProfile = projectProfile;
	}

	public TeamInviteAction(final UserRepresentation user) {
		this(user.getId(), user.getProjectProfile());
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		verifyPermission(context, actionContext);

		try {
			context.findUser(userId).setValid(true);
		} catch (final UserNotFoundException e) {
			context.addUser(new UserRepresentation(userId, projectProfile));
		}

		return new TeamRevogueInvitationAction(userId);
	}

	private void verifyPermission(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (ActionHelper.shouldIgnorePermissionVerification(context, actionContext)) return;

		final UserRepresentation invitor = ActionHelper.findUserFrom(actionContext, context, this);
		if (!invitor.canInvitePeople()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.PERMISSION_DENIED);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

	@Override
	public String toString() {
		return "TeamInviteAction [userId=" + userId + ", profile=" + projectProfile + "]";
	}

}
