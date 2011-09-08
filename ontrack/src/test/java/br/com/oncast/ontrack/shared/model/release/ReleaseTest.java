package br.com.oncast.ontrack.shared.model.release;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseTest {

	@Test
	public void shouldFindAReleaseByDescription() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseMock.getRelease();
		final Release releaseR1 = rootRelease.getChild(0);
		final Release foundRelease = rootRelease.findRelease(releaseR1.getDescription());

		assertNotNull(foundRelease);
		assertEquals(releaseR1, foundRelease);
	}

	@Test
	public void shouldFindAReleaseDeepInHierarchyByDescription() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseMock.getRelease();
		final Release releaseIt2 = rootRelease.getChild(0).getChild(1);
		final Release foundRelease = rootRelease.findRelease("R1/It2");

		assertNotNull(foundRelease);
		assertEquals(releaseIt2, foundRelease);
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldNotFindTheRootRelease() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		rootRelease.findRelease(rootRelease.getDescription());
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldThrowAnExceptionWhenAReleaseIsNotFound() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		rootRelease.findRelease("ReleaseNotFound");
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldThrowAnExceptionWhenAReleaseIsNotFound2() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		rootRelease.findRelease("R1/NotFound");
	}

	@Test
	public void shouldReturnNullWhenReleaseNotFoundByID() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		assertNull(rootRelease.findRelease(new UUID()));
	}

	@Test
	public void shouldFindRootReleaseWhenSearchingByID() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		assertNotNull(rootRelease.findRelease(rootRelease.getId()));
	}

	@Test
	public void shouldReturnAReleaseById() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		final Release foundRelease = rootRelease.findRelease(rootRelease.getChild(0).getId());
		assertEquals(rootRelease.getChild(0), foundRelease);
	}

	@Test
	public void shouldFindAReleaseDeepInHierarchyById() throws Exception {
		final Release rootRelease = ReleaseMock.getRelease();
		final Release foundRelease = rootRelease.findRelease(rootRelease.getChild(0).getChild(1).getId());
		assertEquals(rootRelease.getChild(0).getChild(1), foundRelease);
	}

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod1() {
		final Release release = ReleaseMock.getRelease();
		final List<Release> childrenList1 = release.getChildren();
		childrenList1.clear();

		final List<Release> childrenList2 = release.getChildren();

		assertTrue(!childrenList2.equals(childrenList1));
	}

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod2() {
		final Release release = ReleaseMock.getRelease();
		final List<Release> childrenList1 = release.getChildren();
		final int size = childrenList1.size();
		childrenList1.clear();

		assertEquals(0, childrenList1.size());
		assertEquals(size, release.getChildren().size());
	}

}
