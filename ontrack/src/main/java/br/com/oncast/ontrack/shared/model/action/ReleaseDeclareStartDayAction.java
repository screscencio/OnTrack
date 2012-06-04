package br.com.oncast.ontrack.shared.model.action;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareStartDayActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(ReleaseDeclareStartDayActionEntity.class)
public class ReleaseDeclareStartDayAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID referenceId;

	@Attribute(required = false)
	@IgnoredByDeepEquality
	private Date date;

	ReleaseDeclareStartDayAction() {}

	public ReleaseDeclareStartDayAction(final UUID referenceId, final Date date) {
		this.referenceId = referenceId;
		this.date = date;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context);
		final Date previousDate = release.hasDeclaredStartDay() ? release.getStartDay().getJavaDate() : null;

		release.declareStartDay(date == null ? null : WorkingDayFactory.create(date));

		return new ReleaseDeclareStartDayAction(referenceId, previousDate);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

}
