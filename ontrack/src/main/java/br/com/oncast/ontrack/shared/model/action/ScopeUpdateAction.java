package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUpdateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionHelper;

@ConvertTo(ScopeUpdateActionEntity.class)
public class ScopeUpdateAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newDescription")
	@Attribute
	private String newDescription;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

	// TODO +Only create necessary subActions
	public ScopeUpdateAction(final UUID referenceId, final String newPattern) {
		this.referenceId = referenceId;

		final ScopeRepresentationParser parser = new ScopeRepresentationParser(newPattern);
		newDescription = parser.getScopeDescription();

		subActionList = new ArrayList<ModelAction>();
		subActionList.add(new ScopeBindReleaseAction(referenceId, parser.getReleaseDescription()));
		subActionList.add(new ScopeDeclareEffortAction(referenceId, parser.hasDeclaredEffort(), parser.getDeclaredEffort()));
		subActionList.add(new ScopeDeclareValueAction(referenceId, parser.hasDeclaredValue(), parser.getDeclaredValue()));
		subActionList.add(new ScopeDeclareProgressAction(referenceId, parser.getProgressDescription()));
	}

	public ScopeUpdateAction(final UUID referenceId, final String newDescription, final List<ModelAction> subActionList) {
		this.referenceId = referenceId;
		this.newDescription = newDescription;
		this.subActionList = subActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeUpdateAction() {}

	@Override
	public ScopeUpdateAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final List<ModelAction> subActionRollbackList = ActionExecutionHelper.executeSubActions(subActionList, context);

		final String oldDescription = selectedScope.getDescription();
		selectedScope.setDescription(newDescription);

		return new ScopeUpdateAction(referenceId, oldDescription, subActionRollbackList);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesEffortInference() {
		return true;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesValueInference() {
		return true;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesProgressInference() {
		return true;
	}
}
