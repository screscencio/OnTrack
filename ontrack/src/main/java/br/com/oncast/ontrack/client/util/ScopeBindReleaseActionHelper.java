package br.com.oncast.ontrack.client.util;

import java.util.LinkedList;
import java.util.Queue;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeBindReleaseActionHelperMessages;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;

public class ScopeBindReleaseActionHelper {

	private static final ScopeBindReleaseActionHelperMessages message = GWT.create(ScopeBindReleaseActionHelperMessages.class);

	public static boolean validadeHierarchicalCondition(final UUID scopeId, final String releaseDescription) {
		try {
			final Scope scope = ClientServiceProvider.getCurrentProjectContext().findScope(scopeId);
			if (new ReleaseDescriptionParser(releaseDescription).getHeadRelease().isEmpty()) return true;

			return checkAncestorsForRelease(scope) && checkDescendantsForRelease(scope);
		}
		catch (final ScopeNotFoundException e) {
			return true;
		}
	}

	private static boolean checkDescendantsForRelease(final Scope scope) {
		final Queue<Scope> scopes = new LinkedList<Scope>();
		scopes.addAll(scope.getChildren());

		while (!scopes.isEmpty()) {
			final Scope s = scopes.poll();
			if (s.getRelease() != null) {
				ClientServiceProvider.getInstance().getClientAlertingService()
						.showWarning(message.cantBindReleaseBecauseOfADescendant(s.getDescription()), ClientAlertingService.DURATION_LONG);
				return false;
			}
			scopes.addAll(s.getChildren());
		}
		return true;
	}

	private static boolean checkAncestorsForRelease(final Scope scope) {
		Scope s = scope;
		while (!s.isRoot()) {
			s = s.getParent();
			if (s.getRelease() != null) {
				ClientServiceProvider.getInstance().getClientAlertingService()
						.showWarning(message.cantBindReleaseBecauseOfAnAncestor(s.getDescription()), ClientAlertingService.DURATION_LONG);
				return false;
			}
		}
		return true;
	}

}
