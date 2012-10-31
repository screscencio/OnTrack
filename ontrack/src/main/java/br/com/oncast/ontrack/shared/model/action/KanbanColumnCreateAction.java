package br.com.oncast.ontrack.shared.model.action;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionHelper;

@ConvertTo(KanbanColumnCreateActionEntity.class)
public class KanbanColumnCreateAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("columnDescription")
	@Attribute
	private String columnDescription;

	@ConversionAlias("shouldFixKanban")
	@Attribute
	private boolean shouldLockKanban;

	@ConversionAlias("subActions")
	@ElementList(required = false)
	private List<ModelAction> subActions;

	@ConversionAlias("columnIndex")
	@Attribute
	private int columnIndex = -1;

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban) {
		this(releaseReferenceId, columnDescription, shouldLockKanban, null);
	}

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban, final int columnIndex) {
		this(releaseReferenceId, columnDescription, shouldLockKanban, null);
		this.columnIndex = columnIndex;
	}

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban, final int columnIndex,
			final List<ModelAction> rollbackActions) {
		this(releaseReferenceId, columnDescription, shouldLockKanban, rollbackActions);
		this.columnIndex = columnIndex;
	}

	private KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban,
			final List<ModelAction> rollbackActions) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = shouldLockKanban;
		this.subActions = rollbackActions;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected KanbanColumnCreateAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context);
		final Kanban kanban = context.getKanban(release);

		if (kanban.isStaticColumn(columnDescription)) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.KANBAN_COLUMN_ALREADY_SET);

		if (!kanban.hasNonInferedColumn(columnDescription)) kanban.appendColumn(columnDescription);

		if (columnIndex >= 0) kanban.moveColumn(columnDescription, columnIndex);
		if (shouldLockKanban) kanban.setLocked(true);
		if (subActions != null) ActionExecutionHelper.executeSubActions(subActions, context, actionContext);

		return new KanbanColumnRemoveAction(referenceId, columnDescription, shouldLockKanban);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}