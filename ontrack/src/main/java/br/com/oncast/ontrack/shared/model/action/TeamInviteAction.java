package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TeamInviteActionEntity.class)
public class TeamInviteAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	protected TeamInviteAction() {}

	public TeamInviteAction(final UserRepresentation user) {
		this.userId = user.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation user = new UserRepresentation(userId);
		context.addUser(user);
		return new TeamRevogueInvitationAction(userId);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

}
