package br.com.oncast.ontrack.shared.model.project;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static junit.framework.Assert.assertEquals;
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
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

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
		final Release expectedRelease = ReleaseFactoryTestUtil.create("R1");
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
		final Release expectedRelease = ReleaseFactoryTestUtil.create("R1");
		when(releaseMock.findRelease("R1")).thenReturn(expectedRelease);

		assertEquals(expectedRelease, context.findRelease("project release/R1"));
		verify(releaseMock).findRelease("R1");
	}

	@Test
	public void findReleaseShouldAcceptADescriptionWithoutProjectRelease() throws Exception {
		when(releaseMock.getDescription()).thenReturn("project release");
		final Release expectedRelease = ReleaseFactoryTestUtil.create("R1");
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
		when(proj.getKanban(release)).thenReturn(KanbanFactory.createFor(release));

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
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);

		context.findAnnotation(new UUID(), annotatedObjectId);
	}

	@Test(expected = AnnotationNotFoundException.class)
	public void shouldNotFindTheAnnotationWhenTheAnnotatedObjectIdIsDifferent() throws Exception {
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);

		context.findAnnotation(annotation.getId(), new UUID());
	}

	@Test
	public void shouldBeAbleToFindAnnotationThatAddedPreviouslyById() throws Exception {
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);
		context.addAnnotation(annotation2, annotatedObjectId);

		assertEquals(annotation, context.findAnnotation(annotation.getId(), annotatedObjectId));
		assertEquals(annotation2, context.findAnnotation(annotation2.getId(), annotatedObjectId));
	}

	@Test
	public void shouldNotThrowAnyExceptionsWhenRemovingAAnnotationThatCantBeFound() throws Exception {
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();

		context.removeAnnotation(AnnotationTestUtils.create(), new UUID());

		context.addAnnotation(annotation, annotatedObjectId);

		context.removeAnnotation(annotation, new UUID());
		context.removeAnnotation(AnnotationTestUtils.create(), annotatedObjectId);
	}

	@Test
	public void removedAnnotationshouldNotBeFoundById() throws Exception {
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);
		context.addAnnotation(annotation2, annotatedObjectId);

		assertEquals(annotation, context.findAnnotation(annotation.getId(), annotatedObjectId));
		assertEquals(annotation2, context.findAnnotation(annotation2.getId(), annotatedObjectId));

		context.removeAnnotation(annotation2, annotatedObjectId);

		try {
			context.findAnnotation(annotation2.getId(), annotatedObjectId);
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
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);
		context.addAnnotation(annotation2, annotatedObjectId);

		final List<Annotation> annotationsForAnnotatedObject = context.findAnnotationsFor(annotatedObjectId);
		assertTrue(annotationsForAnnotatedObject.contains(annotation));
		assertTrue(annotationsForAnnotatedObject.contains(annotation2));
	}

	@Test
	public void annotationsAddedLaterShouldComeFirstOnAnnotationsList() throws Exception {
		final UUID annotatedObjectId = new UUID();
		final Annotation annotation = AnnotationTestUtils.create();
		final Annotation annotation2 = AnnotationTestUtils.create();
		final Annotation annotation3 = AnnotationTestUtils.create();
		context.addAnnotation(annotation, annotatedObjectId);
		context.addAnnotation(annotation2, annotatedObjectId);
		context.addAnnotation(annotation3, annotatedObjectId);

		assertCollectionEquality(
				Arrays.asList(annotation3, annotation2, annotation),
				context.findAnnotationsFor(annotatedObjectId));
	}

	@Test(expected = UserNotFoundException.class)
	public void shouldNotFoundAUserThatWasntAdded() throws Exception {
		context.findUser(13L);
	}

	@Test
	public void shouldBeAbleToFindUsersThatWasAddedPreviously() throws Exception {
		final Set<User> userList = new HashSet<User>();
		final User user = UserTestUtils.createUser();
		userList.add(user);
		final User user2 = UserTestUtils.createUser();
		userList.add(user2);

		context = ProjectTestUtils.createProjectContext(scopeMock, releaseMock, userList);

		assertEquals(user, context.findUser(user.getId()));
		assertEquals(user2, context.findUser(user2.getId()));
	}
}
