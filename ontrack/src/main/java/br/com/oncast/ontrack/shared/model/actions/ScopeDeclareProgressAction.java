/*
 * DECISION 2011-11-08 - During October, it was decided to save the timestamp of progress definitions changes.
 * Although it is necessary for features such as the "burn up", I - Lobo - do not like the choosen approach, that uses a time stamp inside this action to store
 * time changes. This should be reviewed because, in my opinion, this approach makes the "burn up" logic intrusive into the app model, demands huge effort to
 * control time stamp consistency problems between clients and is not very flexible (future logic updates may not have sufficient data).
 */

package br.com.oncast.ontrack.shared.model.actions;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareProgressActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(ScopeDeclareProgressActionEntity.class)
public class ScopeDeclareProgressAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newProgressDescription")
	@Attribute
	private String newProgressDescription;

	@ConversionAlias("timestamp")
	@Attribute(required = false)
	@IgnoredByDeepEquality
	private Date timestamp;

	public ScopeDeclareProgressAction(final UUID referenceId, final String newProgressDescription) {
		this.referenceId = referenceId;
		this.newProgressDescription = newProgressDescription == null ? "" : newProgressDescription;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareProgressAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);
		final String oldProgressDescription = selectedScope.getProgress().getDescription();

		selectedScope.getProgress().setDescription(newProgressDescription, timestamp);

		return new ScopeDeclareProgressAction(referenceId, oldProgressDescription);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
