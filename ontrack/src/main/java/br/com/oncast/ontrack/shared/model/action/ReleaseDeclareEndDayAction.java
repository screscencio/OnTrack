package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEndDayActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ReleaseDeclareEndDayActionEntity.class)
public class ReleaseDeclareEndDayAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID referenceId;

	@Attribute(required = false)
	@IgnoredByDeepEquality
	private Date endDay;

	protected ReleaseDeclareEndDayAction() {}

	public ReleaseDeclareEndDayAction(final UUID referenceId, final Date endDay) {
		this.referenceId = referenceId;
		this.endDay = endDay;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context, this);

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
