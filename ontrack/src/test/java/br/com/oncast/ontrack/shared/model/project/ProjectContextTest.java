package br.com.oncast.ontrack.shared.model.project;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// TODO Create tests for other methods. Now it is only testing release search.
public class ProjectContextTest {

	private ProjectContext context;

	@Mock
	private Release releaseMock;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		final Scope scopeMock = createAndConfigureScopeMock();

		context = ProjectTestUtils.createProjectContext(scopeMock, releaseMock);
	}

	private Scope createAndConfigureScopeMock() {
		final Scope scopeMock = mock(Scope.class);
		final Progress progressMock = mock(Progress.class);

		when(scopeMock.getProgress()).thenReturn(progressMock);
		when(progressMock.getDescription()).thenReturn("");
		when(scopeMock.getChildren()).thenReturn(new ArrayList<Scope>());

		return scopeMock;
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
}
