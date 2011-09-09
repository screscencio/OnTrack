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

	@Test
	public void shouldReturnAllDescendants() throws Exception {
		final Release release = ReleaseMock.getBigRelease();

		assertEquals(13, release.getDescendantReleases().size());
	}

	@Test
	public void shouldReturnAllDescendantsInOrder() throws Exception {
		final Release rootRelease = ReleaseMock.getBigRelease();
		final List<Release> descendantReleases = rootRelease.getDescendantReleases();

		assertEquals(rootRelease.getChild(0), descendantReleases.get(0)); // R1
		assertEquals(rootRelease.getChild(0).getChild(0), descendantReleases.get(1)); // R1/It1
		assertEquals(rootRelease.getChild(0).getChild(1), descendantReleases.get(2)); // R1/It2
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(0), descendantReleases.get(3)); // R1/It2/w1
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(1), descendantReleases.get(4)); // R1/It2/w2
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(1).getChild(0), descendantReleases.get(5)); // R1/It2/w2/d1
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(1).getChild(1), descendantReleases.get(6)); // R1/It2/w2/d2
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(1).getChild(2), descendantReleases.get(7)); // R1/It2/w2/d3
		assertEquals(rootRelease.getChild(0).getChild(1).getChild(2), descendantReleases.get(8)); // R1/It2/w3
		assertEquals(rootRelease.getChild(0).getChild(2), descendantReleases.get(9)); // R1/It3
		assertEquals(rootRelease.getChild(1), descendantReleases.get(10)); // R2
		assertEquals(rootRelease.getChild(1).getChild(0), descendantReleases.get(11)); // R2/It4
		assertEquals(rootRelease.getChild(2), descendantReleases.get(12)); // R3
	}
}
