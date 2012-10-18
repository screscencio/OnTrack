package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ScopeUpdateActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private ProjectContext context;

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(Long.MAX_VALUE));

		rootScope = ScopeTestUtils.createScope("root");
		firstChild = ScopeTestUtils.createScope("first");
		rootScope.add(firstChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test
	public void updateActionChangeScopeDescription() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		new ScopeUpdateAction(rootScope.getId(), "new text").execute(context, actionContext);
		assertEquals("new text", rootScope.getDescription());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());

		final ScopeUpdateAction updateScopeAction = new ScopeUpdateAction(rootScope.getId(), "new text");
		final ModelAction rollbackAction = updateScopeAction.execute(context, actionContext);

		assertEquals("new text", rootScope.getDescription());

		rollbackAction.execute(context, actionContext);
		assertEquals("root", rootScope.getDescription());
	}

}
