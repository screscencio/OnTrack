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
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(TeamDeclareProfileActionEntity.class)
public class TeamDeclareProfileAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	@Element
	private Profile newProfile;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	protected TeamDeclareProfileAction() {}

	public TeamDeclareProfileAction(final UUID userId, final Profile newProfile) {
		this.uniqueId = new UUID();
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

}
