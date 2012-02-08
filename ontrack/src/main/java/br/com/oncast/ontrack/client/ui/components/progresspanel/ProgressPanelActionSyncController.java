package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProgressPanelActionSyncController {

	private final ActionExecutionService actionExecutionService;
	private final ActionExecutionListener actionExecutionListener;
	private ReleaseMonitor releaseMonitor;

	public ProgressPanelActionSyncController(final ActionExecutionService actionExecutionService, final Release release, final Display display) {
		this.actionExecutionService = actionExecutionService;
		this.actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> influencedScopes,
					final boolean isUserAction) {
				handleActionExecution(display, action, context);
			}
		};
		setMonitoredRelease(release);
	}

	public void registerActionExecutionListener() {
		actionExecutionService.addActionExecutionListener(actionExecutionListener);
	}

	public void unregisterActionExecutionListener() {
		actionExecutionService.removeActionExecutionListener(actionExecutionListener);
	}

	public void setMonitoredRelease(final Release release) {
		releaseMonitor = new ReleaseMonitor(release);
	}

	private void handleActionExecution(final Display display, final ModelAction action, final ProjectContext context) {
		if (releaseMonitor == null) return;

		try {
			ActionMapper.handleAction(context, action, releaseMonitor, display);
		}
		catch (final ModelBeanNotFoundException e) {
			// TODO ++Resync and Redraw the entire structure to eliminate inconsistencies
			throw new RuntimeException("It was not possible to update the view because an inconsistency with the model was detected.", e);
		}
		finally {
			releaseMonitor.updateMonitoredReleaseState();
		}
	}

	public interface Display {
		void update();

		void exit();

		void updateReleaseInfo();
	}

	private enum ActionMapper {
		KANBAN_ACTIONS {
			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				if (releaseMonitor.getRelease().equals(context.findRelease(action.getReferenceId()))) display.update();
			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				return action instanceof KanbanAction;
			}
		},
		SCOPE_INSERTION_ACTIONS {

			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				final Scope scope = context.findScope(((ScopeInsertAction) action).getNewScopeId());
				if (releaseMonitor.getRelease().equals(scope.getRelease()) || releaseMonitor.releaseContainedScope(scope)) display.update();
			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ScopeInsertAction) return true;

				return false;
			}
		},
		SCOPE_GENERAL_ACTION {

			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				final Scope scope = context.findScope(action.getReferenceId());
				if (releaseMonitor.getRelease().equals(scope.getRelease()) || releaseMonitor.releaseContainedScope(scope)) display.update();
			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ScopeRemoveAction) return true;
				if (action instanceof ScopeRemoveRollbackAction) return true;
				if (action instanceof ScopeUpdateAction) return true;
				if (action instanceof ScopeInsertSiblingUpRollbackAction) return true;
				if (action instanceof ScopeInsertSiblingDownRollbackAction) return true;
				if (action instanceof ScopeInsertParentRollbackAction) return true;
				if (action instanceof ScopeInsertChildRollbackAction) return true;
				if (action instanceof ScopeBindReleaseAction) return true;
				if (action instanceof ScopeDeclareProgressAction) return true;

				return false;
			}
		},
		RELEASE_GENERAL_ACTION {

			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				if (releaseMonitor.getRelease().getId().equals(action.getReferenceId())) display.update();
			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ReleaseScopeUpdatePriorityAction) return true;

				return false;
			}
		},
		RELEASE_RENAMING_ACTION {
			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				if (releaseMonitor.getRelease().getId().equals(action.getReferenceId())) display.updateReleaseInfo();
			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ReleaseRenameAction) return true;

				return false;
			}
		},
		RELEASE_REMOVAL_ACTION {
			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {
				final Release kanbanRelease = releaseMonitor.getRelease();
				try {
					context.findRelease(kanbanRelease.getId());
				}
				catch (final ModelBeanNotFoundException e) {
					display.exit();
				}

			}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ReleaseRemoveAction) return true;

				return false;
			}
		},
		IGNORED_ACTIONS {
			@Override
			protected void handleActionImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
					throws ModelBeanNotFoundException {}

			@Override
			protected boolean isHandlerFor(final ModelAction action) {
				if (action instanceof ReleaseCreateAction) return true;
				if (action instanceof ReleaseRemoveRollbackAction) return true;
				if (action instanceof ReleaseUpdatePriorityAction) return true;
				if (action instanceof ScopeDeclareEffortAction) return true;
				if (action instanceof ScopeDeclareValueAction) return true;
				if (action instanceof ScopeMoveAction) return true;

				return false;
			}
		};

		private static void handleAction(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor, final Display display)
				throws ModelBeanNotFoundException {
			for (final ActionMapper mapper : values()) {
				if (mapper.isHandlerFor(action)) {
					mapper.handleActionImpl(context, action, releaseMonitor, display);
					break;
				}
			}
		}

		protected abstract void handleActionImpl(ProjectContext context, ModelAction action, ReleaseMonitor releaseMonitor, Display display)
				throws ModelBeanNotFoundException;

		protected abstract boolean isHandlerFor(ModelAction action);
	}

	protected class ReleaseMonitor {

		private final Release release;
		private List<Scope> scopeListCopy;

		public ReleaseMonitor(final Release release) {
			this.release = release;
			updateMonitoredReleaseState();
		}

		public boolean releaseContainedScope(final Scope scope) {
			return scopeListCopy.contains(scope);
		}

		public Release getRelease() {
			return release;
		}

		private void updateMonitoredReleaseState() {
			scopeListCopy = release.getScopeList();
		}
	}
}
