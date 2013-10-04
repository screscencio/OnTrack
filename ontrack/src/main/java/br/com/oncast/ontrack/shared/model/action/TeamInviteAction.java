package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
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

	public TeamInviteAction() {}

	public TeamInviteAction(final UUID userId, final Profile projectProfile) {
		this.userId = userId;
		this.projectProfile = projectProfile;
	}

	public TeamInviteAction(final UserRepresentation user) {
		this(user.getId(), user.getProjectProfile());
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		ActionHelper.verifyPermission(context, actionContext, Profile.PEOPLE_MANAGER, this);

		try {
			context.findUser(userId).setValid(true);
		} catch (final UserNotFoundException e) {
			context.addUser(new UserRepresentation(userId, projectProfile));
		}

		return new TeamRevogueInvitationAction(userId);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID userId) {
		this.userId = userId;
	}

	public Profile getProjectProfile() {
		return projectProfile;
	}

	public void setProjectProfile(final Profile projectProfile) {
		this.projectProfile = projectProfile;
	}

	@Override
	public String toString() {
		return "TeamInviteAction [userId=" + userId + ", profile=" + projectProfile + "]";
	}

}
