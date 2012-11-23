package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeAddAssociatedUserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.TagFactory;
import br.com.oncast.ontrack.shared.model.tags.UserTag;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeAddAssociatedUserActionEntity.class)
public class ScopeAddAssociatedUserAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID userId;

	@Element
	private UUID tagId;

	protected ScopeAddAssociatedUserAction() {}

	public ScopeAddAssociatedUserAction(final UUID scopeId, final UUID userId) {
		this.scopeId = scopeId;
		this.userId = userId;
		this.tagId = new UUID();
	}

	protected ScopeAddAssociatedUserAction(final UserTag tag) {
		this.scopeId = tag.getSubject().getId();
		this.userId = tag.getUser().getId();
		this.tagId = tag.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context);
		final User user = ActionHelper.findUser(userId, context);

		context.addTag(TagFactory.createUserTag(tagId, scope, user));

		return new ScopeRemoveAssociatedUserAction(scopeId, tagId);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}

}
