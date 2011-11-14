package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;

import com.octo.gwt.test.GwtTest;

public class UpdateTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
	}

	@AfterClass
	public static void afterClass() {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
	}

	@Before
	public void setUp() {
		scope = getScope();
		tree = new ScopeTree();
		tree.setContext(new ProjectContext(new Project(scope, null)));

		projectContext = new ProjectContext((new Project(scope, ReleaseFactoryTestUtil.create(""))));
		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = new Scope("Project");
		firstScope = new Scope("1");
		rootScope.add(firstScope);
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("3"));
		projectScope.add(new Scope("2"));

		return new ProjectContext(new Project(projectScope, null));
	}

	private ProjectContext getModifiedRootContext() {
		final Scope projectScope = new Scope("Root");
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("2"));

		return new ProjectContext(new Project(projectScope, null));
	}

	private ProjectContext getUnmodifieldContext() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1"));
		unmodifiedScope.add(new Scope("2"));

		return new ProjectContext(new Project(unmodifiedScope, null));
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getUnmodifieldContext());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedContext());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedRootTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedRootContext());
		return treeAfterManipulation;
	}

	@Test
	public void shouldUpdateScopeWithNewValue() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeUpdateAction(firstScope.getId(), "3"));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test
	public void shouldUpdateRootScope() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeUpdateAction(rootScope.getId(), "Root"));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedRootContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedRootTree());
	}

	@Test
	public void shouldRollbackUpdatedScope() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeUpdateAction(firstScope.getId(), "3"));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());

		actionExecutionService.onUserActionUndoRequest();

		DeepEqualityTestUtils.assertObjectEquality(scope, getUnmodifieldContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getUnmodifiedTree());
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
