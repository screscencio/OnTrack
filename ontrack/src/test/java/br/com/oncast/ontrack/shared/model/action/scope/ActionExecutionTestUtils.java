package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionExecutionTestUtils {

	public static ActionExecutionManager createManager(final ProjectContext context) {
		final ContextProviderService contextProvider = mock(ContextProviderService.class);
		when(contextProvider.getCurrent()).thenReturn(context);
		return new ActionExecutionManager(contextProvider, Mockito.mock(ActionExecutionListener.class));
	}

}
