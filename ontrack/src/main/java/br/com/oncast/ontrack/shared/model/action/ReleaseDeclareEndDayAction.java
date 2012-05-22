package br.com.oncast.ontrack.shared.model.action;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEndDayActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

@ConvertTo(ReleaseDeclareEndDayActionEntity.class)
public class ReleaseDeclareEndDayAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID referenceId;

	@Attribute(required = false)
	private Date endDay;

	protected ReleaseDeclareEndDayAction() {}

	public ReleaseDeclareEndDayAction(final UUID referenceId, final Date endDay) {
		this.referenceId = referenceId;
		this.endDay = endDay;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release release = ReleaseActionHelper.findRelease(referenceId, context);

		Date previousDeclaration = null;
		if (release.hasDeclaredEndDay() && release.getEndDay() != null) previousDeclaration = release.getEndDay().getJavaDate();

		release.declareEndDay(endDay == null ? null : WorkingDayFactory.create(endDay));

		return new ReleaseDeclareEndDayAction(referenceId, previousDeclaration);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

}
