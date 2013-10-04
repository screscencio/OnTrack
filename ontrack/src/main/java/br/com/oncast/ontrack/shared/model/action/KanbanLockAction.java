package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanLockActionEntity;
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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(KanbanLockActionEntity.class)
public class KanbanLockAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID releaseId;

	@ElementList(required = false)
	private List<String> columnDescriptions;

	@ElementList(required = false)
	private List<String> columnIds;

	public KanbanLockAction() {}

	public KanbanLockAction(final UUID releaseId) {
		this.releaseId = releaseId;
		columnDescriptions = new ArrayList<String>();
		columnIds = new ArrayList<String>();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(releaseId, context, this);
		final Kanban kanban = context.getKanban(release);
		if (kanban.isLocked()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CONFLICTED);
		kanban.setLocked(true);
		for (final KanbanColumn column : kanban.getColumns()) {
			if (column.getId() == null) column.setId(getId(column.getDescription()));
		}
		return null;
	}

	private UUID getId(final String description) {
		if (!columnDescriptions.contains(description)) {
			columnDescriptions.add(description);
			columnIds.add(new UUID().toString());
		}
		return new UUID(columnIds.get(columnDescriptions.indexOf(description)));
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

	public UUID getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final UUID releaseId) {
		this.releaseId = releaseId;
	}

	public List<String> getColumnDescriptions() {
		return columnDescriptions;
	}

	public void setColumnDescriptions(final List<String> columnDescriptions) {
		this.columnDescriptions = columnDescriptions;
	}

	public List<String> getColumnIds() {
		return columnIds;
	}

	public void setColumnIds(final List<String> columnIds) {
		this.columnIds = columnIds;
	}

}
