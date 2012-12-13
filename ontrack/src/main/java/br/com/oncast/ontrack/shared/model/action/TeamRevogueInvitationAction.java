package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamRevogueInvitationActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TeamRevogueInvitationActionEntity.class)
public class TeamRevogueInvitationAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID userId;

	protected TeamRevogueInvitationAction() {}

	public TeamRevogueInvitationAction(final User user) {
		this(user.getId());
	}

	public TeamRevogueInvitationAction(final UUID userId) {
		this.userId = userId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		ActionHelper.findUser(userId, context).setValid(false);

		return new TeamInviteAction(userId);
	}

	@Override
	public UUID getReferenceId() {
		return userId;
	}

}
