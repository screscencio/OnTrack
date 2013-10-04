package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareProfileActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(TeamDeclareProfileActionEntity.class)
public class TeamDeclareProfileAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Element
	private Profile newProfile;

	public TeamDeclareProfileAction() {}

	public TeamDeclareProfileAction(final UUID userId, final Profile newProfile) {
		this.userId = userId;
		this.newProfile = newProfile;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Profile authorProfile = ActionHelper.verifyPermission(context, actionContext, Profile.PEOPLE_MANAGER, this);
		if (authorProfile != Profile.SYSTEM_ADMIN && userId.equals(actionContext.getUserId()))
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CANT_CHANGE_YOUR_OWN_PERMISSION);

		final UserRepresentation user = ActionHelper.findUser(userId, context, this);
		final Profile previousProfile = user.getProjectProfile();

		if (!authorProfile.hasPermissionsOf(previousProfile) || !authorProfile.hasPermissionsOf(newProfile))
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.PERMISSION_DENIED);

		user.setProjectProfile(newProfile);
		return new TeamDeclareProfileAction(userId, previousProfile);
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

	public Profile getNewProfile() {
		return newProfile;
	}

	public void setNewProfile(final Profile newProfile) {
		this.newProfile = newProfile;
	}

}
