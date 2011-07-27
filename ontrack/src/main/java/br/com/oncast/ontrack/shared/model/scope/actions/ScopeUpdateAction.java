package br.com.oncast.ontrack.shared.model.scope.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUpdateActionEntity;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeUpdateActionEntity.class)
public class ScopeUpdateAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newDescription")
	private String newDescription;

	@ConversionAlias("subActionList")
	private List<ModelAction> subActionList;

	// TODO +Only create necessary subActions
	public ScopeUpdateAction(final UUID referenceId, final String newPattern) {
		this.referenceId = referenceId;

		final ScopeRepresentationParser parser = new ScopeRepresentationParser(newPattern);
		newDescription = parser.getScopeDescription();

		subActionList = new ArrayList<ModelAction>();
		subActionList.add(new ScopeBindReleaseAction(referenceId, parser.getReleaseDescription()));
		subActionList.add(new ScopeDeclareEffortAction(referenceId, parser.hasDeclaredEffort(), parser.getDeclaredEffort()));
		subActionList.add(new ScopeProgressAction(referenceId, parser.getProgressDescription()));
	}

	public ScopeUpdateAction(final UUID referenceId, final String newDescription, final List<ModelAction> subActionList) {
		this.referenceId = referenceId;
		this.newDescription = newDescription;
		this.subActionList = subActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeUpdateAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		for (final ModelAction action : subActionList) {
			subActionRollbackList.add(action.execute(context));
		}

		final String oldDescription = selectedScope.getDescription();
		selectedScope.setDescription(newDescription);

		return new ScopeUpdateAction(referenceId, oldDescription, subActionRollbackList);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}
}
