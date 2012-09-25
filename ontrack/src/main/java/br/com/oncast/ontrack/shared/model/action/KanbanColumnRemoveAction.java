package br.com.oncast.ontrack.shared.model.action;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(KanbanColumnRemoveActionEntity.class)
public class KanbanColumnRemoveAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("columnDescription")
	@Attribute
	private String columnDescription;

	@ConversionAlias("shouldUnfixKanban")
	@Attribute
	private boolean shouldLockKanban;

	public KanbanColumnRemoveAction(final UUID releaseReferenceId, final String columnDescription) {
		this(releaseReferenceId, columnDescription, true);
	}

	public KanbanColumnRemoveAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = shouldLockKanban;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected KanbanColumnRemoveAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context);
		final Kanban kanban = context.getKanban(release);

		validateExecution(kanban);
		final List<ModelAction> rollbackActions = moveColumnScopes(columnDescription, kanban.getColumnPredeceding(columnDescription).getDescription(), release,
				context, actionContext);
		final int oldColumnIndex = kanban.indexOf(columnDescription);
		kanban.removeColumn(columnDescription);

		if (shouldLockKanban) kanban.setLocked(true);

		return new KanbanColumnCreateAction(referenceId, columnDescription, shouldLockKanban, oldColumnIndex, rollbackActions);
	}

	private List<ModelAction> moveColumnScopes(final String originColumn, final String destinationColumn, final Release release, final ProjectContext context,
			final ActionContext actionContext)
			throws UnableToCompleteActionException {
		final List<Scope> scopes = release.getScopeList();
		if (scopes.isEmpty()) return null;

		final List<ModelAction> rollbackActions = new LinkedList<ModelAction>();
		for (final Scope scope : scopes) {
			if (!originColumn.equals(scope.getProgress().getDescription())) continue;
			rollbackActions.add(0, new ScopeDeclareProgressAction(scope.getId(), destinationColumn).execute(context, actionContext));
		}

		return rollbackActions;
	}

	private void validateExecution(final Kanban kanban) throws UnableToCompleteActionException {
		if (!kanban.hasColumn(columnDescription)) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.KANBAN_COLUMN_NOT_FOUND);
		if (kanban.isStaticColumn(columnDescription)) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.REMOVE_STATIC_KANBAN_COLUMN);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}