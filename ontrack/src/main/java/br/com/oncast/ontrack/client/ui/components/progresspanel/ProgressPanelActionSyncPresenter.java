package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProgressPanelActionSyncPresenter {

	public ProgressPanelActionSyncPresenter(final Release release, final Display display, final ActionExecutionService actionExecutionService) {
		actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> influencedScopes,
					final boolean isUserAction) {
				try {
					if (ActionMapper.isRelated(context, release, display, action)) {
						display.update();
					}
				}
				catch (final ModelBeanNotFoundException e) {
					e.printStackTrace();
				}
			}

		});
	}

	private enum ActionMapper {
		SCOPE_ACTION {
			@Override
			protected boolean accepts(final ModelAction action) {
				return action instanceof ScopeAction;
			}

			@Override
			protected boolean isRelatedImpl(final ProjectContext context, final Release release, final Display display, final ModelAction action)
					throws ModelBeanNotFoundException {
				final Scope scope = context.findScope(action.getReferenceId());
				return release.equals(scope.getRelease()) || display.containsScope(scope);
			}
		},
		RELEASE_ACTION {
			@Override
			protected boolean accepts(final ModelAction action) {
				return action instanceof ReleaseAction;
			}

			@Override
			protected boolean isRelatedImpl(final ProjectContext context, final Release release, final Display display, final ModelAction action)
					throws ModelBeanNotFoundException {
				final Release foundRelease = context.findRelease(action.getReferenceId());
				return release.equals(foundRelease);
			}

		};

		private static boolean isRelated(final ProjectContext context, final Release release, final Display display, final ModelAction action)
				throws ModelBeanNotFoundException {
			for (final ActionMapper mapper : values()) {
				if (mapper.accepts(action)) return mapper.isRelatedImpl(context, release, display, action);
			}
			return false;
		}

		protected abstract boolean accepts(ModelAction action);

		protected abstract boolean isRelatedImpl(ProjectContext context, Release release, Display display, ModelAction action)
				throws ModelBeanNotFoundException;

	}

	public interface Display {

		void update();

		boolean containsScope(Scope scope);

	}

}
