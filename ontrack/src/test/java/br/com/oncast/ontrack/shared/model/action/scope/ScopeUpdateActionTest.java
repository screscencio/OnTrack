package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ScopeUpdateActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("first");
		rootScope.add(firstChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test
	public void updateActionChangeScopeDescription() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		new ScopeUpdateAction(rootScope.getId(), "new text").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("new text", rootScope.getDescription());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());

		final ScopeUpdateAction updateScopeAction = new ScopeUpdateAction(rootScope.getId(), "new text");
		final ModelAction rollbackAction = updateScopeAction.execute(context, Mockito.mock(ActionContext.class));

		assertEquals("new text", rootScope.getDescription());

		rollbackAction.execute(context, Mockito.mock(ActionContext.class));
		assertEquals("root", rootScope.getDescription());
	}

}
