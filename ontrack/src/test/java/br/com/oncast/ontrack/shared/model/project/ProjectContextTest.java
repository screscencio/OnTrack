package br.com.oncast.ontrack.shared.model.project;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.model.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.gwt.dev.util.collect.HashSet;

// TODO Create tests for other methods. Now it is only testing release search.
public class ProjectContextTest {

	private ProjectContext context;

	@Mock
	private Release releaseMock;

	@Mock
	private Scope scopeMock;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		configureScopeMock();

		context = ProjectTestUtils.createProjectContext(scopeMock, releaseMock);
	}

	private void configureScopeMock() {
		final Progress progressMock = mock(Progress.class);

		when(scopeMock.getProgress()).thenReturn(progressMock);
		when(progressMock.getDescription()).thenReturn("");
		when(scopeMock.getChildren()).thenReturn(new ArrayList<Scope>());
	}

	@Test
	public void findReleaseShouldReturnARelease() throws Exception {
		final Release expectedRelease = ReleaseTestUtils.createRelease("R1");
		when(releaseMock.findRelease(anyString())).thenReturn(expectedRelease);

		assertEquals(expectedRelease, context.findRelease("R1"));
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void findReleaseShouldThrowAnExceptionIfNoReleaseIsFound() throws Exception {
		when(releaseMock.findRelease(anyString())).thenThrow(new ReleaseNotFoundException());
		context.findRelease("R1");
	}

	@Test
	public void findReleaseShouldReturnNullWhenAnEmptyReleaseDescriptionIsPassed() throws Exception {
		final String nullString = null;

		assertEquals(null, context.findRelease(""));
		assertEquals(null, context.findRelease(" "));
		assertEquals(null, context.findRelease(nullString));
	}

	@Test
	public void findReleaseShouldReturnNullWhenNullIsPassed() throws Exception {
		final String nullString = null;
		assertEquals(null, context.findRelease(nullString));
	}

	@Test
	public void findReleaseShouldAcceptProjectReleaseInDescriptionButShouldIgnoreIt() throws Exception {
		when(releaseMock.getDescription()).thenReturn("project release");
		final Release expectedRelease = ReleaseTestUtils.createRelease("R1");
		when(releaseMock.findRelease("R1")).thenReturn(expectedRelease);

		assertEquals(expectedRelease, context.findRelease("project release/R1"));
		verify(releaseMock).findRelease("R1");
	}

	@Test
	public void findReleaseShouldAcceptADescriptionWithoutProjectRelease() throws Exception {
		when(releaseMock.getDescription()).thenReturn("project release");
		final Release expectedRelease = ReleaseTestUtils.createRelease("R1");
		when(releaseMock.findRelease("R1")).thenReturn(expectedRelease);

		assertEquals(expectedRelease, context.findRelease("R1"));
		verify(releaseMock).findRelease("R1");
	}

	@Test
	public void shouldGiveSavedKanbanWhenProjectHasOne() throws Exception {
		final Project proj = mock(Project.class);
		when(proj.getProjectScope()).thenReturn(ScopeTestUtils.createScope());
		final ProjectContext ctx = new ProjectContext(proj);

		final Release release = ReleaseTestUtils.createRelease();
		when(proj.hasKanbanFor(release)).thenReturn(true);
		when(proj.getKanban(release)).thenReturn(KanbanFactory.create());

		ctx.getKanban(release);

		verify(proj, atLeastOnce()).getKanban(release);
	}

	@Test
	public void shouldCreateAKanbanWhenThereIsNoSavedOne() throws Exception {
		final Project proj = mock(Project.class);
		when(proj.getProjectScope()).thenReturn(ScopeTestUtils.createScope());
		final ProjectContext ctx = new ProjectContext(proj);

		final Release release = ReleaseTestUtils.createRelease();
		when(proj.hasKanbanFor(release)).thenReturn(false);

		ctx.getKanban(release);

		verify(proj, never()).getKanban(release);
	}

	@Test
	public void shouldInferKanbanWhenTheKanbanIsNotLocked() throws Exception {
		final Project proj = mock(Project.class);
		when(proj.getProjectScope()).thenReturn(ScopeTestUtils.createScope());
		final ProjectContext ctx = new ProjectContext(proj);

		final Release release = ReleaseTestUtils.createRelease();
		when(proj.hasKanbanFor(release)).thenReturn(true);
		final Kanban kanban = Mockito.mock(Kanban.class);
		when(proj.getKanban(release)).thenReturn(kanban);
		when(kanban.isLocked()).thenReturn(false);

		ctx.getKanban(release);

		verify(kanban).merge(Mockito.any(Kanban.class));
	}

	@Test
	public void shouldNotInferKanbanWhenTheKanbanIsLocked() throws Exception {
		final Project proj = mock(Project.class);
		when(proj.getProjectScope()).thenReturn(ScopeTestUtils.createScope());
		final ProjectContext ctx = new ProjectContext(proj);

		final Release release = ReleaseTestUtils.createRelease();
		when(proj.hasKanbanFor(release)).thenReturn(true);
		final Kanban kanban = Mockito.mock(Kanban.class);
		when(proj.getKanban(release)).thenReturn(kanban);
		when(kanban.isLocked()).thenReturn(true);

		ctx.getKanban(release);

		verify(kanban, never()).merge(Mockito.any(Kanban.class));
	}

	@Test(expected = AnnotationNotFoundException.class)
	public void shouldNotFoundTheAnnotationWhenThereIsNoAnnotation() throws Exception {
		context.findAnnotation(new UUID(), new UUID());
	}

	@Test(expected = AnnotationNotFoundException.class)
	public void shouldNotFindTheAnnotationWhenTheAnnotationIdIsDifferent() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);

		context.findAnnotation(subjectId, new UUID());
	}

	@Test(expected = AnnotationNotFoundException.class)
	public void shouldNotFindTheAnnotationWhenTheAnnotatedObjectIdIsDifferent() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);

		context.findAnnotation(new UUID(), annotation.getId());
	}

	@Test
	public void shouldBeAbleToFindAnnotationThatAddedPreviouslyById() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);
		context.addAnnotation(subjectId, annotation2);

		assertEquals(annotation, context.findAnnotation(subjectId, annotation.getId()));
		assertEquals(annotation2, context.findAnnotation(subjectId, annotation2.getId()));
	}

	@Test
	public void shouldNotThrowAnyExceptionsWhenRemovingAAnnotationThatCantBeFound() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();

		context.removeAnnotation(new UUID(), AnnotationTestUtils.create());

		context.addAnnotation(subjectId, annotation);

		context.removeAnnotation(new UUID(), annotation);
		context.removeAnnotation(subjectId, AnnotationTestUtils.create());
	}

	@Test
	public void removedAnnotationshouldNotBeFoundById() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);
		context.addAnnotation(subjectId, annotation2);

		assertEquals(annotation, context.findAnnotation(subjectId, annotation.getId()));
		assertEquals(annotation2, context.findAnnotation(subjectId, annotation2.getId()));

		context.removeAnnotation(subjectId, annotation2);

		try {
			context.findAnnotation(subjectId, annotation2.getId());
			fail();
		}
		catch (final AnnotationNotFoundException e) {}
	}

	@Test
	public void shouldReturnAnEmptyListWhenThereIsNoAnnotationForTheGivenObject() throws Exception {
		assertTrue(context.findAnnotationsFor(new UUID()).isEmpty());
	}

	@Test
	public void shouldBeAbleToRetrieveAllAnnotationsForAGivenObjectId() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);
		context.addAnnotation(subjectId, annotation2);

		final List<Annotation> annotationsForAnnotatedObject = context.findAnnotationsFor(subjectId);
		assertTrue(annotationsForAnnotatedObject.contains(annotation));
		assertTrue(annotationsForAnnotatedObject.contains(annotation2));
	}

	@Test
	public void annotationsAddedLaterShouldComeFirstOnAnnotationsList() throws Exception {
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		final Annotation annotation3 = AnnotationTestUtils.create();
		context.addAnnotation(subjectId, annotation);
		context.addAnnotation(subjectId, annotation2);
		context.addAnnotation(subjectId, annotation3);

		assertCollectionEquality(
				Arrays.asList(annotation3, annotation2, annotation),
				context.findAnnotationsFor(subjectId));
	}

	@Test(expected = UserNotFoundException.class)
	public void shouldNotFoundAUserThatWasntAdded() throws Exception {
		context.findUser(new UUID());
	}

	@Test
	public void shouldBeAbleToFindUsersThatWasAddedPreviously() throws Exception {
		final Set<UserRepresentation> userList = new HashSet<UserRepresentation>();
		final UserRepresentation user = UserRepresentationTestUtils.createUser();
		userList.add(user);
		final UserRepresentation user2 = UserRepresentationTestUtils.createUser();
		userList.add(user2);

		context = ProjectTestUtils.createProjectContext(scopeMock, releaseMock, userList);

		assertEquals(user, context.findUser(user.getId()));
		assertEquals(user2, context.findUser(user2.getId()));
	}

	@Test
	public void shouldBeAbleToRetrieveTheAddedChecklistByItsIdAndTheSubjectId() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist addedChecklist = createAndAddChecklist(subjectId);
		assertEquals(addedChecklist, context.findChecklist(subjectId, addedChecklist.getId()));
	}

	@Test
	public void addingTheSameChecklistTwiceShouldNotDuplicate() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist checklist = createAndAddChecklist(subjectId);
		for (int i = 0; i < 10; i++) {
			context.addChecklist(subjectId, checklist);
		}
		assertEquals(checklist, context.findChecklist(subjectId, checklist.getId()));
		assertEquals(1, context.findChecklistsFor(subjectId).size());
	}

	@Test
	public void shouldBeAbleToRetrieveAllCheckblistsForAGivenSubjectId() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist checklist1 = createAndAddChecklist(subjectId);
		final Checklist checklist2 = createAndAddChecklist(subjectId);
		final Checklist checklist3 = createAndAddChecklist(subjectId);

		assertEquals(checklist1, context.findChecklist(subjectId, checklist1.getId()));
		assertEquals(checklist2, context.findChecklist(subjectId, checklist2.getId()));
		assertEquals(checklist3, context.findChecklist(subjectId, checklist3.getId()));

		AssertTestUtils.assertContainsAll(context.findChecklistsFor(subjectId), checklist1, checklist2, checklist3);
	}

	@Test(expected = ChecklistNotFoundException.class)
	public void shouldThrowChecklistNotFoundExceptionWhenThereIsNoChecklistForTheGivenIdAndSubjectId() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		context.findChecklist(new UUID(), new UUID());
	}

	@Test(expected = ChecklistNotFoundException.class)
	public void shouldThrowChecklistNotFoundExceptionEvenWhenThereIsAChecklistWithTheGivenIdButItIsNotAssociatedWithTheGivenSubjectId() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist checklist = createAndAddChecklist(subjectId);
		context.findChecklist(new UUID(), checklist.getId());
	}

	@Test(expected = ChecklistNotFoundException.class)
	public void shouldThrowChecklistNotFoundExceptionEvenWhenThereIsAChecklistAssociatedWithTheGivenSubjectIdButTheChecklistIdIsDifferentFromTheGivenOne()
			throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		createAndAddChecklist(subjectId);

		context.findChecklist(subjectId, new UUID());
	}

	@Test
	public void shouldReturnAEmptyListWhenThereIsNoChecklistForTheGivenSubject() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		assertTrue(context.findChecklistsFor(new UUID()).isEmpty());
	}

	@Test
	public void shouldBeAbleToRemoveAPreviouslyAddedChecklist() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist checklist = createAndAddChecklist(subjectId);

		assertEquals(checklist, context.findChecklist(subjectId, checklist.getId()));

		context.removeChecklist(subjectId, checklist);
		try {
			context.findChecklist(subjectId, checklist.getId());
			fail("Checklist was not removed from the context.");
		}
		catch (final ChecklistNotFoundException e) {
			assertTrue(true);
		}
	}

	@Test
	public void shouldNotHaveAnnotationsWhenNoAnnotationWhereAdded() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		assertFalse(context.hasAnnotationsFor(new UUID()));
	}

	@Test
	public void shouldHaveAnnotationsWhenThereAreAddedAnnotations() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		context.addAnnotation(subjectId, AnnotationTestUtils.create());
		assertTrue(context.hasAnnotationsFor(subjectId));
	}

	@Test
	public void shouldNotHaveAnnotationsWhenAllTheAnnotationsWhereRemoved() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();

		context.addAnnotation(subjectId, annotation);
		context.removeAnnotation(subjectId, annotation);

		assertFalse(context.hasAnnotationsFor(subjectId));
	}

	@Test
	public void shouldNotHaveChecklistsWhenNoChecklistWhereAdded() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		assertFalse(context.hasChecklistsFor(new UUID()));
	}

	@Test
	public void shouldHaveChecklistsWhenThereAreAddedChecklists() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		context.addChecklist(subjectId, ChecklistTestUtils.create());
		assertTrue(context.hasChecklistsFor(subjectId));
	}

	@Test
	public void shouldNotHaveChecklistsWhenAllTheChecklistsWhereRemoved() throws Exception {
		context = ProjectTestUtils.createProjectContext();
		final UUID subjectId = new UUID();
		final Checklist checklist = ChecklistTestUtils.create();

		context.addChecklist(subjectId, checklist);
		context.removeChecklist(subjectId, checklist);

		assertFalse(context.hasChecklistsFor(subjectId));
	}

	private Checklist createAndAddChecklist(final UUID subjectId) {
		final Checklist checklist1 = ChecklistTestUtils.create();
		context.addChecklist(subjectId, checklist1);
		return checklist1;
	}
}
