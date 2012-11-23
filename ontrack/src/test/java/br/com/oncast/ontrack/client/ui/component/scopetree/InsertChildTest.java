package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServiceProviderTestUtils;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import com.googlecode.gwt.test.GwtTest;

public class InsertChildTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private String newScopeDescription;

	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClientServiceProviderTestUtils.configure().mockEssential();
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
	}

	@AfterClass
	public static void afterClass() throws Exception {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
		ClientServiceProviderTestUtils.reset();
	}

	@Before
	public void setUp() throws Exception {

		scope = getScope();

		tree = new ScopeTree();
		tree.setContext(ProjectTestUtils.createProjectContext(scope, null));

		newScopeDescription = "description for new scope";
		projectContext = ProjectTestUtils.createProjectContext(scope, ReleaseFactoryTestUtil.create(""));
		projectContext.addUser(UserTestUtils.getAdmin());

		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() throws Exception {
		rootScope = ScopeTestUtils.createScope("Project");
		firstScope = ScopeTestUtils.createScope("1");
		rootScope.add(firstScope);
		rootScope.add(ScopeTestUtils.createScope("2"));

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		projectScope.add(ScopeTestUtils.createScope("1").add(ScopeTestUtils.createScope(newScopeDescription)));
		projectScope.add(ScopeTestUtils.createScope("2"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getModifiedContextForRootChild() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		projectScope.add(ScopeTestUtils.createScope("1"));
		projectScope.add(ScopeTestUtils.createScope("2"));
		projectScope.add(ScopeTestUtils.createScope(newScopeDescription));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedContext() {
		final Scope unmodifiedScope = ScopeTestUtils.createScope("Project");
		unmodifiedScope.add(ScopeTestUtils.createScope("1"));
		unmodifiedScope.add(ScopeTestUtils.createScope("2"));

		return ProjectTestUtils.createProjectContext(unmodifiedScope, null);
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getUnmodifiedContext());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedContext());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTreeForRootChild() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedContextForRootChild());
		return treeAfterManipulation;
	}

	@Test
	public void shouldInsertChild() throws ActionNotFoundException, DeepEqualityException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeInsertChildAction(firstScope.getId(), newScopeDescription));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test
	public void shouldInsertChildForRoot() throws ActionNotFoundException, DeepEqualityException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeInsertChildAction(rootScope.getId(), newScopeDescription));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContextForRootChild().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTreeForRootChild());
	}

	@Test
	public void shouldRemoveInsertedChildAfterUndo() throws ActionNotFoundException, DeepEqualityException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeInsertChildAction(firstScope.getId(), newScopeDescription));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());

		actionExecutionService.onUserActionUndoRequest();

		DeepEqualityTestUtils.assertObjectEquality(scope, getUnmodifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getUnmodifiedTree());
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
