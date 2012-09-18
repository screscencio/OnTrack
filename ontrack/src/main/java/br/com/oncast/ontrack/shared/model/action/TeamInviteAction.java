package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TeamInviteActionEntity.class)
public class TeamInviteAction implements TeamAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID inviteeEmail;

	protected TeamInviteAction() {}

	public TeamInviteAction(final User user) {
		this(user.getEmail());
	}

	public TeamInviteAction(final String inviteeEmail) {
		this.inviteeEmail = new UUID(inviteeEmail);
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final User user = new User(inviteeEmail.toStringRepresentation());
		context.addUser(user);
		return new TeamRevogueInvitationAction(inviteeEmail.toStringRepresentation());
	}

	@Override
	public UUID getReferenceId() {
		return inviteeEmail;
	}

}
