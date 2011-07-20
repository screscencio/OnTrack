package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.mocks.ContextProviderServiceMock;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;

import com.octo.gwt.test.GwtTest;

public class InsertSiblingUpTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionService actionExecutionService;
	private String newScopeDescription;

	@BeforeClass
	public static void beforeClass() {
		DeepEqualityTestUtils.addCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
	}

	@AfterClass
	public static void afterClass() {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparatorFor(Effort.class);
	}

	@Before
	public void setUp() {
		scope = getScope();
		tree = new ScopeTree();
		tree.setScope(scope);

		newScopeDescription = "description for new scope";

		projectContext = new ProjectContext((new Project(scope, new Release(""))));
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		actionExecutionService = new ActionExecutionService(contextService);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = new Scope("Project");
		firstScope = new Scope("1");
		rootScope.add(firstScope);
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	private Scope getModifiedScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope(newScopeDescription));
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("2"));

		return projectScope;
	}

	private Scope getUnmodifiedScope() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1"));
		unmodifiedScope.add(new Scope("2"));

		return unmodifiedScope;
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getUnmodifiedScope());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getModifiedScope());
		return treeAfterManipulation;
	}

	@Test
	public void shouldInsertSiblingUp() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertSiblingUpAction(firstScope.getId(), newScopeDescription));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotInsertSiblingUpForRoot() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertSiblingUpAction(rootScope.getId(), newScopeDescription));
	}

	@Test
	public void shouldRemoveInsertedSiblingAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertSiblingUpAction(firstScope.getId(), newScopeDescription));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());

		actionExecutionService.onActionUndoRequest();

		DeepEqualityTestUtils.assertObjectEquality(scope, getUnmodifiedScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getUnmodifiedTree());
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
