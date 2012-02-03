package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = true;
	}

	public KanbanColumnCreateAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = shouldLockKanban;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected KanbanColumnCreateAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release release = ReleaseActionHelper.findRelease(referenceId, context);
		final Kanban kanban = context.getKanban(release);

		if (kanban.hasColumnForDescription(columnDescription)) throw new UnableToCompleteActionException("The column is already set.");
		kanban.appendColumn(columnDescription);

		if (shouldLockKanban) kanban.setLocked(true);

		return new KanbanColumnRemoveAction(referenceId, columnDescription, shouldLockKanban);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}
