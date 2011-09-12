package br.com.oncast.ontrack.server.services;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectActionsAssembler {
	private final Project project;
	private final List<ModelAction> scopeDeclarationActions = new ArrayList<ModelAction>();
	private final List<ModelAction> releaseCreationActions = new ArrayList<ModelAction>();
	private final List<ModelAction> releaseBindingActions = new ArrayList<ModelAction>();
	private final List<ModelAction> effortDeclarationActions = new ArrayList<ModelAction>();
	private final List<ModelAction> progressDeclarationActions = new ArrayList<ModelAction>();
	private ReleaseCreator releaseCreator = new ReleaseCreator();

	private ProjectActionsAssembler(final Project project) {
		this.project = project;
	}

	public static List<ModelAction> assemble(final Project project) {
		return new ProjectActionsAssembler(project).assemble();
	}

	private List<ModelAction> assemble() {
		visitScope();

		final List<ModelAction> actions = new ArrayList<ModelAction>();
		actions.addAll(scopeDeclarationActions);
		actions.addAll(releaseCreationActions);
		actions.addAll(releaseBindingActions);
		actions.addAll(effortDeclarationActions);
		actions.addAll(progressDeclarationActions);
		return actions;
	}

	private void visitScope() {
		// Update project description on root scope
		scopeDeclarationActions.add(new ScopeUpdateAction(new UUID("0"), project.getProjectScope().getDescription()));

		// Now visit the rest of the scope tree
		visitScope(new UUID("0"), project.getProjectScope());
	}

	private void visitScope(final UUID uuid, final Scope s) {
		if (s.getRelease()!=null) {
			final String desc = s.getRelease().getFullDescription();
			if (!releaseCreator.releaseAlreadyCreated(desc))
				releaseCreationActions.addAll(releaseCreator.createNewReleaseHierarchy(desc));

			releaseBindingActions.add(new ScopeBindReleaseAction(uuid, desc));
		}

		if (s.getEffort().hasDeclared()) {
			effortDeclarationActions.add(new ScopeDeclareEffortAction(uuid, true, s.getEffort().getDeclared()));
		}

		if (s.getProgress().hasDeclared()) {
			progressDeclarationActions.add(new ScopeDeclareProgressAction(uuid, s.getProgress().getDescription()));
		}

		for (final Scope child : s.getChildren()) {
			final ScopeInsertChildAction insertAction = new ScopeInsertChildAction(uuid, child.getDescription());
			scopeDeclarationActions.add(insertAction);
			visitScope(insertAction.getNewScopeId(), child);
		}
	}
}
