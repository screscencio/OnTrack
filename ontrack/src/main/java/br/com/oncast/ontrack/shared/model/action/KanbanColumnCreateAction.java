package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(KanbanColumnCreateActionEntity.class)
public class KanbanColumnCreateAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("columnId")
	@Element
	private UUID columnId;

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

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban, final int columnIndex, final List<ModelAction> rollbackActions) {
		this(releaseReferenceId, columnDescription, shouldLockKanban, rollbackActions);
		this.columnIndex = columnIndex;
	}

	private KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban, final List<ModelAction> rollbackActions) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = shouldLockKanban;
		this.subActions = rollbackActions == null ? new ArrayList<ModelAction>() : rollbackActions;
		this.columnId = new UUID();
	}

	public KanbanColumnCreateAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context, this);
		final Kanban kanban = context.getKanban(release);
		if (kanban.isStaticColumn(columnDescription)) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.KANBAN_COLUMN_ALREADY_SET);

		if (!kanban.hasNonInferedColumn(columnDescription)) kanban.appendColumn(new KanbanColumn(columnDescription, columnId));

		if (columnIndex >= 0) kanban.moveColumn(columnDescription, columnIndex);
		if (shouldLockKanban && subActions.isEmpty() && !kanban.isLocked()) subActions.add(new KanbanLockAction(referenceId));
		ActionHelper.executeSubActions(subActions, context, actionContext);

		return new KanbanColumnRemoveAction(referenceId, columnDescription, shouldLockKanban);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	public UUID getColumnId() {
		return columnId;
	}

	public void setColumnId(final UUID columnId) {
		this.columnId = columnId;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public void setColumnDescription(final String columnDescription) {
		this.columnDescription = columnDescription;
	}

	public boolean getShouldLockKanban() {
		return shouldLockKanban;
	}

	public void setShouldLockKanban(final boolean shouldLockKanban) {
		this.shouldLockKanban = shouldLockKanban;
	}

	public List<ModelAction> getSubActions() {
		return subActions;
	}

	public void setSubActions(final List<ModelAction> subActions) {
		this.subActions = subActions;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
	}

}