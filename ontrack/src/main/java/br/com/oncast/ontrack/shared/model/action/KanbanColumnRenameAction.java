package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRenameActionEntity;
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

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(KanbanColumnRenameActionEntity.class)
public class KanbanColumnRenameAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("releaseId")
	private UUID releaseId;

	@Attribute
	@ConversionAlias("columnDescription")
	private String columnDescription;

	@Attribute
	@ConversionAlias("newDescription")
	private String newDescription;

	@ElementList(required = false)
	private List<ModelAction> subActions;

	public KanbanColumnRenameAction() {}

	public KanbanColumnRenameAction(final UUID releaseId, final String columnDescription, final String newDescription) {
		this.releaseId = releaseId;
		this.columnDescription = columnDescription;
		this.newDescription = newDescription;
		this.subActions = new ArrayList<ModelAction>();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final String trimmedDescription = newDescription.trim();
		if (trimmedDescription.isEmpty()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.EMPTY_DESCRIPTION);

		final Release release = ActionHelper.findRelease(releaseId, context, this);
		final Kanban kanban = context.getKanban(release);

		try {
			kanban.renameColumn(columnDescription, newDescription);
		} catch (final RuntimeException e) {
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.KANBAN_COLUMN_ALREADY_SET);
		}
		for (final Scope scope : release.getScopeList()) {
			if (scope.getProgress().getDescription().equals(columnDescription)) new ScopeDeclareProgressAction(scope.getId(), newDescription).execute(context, actionContext);
		}
		if (subActions.isEmpty() && !kanban.isLocked()) subActions.add(new KanbanLockAction(releaseId));
		ActionHelper.executeSubActions(subActions, context, actionContext);
		return new KanbanColumnRenameAction(releaseId, newDescription, columnDescription);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

	public UUID getReleaseId() {
		return releaseId;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public List<ModelAction> getSubActions() {
		return subActions;
	}

	public void setReleaseId(final UUID releaseId) {
		this.releaseId = releaseId;
	}

	public void setColumnDescription(final String columnDescription) {
		this.columnDescription = columnDescription;
	}

	public void setNewDescription(final String newDescription) {
		this.newDescription = newDescription;
	}

	public void setSubActions(final List<ModelAction> subActions) {
		this.subActions = subActions;
	}

}
