package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeCopyToActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeCopyToAction;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.TagTestUtils;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;
import br.com.oncast.ontrack.utils.model.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.model.DescriptionTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScopeCopyToActionTest extends ModelActionTest {

	private Scope sourceScope;

	private Scope targetedParent;

	private int targetedIndex;

	private UUID targetedParentId;

	@Before
	public void setup() throws Exception {
		targetedParent = spy(ScopeTestUtils.createScope());
		targetedParentId = targetedParent.getId();
		targetedIndex = 0;
		sourceScope = ScopeTestUtils.createScope();
		when(context.findScope(sourceScope.getId())).thenReturn(sourceScope);
		when(context.findScope(targetedParentId)).thenReturn(targetedParent);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				final Scope scope = (Scope) invocation.getArguments()[1];
				targetedParent.add(scope);
				when(context.findScope(scope.getId())).thenReturn(scope);
				return null;
			}
		}).when(targetedParent).add(anyInt(), Mockito.any(Scope.class));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				final UUID subjectId = (UUID) invocation.getArguments()[0];
				final Checklist checklist = (Checklist) invocation.getArguments()[1];
				when(context.findChecklist(subjectId, checklist.getId())).thenReturn(checklist);
				return null;
			}
		}).when(context).addChecklist(any(UUID.class), any(Checklist.class));
		targetedIndex = 0;
	}

	@Test
	public void shouldBeAbleToCopyASimpleScope() throws Exception {
		executeAction();
		final Scope copy = captureCopiedScope();
		assertEquals(sourceScope.getDescription(), copy.getDescription());
	}

	@Test
	public void theCopiedScopeShouldNotHaveSameIdAsTheSourceScope() throws Exception {
		executeAction();
		final Scope copy = captureCopiedScope();
		assertFalse(sourceScope.getId().equals(copy.getId()));
	}

	@Test
	public void shouldKeepTheSameIdOnConsecutiveExecutions() throws Exception {
		final ModelAction action = getNewInstance();
		action.execute(context, actionContext);
		final UUID firstId = captureCopiedScope().getId();

		for (int i = 0; i < 10; i++) {
			action.execute(context, actionContext);
			assertEquals(firstId, captureCopiedScope().getId());
		}
	}

	@Test
	public void shouldCopyTheDeclaredEffortIfAny() throws Exception {
		final int declaredEffort = 12;
		ScopeTestUtils.declareEffort(sourceScope, declaredEffort);
		executeAction();
		final Scope copy = captureCopiedScope();
		assertEquals(declaredEffort, copy.getEffort().getDeclared(), 0);
	}

	@Test
	public void shouldCopyTheEffortDeclaredOnFirstExecution() throws Exception {
		final int declaredEffort = 12;
		ScopeTestUtils.declareEffort(sourceScope, declaredEffort);
		final ModelAction action = getNewInstance();
		action.execute(context, actionContext);

		for (int i = 0; i < 5; i++) {
			ScopeTestUtils.declareEffort(sourceScope, i);
			action.execute(context, actionContext);
			assertEquals(declaredEffort, captureCopiedScope().getEffort().getDeclared(), 0);
		}
	}

	@Test
	public void shouldCopyTheDeclaredValueIfAny() throws Exception {
		final int declaredValue = 18;
		ScopeTestUtils.declareValue(sourceScope, declaredValue);
		executeAction();
		final Scope copy = captureCopiedScope();
		assertEquals(declaredValue, copy.getValue().getDeclared(), 0);
	}

	@Test
	public void shouldCopyTheValueDeclaredOnFirstExecution() throws Exception {
		final int declaredValue = 3;
		ScopeTestUtils.declareValue(sourceScope, declaredValue);
		final ModelAction action = getNewInstance();
		action.execute(context, actionContext);

		for (int i = 0; i < 5; i++) {
			ScopeTestUtils.declareValue(sourceScope, i);
			action.execute(context, actionContext);
			assertEquals(declaredValue, captureCopiedScope().getValue().getDeclared(), 0);
		}
	}

	@Test
	public void shouldCopyTheItemDescription() throws Exception {
		final Description description = DescriptionTestUtils.create();
		when(context.findDescriptionFor(sourceScope.getId())).thenReturn(description);
		when(context.hasDescriptionFor(sourceScope.getId())).thenReturn(true);
		executeAction();
		final Scope copy = captureCopiedScope();
		final ArgumentCaptor<Description> captor = ArgumentCaptor.forClass(Description.class);
		verify(context).addDescription(captor.capture(), eq(copy.getId()));
		assertEquals(description.getDescription(), captor.getValue().getDescription());
	}

	@Test
	public void shouldCopyAllChecklists() throws Exception {
		final List<Checklist> checklists = Arrays.asList(ChecklistTestUtils.create(), ChecklistTestUtils.create());
		when(context.findChecklistsFor(sourceScope.getId())).thenReturn(checklists);
		executeAction();

		final Scope copy = captureCopiedScope();
		final List<Checklist> checklistsCopy = captureAddedChecklists(copy.getId());
		assertChecklistsCopy(checklists, checklistsCopy);
	}

	@Test
	public void shouldCopyAllChecklistItems() throws Exception {
		final List<Checklist> checklists = Arrays.asList(ChecklistTestUtils.createWithItems(4), ChecklistTestUtils.createWithItems(2));
		when(context.findChecklistsFor(sourceScope.getId())).thenReturn(checklists);
		executeAction();

		final Scope copy = captureCopiedScope();
		final List<Checklist> checklistsCopy = captureAddedChecklists(copy.getId());
		assertChecklistsCopy(checklists, checklistsCopy);
	}

	@Test
	public void allCopiedChecklistItemsShouldBeUnchecked() throws Exception {
		final List<Checklist> checklists = Arrays.asList(ChecklistTestUtils.createWithItems(true, false, true, true), ChecklistTestUtils.createWithItems(2));
		when(context.findChecklistsFor(sourceScope.getId())).thenReturn(checklists);
		executeAction();

		final Scope copy = captureCopiedScope();
		final List<Checklist> checklistsCopy = captureAddedChecklists(copy.getId());
		assertChecklistsCopy(checklists, checklistsCopy);
	}

	@Test
	public void shouldCopyAllTags() throws Exception {
		final List<Tag> tags = Arrays.asList(TagTestUtils.createTag(), TagTestUtils.createTag(), TagTestUtils.createTag());
		when(context.getTagsFor(sourceScope)).thenReturn(tags);
		for (final Tag tag : tags) {
			when(context.findTag(tag.getId())).thenReturn(tag);
		}
		executeAction();

		final Scope copy = captureCopiedScope();
		final List<Tag> tagsCopy = captureTagsAddedTo(copy);
		AssertTestUtils.assertCollectionEquality(tags, tagsCopy);
	}

	@Test
	public void shouldCopyAllItsDescendants() throws Exception {
		final ProjectContext context = ProjectTestUtils.createProjectContext();
		final Scope root = context.getProjectScope();
		root.add(sourceScope);
		final List<Scope> children = Arrays.asList(ScopeTestUtils.createScope(), ScopeTestUtils.createScope(), ScopeTestUtils.createScope());
		for (final Scope child : children) {
			sourceScope.add(child);
		}
		final Scope parentScope = ScopeTestUtils.createScope();
		root.add(parentScope);
		final ScopeCopyToAction action = new ScopeCopyToAction(sourceScope.getId(), parentScope.getId(), 0);
		action.execute(context, actionContext);
		final Scope copy = context.findScope(action.getNewScopeId());
		final List<Scope> obtainedChildren = copy.getChildren();
		assertEquals(children.size(), obtainedChildren.size());
		for (int i = 0; i < children.size(); i++) {
			final Scope expectedChild = children.get(i);
			final Scope obtainedChild = obtainedChildren.get(i);
			assertFalse(expectedChild.getId().equals(obtainedChild.getId()));
			assertEquals(expectedChild.getDescription(), obtainedChild.getDescription());
		}
	}

	@Test
	public void undoShouldRemoveTheCopiedScope() throws Exception {
		final ModelAction undoAction = executeAction();
		final Scope copy = captureCopiedScope();
		undoAction.execute(context, actionContext);
		verify(targetedParent).remove(copy);
	}

	@Test
	public void redoShouldAddTheRemovedCopyScopeAgain() throws Exception {
		final ModelAction undoAction = executeAction();
		final Scope copy = captureCopiedScope();
		final ModelAction redoAction = undoAction.execute(context, actionContext);
		redoAction.execute(context, actionContext);
		verify(targetedParent, times(2)).add(targetedIndex, copy);
	}

	private List<Tag> captureTagsAddedTo(final Scope copy) {
		final ArgumentCaptor<TagAssociationMetadata> captor = ArgumentCaptor.forClass(TagAssociationMetadata.class);
		verify(context, atLeastOnce()).addMetadata(captor.capture());
		final List<Tag> tags = new ArrayList<Tag>();
		for (final TagAssociationMetadata m : captor.getAllValues()) {
			tags.add(m.getTag());
		}
		return tags;
	}

	private void assertChecklistsCopy(final List<Checklist> expected, final List<Checklist> obtained) {
		assertEquals(expected.size(), obtained.size());
		for (int i = 0; i < expected.size(); i++) {
			final Checklist expectedCL = expected.get(i);
			final Checklist obtainedCL = obtained.get(i);
			assertEquals(expectedCL.getTitle(), obtainedCL.getTitle());
			assertFalse(expectedCL.getId().equals(obtainedCL.getId()));
			assertChecklistItemsCopy(expectedCL.getItems(), obtainedCL.getItems());
		}
	}

	private void assertChecklistItemsCopy(final List<ChecklistItem> expected, final List<ChecklistItem> obtained) {
		assertEquals(expected.size(), obtained.size());
		for (int i = 0; i < expected.size(); i++) {
			final ChecklistItem expectedItem = expected.get(i);
			final ChecklistItem obtainedItem = obtained.get(i);
			assertEquals(expectedItem.getDescription(), obtainedItem.getDescription());
			assertFalse(expectedItem.getId().equals(obtainedItem.getId()));
			assertFalse(obtainedItem.isChecked());
		}
	}

	private List<Checklist> captureAddedChecklists(final UUID scopeId) {
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context, atLeastOnce()).addChecklist(eq(scopeId), captor.capture());
		return captor.getAllValues();
	}

	private Scope captureCopiedScope() throws Exception {
		final ArgumentCaptor<Scope> captor = ArgumentCaptor.forClass(Scope.class);
		verify(targetedParent, atLeast(1)).add(eq(targetedIndex), captor.capture());
		final Scope copy = captor.getValue();
		return copy;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeCopyToAction(sourceScope.getId(), targetedParentId, targetedIndex);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeCopyToAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeCopyToActionEntity.class;
	}

}
