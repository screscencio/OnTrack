package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.ui.generalwidgets.ShortcutLabel;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@GwtModule("br.com.oncast.ontrack.Application")
public class ScopeTreeInstructionGuideTest extends GwtTest {

	private ScopeTreeInstructionGuide guide;
	private HasInstructions targetWidget;
	private Scope projectScope;
	private ProjectContext context;

	@Before
	public void setup() {
		targetWidget = mock(HasInstructions.class);
		guide = new ScopeTreeInstructionGuide(targetWidget);
		guide.reset();

		projectScope = ScopeTestUtils.createScope();

		context = mock(ProjectContext.class);
		when(context.getProjectScope()).thenReturn(projectScope);
	}

	@Test
	public void notConfiguredGuideDoesNothing() throws Exception {
		executeAllActions();

		verify(targetWidget, never()).clearInstructions();
		verify(targetWidget, never()).addInstruction(Mockito.any(Label.class));
	}

	@Test
	public void instructionWhenThereIsNoScopes() {
		guide.onSetContext(context);

		assertAddedInstructions("CONTROL + ENTER to insert a child item");
	}

	@Test
	public void instructionWhenThereIsOnlyOneScope() throws Exception {
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		assertAddedInstructions("ENTER to insert below", "SHIFT + ENTER to insert above");
	}

	@Test
	public void instructionWhenTheScopesCountIsBetweenTwoAndFiveInOneLevel() throws Exception {
		addScopesTo(projectScope, 1);

		for (int i = 2; i <= 5; i++) {
			addScopesTo(projectScope, 1);
			assertEquals(i, projectScope.getChildCount());

			resetGuide();

			guide.onSetContext(context);
			assertAddedInstructions("CONTROL + ARROWS to move an Item");
		}
	}

	@Test
	public void instructionWhenTheScopesCountIsTwoInTwoLevels() throws Exception {
		addScopesTo(projectScope, 1);
		addScopesTo(projectScope.getChild(0), 1);

		guide.onSetContext(context);
		assertAddedInstructions("CONTROL + ARROWS to move an Item", "CONTROL + SHIFT + ENTER to insert a parent item");
	}

	@Test
	public void instructionWhenTheScopesCountIsBetweenThreeAndFiveInTwoLevels() throws Exception {
		addScopesTo(projectScope, 2);
		for (int i = 3; i <= 5; i++) {
			addScopesTo(projectScope.getChild(0), 1);

			assertEquals(i, projectScope.getAllDescendantScopes().size());
			resetGuide();

			guide.onSetContext(context);
			assertAddedInstructions("CONTROL + SHIFT + ENTER to insert a parent item");
		}
	}

	@Test
	public void thereAreNoInstructionWhenTheScopesCountIsMoreThanFiveInOneLevel() throws Exception {
		addScopesTo(projectScope, 6);
		assertTrue(projectScope.getChildCount() > 5);

		guide.onSetContext(context);
		assertAddedInstructions();
	}

	@Test
	public void thereAreNoInstructionWhenTheScopesCountIsMoreThanFiveInMultipleLevels() throws Exception {
		Scope current = projectScope;
		for (int i = 0; i < 6; i++) {
			current.add(current = ScopeTestUtils.createScope());
		}
		assertTrue(projectScope.getAllDescendantScopes().size() > 5);

		guide.onSetContext(context);
		assertAddedInstructions();
	}

