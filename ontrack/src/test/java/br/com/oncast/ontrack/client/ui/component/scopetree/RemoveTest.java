package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;

import com.octo.gwt.test.GwtTest;

public class RemoveTest extends GwtTest {

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
		firstScope.add(new Scope("1.1"));
		rootScope.add(firstScope);
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("2"));

		return new ProjectContext(new Project(projectScope, null));
	}

	private ProjectContext getUnmodifiedContext() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1").add(new Scope("1.1")));
		unmodifiedScope.add(new Scope("2"));

		return new ProjectContext(new Project(unmodifiedScope, null));
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

	@Test
	public void shouldRemoveItem() throws ActionNotFoundException {
		final ScopeRemoveAction removeAction = new ScopeRemoveAction(firstScope.getId());
		actionExecutionService.onUserActionExecutionRequest(removeAction);

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotRemoveRoot() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeRemoveAction(rootScope.getId()));
	}

	@Test
	public void shouldInsertRemovedItemAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeRemoveAction(firstScope.getId()));

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
