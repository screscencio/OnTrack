package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
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
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = true;
	}

	public KanbanColumnRemoveAction(final UUID releaseReferenceId, final String columnDescription, final boolean shouldLockKanban) {
		this.referenceId = releaseReferenceId;
		this.columnDescription = columnDescription;
		this.shouldLockKanban = shouldLockKanban;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected KanbanColumnRemoveAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release release = ReleaseActionHelper.findRelease(referenceId, context);
		final Kanban kanban = context.getKanban(release);

		validateExecution(kanban);
		kanban.removeColumn(columnDescription);

		if (shouldLockKanban) kanban.setLocked(true);

		return new KanbanColumnCreateAction(referenceId, columnDescription, shouldLockKanban);
	}

	private void validateExecution(final Kanban savedKanban) throws UnableToCompleteActionException {
		final String defaultNotStartedName = Progress.DEFAULT_NOT_STARTED_NAME;
		final String defaultDoneName = ProgressState.DONE.getDescription();

		if (!savedKanban.hasColumnForDescription(columnDescription)) throw new UnableToCompleteActionException("The column is does not exist.");
		if (defaultDoneName.equals(columnDescription)) throw new UnableToCompleteActionException("The column '" + defaultDoneName
				+ "' should never be removed.");
		if (columnDescription.isEmpty() || defaultNotStartedName.equals(columnDescription)) throw new UnableToCompleteActionException("The column '"
				+ defaultNotStartedName + "' should never be removed.");
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}