	@Test
	public void scopeInsertChildActionWhenThereAreNoScopes() throws Exception {
		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertChildAction.class);
		assertAddedInstructions("ENTER to insert below", "SHIFT + ENTER to insert above");
	}

	@Test
	public void otherActionsWhenThereAreNoScopes() throws Exception {
		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAllActionsExcept(ScopeInsertChildAction.class);

		assertAddedInstructions();
	}

	@Test
	public void scopeInsertChildActionWhenThereIsOnlyOneScope() throws Exception {
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertChildAction.class);
		assertAddedInstructions("CONTROL + ARROWS to move an Item", "CONTROL + SHIFT + ENTER to insert a parent item");
	}

	@Test
	public void scopeInsertSiblingDownActionWhenThereIsOnlyOneScope() throws Exception {
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertSiblingDownAction.class);
		assertAddedInstructions("CONTROL + ARROWS to move an Item");
	}

	@Test
	public void scopeInsertSiblingUpActionWhenThereIsOnlyOneScope() throws Exception {
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		guide.onActionExecution(mock(ScopeInsertSiblingUpAction.class));
		assertAddedInstructions("CONTROL + ARROWS to move an Item");
	}

	@Test
	public void otherActionsWhenThereAreOnlyOneScopes() throws Exception {
		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		for (final ModelAction action : ActionTestUtils.getAllSubactionsOfType(ModelAction.class)) {
			if (action instanceof ScopeInsertSiblingAction || action instanceof ScopeInsertChildAction) continue;
			guide.onActionExecution(action);
		}

		assertAddedInstructions();
	}

	@Test
	public void scopeInsertChildActionWhenThereAreLessThanFiveScopesIsOneLevel() throws Exception {
		addScopesTo(projectScope, 3);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertChildAction.class);
		assertAddedInstructions("CONTROL + SHIFT + ENTER to insert a parent item");
	}

	@Test
	public void scopeInsertChildActionAfterScopeInsertSiblingActionWhenThereAreLessThanFiveScopesIsOneLevel() throws Exception {
		addScopesTo(projectScope, 1);
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertSiblingDownAction.class);
		assertAddedInstructions();

		executeAction(ScopeInsertSiblingUpAction.class);
		assertAddedInstructions();

		executeAction(ScopeInsertChildAction.class);
		assertAddedInstructions("CONTROL + SHIFT + ENTER to insert a parent item");
	}

	@Test
	public void actionsThatDoesntChangeTheCountNeitherDeepnessOfScopesDoesNotAffectWhenThereAreLessThanFiveScopesInOneLevel() throws Exception {
		addScopesTo(projectScope, 1);
		addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAction(ScopeInsertSiblingDownAction.class);
		assertAddedInstructions();

		for (final ModelAction action : ActionTestUtils.getAllSubactionsOfType(ModelAction.class)) {
			if (action instanceof ScopeInsertAction || action instanceof ScopeMoveRightAction) continue;
			guide.onActionExecution(action);
		}

		assertAddedInstructions();

		executeAction(ScopeInsertSiblingUpAction.class);
		assertAddedInstructions();

		for (final ModelAction action : ActionTestUtils.getAllSubactionsOfType(ModelAction.class)) {
			if (action instanceof ScopeInsertAction || action instanceof ScopeMoveRightAction) continue;
			guide.onActionExecution(action);
		}

		assertAddedInstructions();

		executeAction(ScopeInsertChildAction.class);
		assertAddedInstructions("CONTROL + SHIFT + ENTER to insert a parent item");
	}

	@Test
	public void scopeInsertChildActionWhenThereAreMoreThanFiveScopesIsOneLevel() throws Exception {
		final int moreThanFive = 6;
		final int previousScopesCount = 4;

		for (int i = 0; i < previousScopesCount; i++)
			addScopesTo(projectScope, 1);

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		for (int i = 0; i < moreThanFive - previousScopesCount; i++) {
			executeAction(ScopeInsertSiblingDownAction.class);
			assertAddedInstructions();
		}

		for (int i = 0; i < 10; i++) {
			executeAction(ScopeInsertChildAction.class);
			assertAddedInstructions();
		}
	}

	@Test
	public void anyActionAfterTwoLevelsOfScopesShouldClearAndDisableInstructions() throws Exception {
		addScopesTo(projectScope, 1);
		addScopesTo(projectScope, 1);
		for (int i = 3; i <= 5; i++) {
			projectScope.getChild(1).add(ScopeTestUtils.createScope());
		}

		guide.onSetContext(context);
		resetAddedInstructionsRegistry();

		executeAllActions();

		verify(targetWidget, atLeastOnce()).clearInstructions();
		assertAddedInstructions();
	}

	private void executeAllActions() {
		executeAllActionsExcept();
	}

	private void executeAllActionsExcept(final Class<? extends ModelAction>... classes) {
		for (final ModelAction action : ActionTestUtils.getAllSubactionsOfType(ModelAction.class)) {
			boolean shouldExecute = true;
			for (final Class<? extends ModelAction> clazz : classes) {
				if (clazz.isAssignableFrom(action.getClass())) {
					shouldExecute = false;
					break;
				}
			}
			if (shouldExecute) guide.onActionExecution(action);
		}
	}

	private void addScopesTo(final Scope scope, final int nOfScopesToBeAdded) {
		for (int i = 0; i < nOfScopesToBeAdded; i++) {
			scope.add(ScopeTestUtils.createScope());
		}
	}

	private void executeAction(final Class<? extends ModelAction> clazz) {
		guide.onActionExecution(mock(clazz));
	}

	private void resetAddedInstructionsRegistry() {
		reset(targetWidget);
	}

	private void resetGuide() {
		resetAddedInstructionsRegistry();
		guide.reset();
	}

	private void assertAddedInstructions(final String... expectedInstructions) {
		final ArgumentCaptor<Label> captor = ArgumentCaptor.forClass(Label.class);
		verify(targetWidget, times(expectedInstructions.length)).addInstruction(captor.capture());

		final List<Label> actual = captor.getAllValues();
		for (int i = 0; i < expectedInstructions.length; i++) {
			assertEquals(new ShortcutLabel(expectedInstructions[i]).getText(), actual.get(i).getText());
		}
	}
}
