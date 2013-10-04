package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareCanInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(TeamDeclareCanInviteActionEntity.class)
public class TeamDeclareCanInviteAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Attribute
	private boolean canInvite;

	public TeamDeclareCanInviteAction() {}

	public TeamDeclareCanInviteAction(final UUID userId, final boolean canInvite) {
		this.userId = userId;
		this.canInvite = canInvite;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (userId.equals(actionContext.getUserId())) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CANT_CHANGE_YOUR_OWN_PERMISSION);
		ActionHelper.verifyPermission(context, actionContext, Profile.PEOPLE_MANAGER, this);

		final UserRepresentation user = ActionHelper.findUser(userId, context, this);
		final boolean previousCanInvite = user.canInvitePeople();
		user.setProjectProfile(canInvite ? Profile.PROJECT_MANAGER : Profile.CONTRIBUTOR);
		return new TeamDeclareCanInviteAction(userId, previousCanInvite);
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

	public boolean getCanInvite() {
		return canInvite;
	}

	public void setCanInvite(final boolean canInvite) {
		this.canInvite = canInvite;
	}

}
