package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
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
			if (ActionMapper.isRelated(context, action, releaseMonitor)) {
				if (action instanceof ReleaseRemoveAction) display.exit();
				else display.update();
			}
		}
		catch (final ModelBeanNotFoundException e) {
			// FIXME MATSUMOTO
			e.printStackTrace();
		}
		finally {
			releaseMonitor.updateMonitoredReleaseState();
		}
	}

	public interface Display {
		void update();

		void exit();
	}

	private enum ActionMapper {
		SCOPE_ACTION {
			@Override
			protected boolean accepts(final ModelAction action) {
				return action instanceof ScopeAction;
			}

			@Override
			protected boolean isRelatedImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor)
					throws ModelBeanNotFoundException {
				final Scope scope = context.findScope(action.getReferenceId());
				return releaseMonitor.getRelease().equals(scope.getRelease()) || releaseMonitor.releaseContainedScope(scope);
			}
		},
		RELEASE_ACTION {
			@Override
			protected boolean accepts(final ModelAction action) {
				return action instanceof ReleaseAction;
			}

			@Override
			protected boolean isRelatedImpl(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor)
					throws ModelBeanNotFoundException {
				final Release foundRelease = context.findRelease(action.getReferenceId());
				return releaseMonitor.getRelease().equals(foundRelease);
			}

		};

		private static boolean isRelated(final ProjectContext context, final ModelAction action, final ReleaseMonitor releaseMonitor)
				throws ModelBeanNotFoundException {
			for (final ActionMapper mapper : values()) {
				if (mapper.accepts(action)) return mapper.isRelatedImpl(context, action, releaseMonitor);
			}
			return false;
		}

		protected abstract boolean accepts(ModelAction action);

		protected abstract boolean isRelatedImpl(ProjectContext context, ModelAction action, ReleaseMonitor releaseMonitor)
				throws ModelBeanNotFoundException;
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
