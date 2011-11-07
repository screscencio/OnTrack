package br.com.oncast.ontrack.shared.model.release;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;

public class ReleaseTest {

	@Test
	public void shouldFindAReleaseByDescription() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		final Release releaseR1 = rootRelease.getChild(0);
		final Release foundRelease = rootRelease.findRelease(releaseR1.getDescription());

		assertNotNull(foundRelease);
		assertEquals(releaseR1, foundRelease);
	}

	@Test
	public void shouldFindReleaseThatIsInTheFirstSubLevel() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
		final Release releaseIt2 = rootRelease.getChild(0);
		final Release foundRelease = rootRelease.findRelease("R1");

		assertNotNull(foundRelease);
		assertEquals(releaseIt2, foundRelease);
	}

	@Test
	public void shouldFindReleaseThatIsInTheSecondSubLevel() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
		final Release releaseIt2 = rootRelease.getChild(0).getChild(1);
		final Release foundRelease = rootRelease.findRelease("R1/It2");

		assertNotNull(foundRelease);
		assertEquals(releaseIt2, foundRelease);
	}

	@Test
	public void shouldFindReleaseThatIsInTheThirdSubLevel() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
		final Release releaseIt2 = rootRelease.getChild(0).getChild(1).getChild(0);
		final Release foundRelease = rootRelease.findRelease("R1/It2/w1");

		assertNotNull(foundRelease);
		assertEquals(releaseIt2, foundRelease);
	}

	@Test
	public void shouldIgnoreMalformedSeparatorsInReleaseDescriptionWhenFindingRelease() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
		final Release releaseIt2 = rootRelease.getChild(0).getChild(1);
		final String[] malformedReleaseDescriptions = { "R1//It2", "/R1/It2", "R1/It2/", "/R1//It2", "R1//It2/", "/R1//It2/", "//R1//It2//",
				"   / / / R1 / / / It2 / / / " };

		for (final String releaseDescription : malformedReleaseDescriptions) {
			final Release foundRelease = rootRelease.findRelease(releaseDescription);

			assertNotNull(foundRelease);
			assertEquals(releaseIt2, foundRelease);
		}
	}

	@Test
	public void findReleaseShouldThrow() throws ReleaseNotFoundException {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
		final Release releaseIt2 = rootRelease.getChild(0).getChild(1);
		final String[] malformedReleaseDescriptions = { "R1//It2", "/R1/It2", "R1/It2/", "/R1//It2", "R1//It2/", "/R1//It2/", "//R1//It2//",
				"   / / / R1 / / / It2 / / / " };

		for (final String releaseDescription : malformedReleaseDescriptions) {
			final Release foundRelease = rootRelease.findRelease(releaseDescription);

			assertNotNull(foundRelease);
			assertEquals(releaseIt2, foundRelease);
		}
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldNotFindTheRootRelease() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		rootRelease.findRelease(rootRelease.getDescription());
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldThrowAnExceptionWhenAReleaseIsNotFound() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		rootRelease.findRelease("ReleaseNotFound");
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldThrowAnExceptionWhenAReleaseIsNotFound2() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		rootRelease.findRelease("R1/NotFound");
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldNotFindReleaseWithEmptyDescription() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		rootRelease.findRelease("");
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void shouldNotFindReleaseWithInvalidDescription() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		rootRelease.findRelease(" / ");
	}

	@Test
	public void shouldReturnNullWhenReleaseNotFoundByID() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		assertNull(rootRelease.findRelease(new UUID()));
	}

	@Test
	public void shouldFindRootReleaseWhenSearchingByID() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		assertNotNull(rootRelease.findRelease(rootRelease.getId()));
	}

	@Test
	public void shouldReturnAReleaseById() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		final Release foundRelease = rootRelease.findRelease(rootRelease.getChild(0).getId());
		assertEquals(rootRelease.getChild(0), foundRelease);
	}

	@Test
	public void shouldFindAReleaseDeepInHierarchyById() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getRelease();
		final Release foundRelease = rootRelease.findRelease(rootRelease.getChild(0).getChild(1).getId());
		assertEquals(rootRelease.getChild(0).getChild(1), foundRelease);
	}

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod1() {
		final Release release = ReleaseTestUtils.getRelease();
		final List<Release> childrenList1 = release.getChildren();
		childrenList1.clear();

		final List<Release> childrenList2 = release.getChildren();

		assertTrue(!childrenList2.equals(childrenList1));
	}

	@Test
	public void shouldNotChangeChildListWhenChangingListReturnedFromGetChildrenMethod2() {
		final Release release = ReleaseTestUtils.getRelease();
		final List<Release> childrenList1 = release.getChildren();
		final int size = childrenList1.size();
		childrenList1.clear();

		assertEquals(0, childrenList1.size());
		assertEquals(size, release.getChildren().size());
	}

	@Test
	public void shouldReturnAllDescendants() throws Exception {
		final Release release = ReleaseTestUtils.getBigRelease();

		assertEquals(13, release.getDescendantReleases().size());
	}

	@Test
	public void shouldReturnAllDescendantsInOrder() throws Exception {
		final Release rootRelease = ReleaseTestUtils.getBigRelease();
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

	@Test
	public void shouldReturnAllScopes() throws Exception {
		final Release release = ReleaseTestUtils.getEmptyRelease();
		final int nScopes = 3;
		for (int i = 0; i < nScopes; i++) {
			release.addScope(new Scope("scope"));
		}
		assertEquals(nScopes, release.getAllScopesIncludingChildrenReleases().size());
	}

	@Test
	public void shouldReturnAllScopesRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final int nScopes = 12;
		assertEquals(nScopes, release.getAllScopesIncludingChildrenReleases().size());
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDate() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		for (int i = 0; i < scopeList.size(); i++) {
			setScopeToUnderWorkInDay(scopeList.get(i), WorkingDayFactory.create().add(i));
		}

		assertReleaseStartDateIsEqualsScopeStartDateOnIndex(release, 0);
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDate2() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		final int lastElementIndex = getLastIndex(scopeList);
		for (int i = lastElementIndex; i >= 0; i--) {
			setScopeToUnderWorkInDay(scopeList.get(i), WorkingDayFactory.create().add(lastElementIndex - i));
		}
		assertReleaseStartDateIsEqualsScopeStartDateOnIndex(release, 2);
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDateRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();

		final Scope earliestScope = release.getChild(1).getScopeList().get(2);
		setScopeToUnderWorkInDay(earliestScope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 3));
		setScopeToUnderWorkInDay(release.getChild(0).getScopeList().get(0), WorkingDayFactory.create(2011, Calendar.OCTOBER, 7));
		setScopeToUnderWorkInDay(release.getChild(2).getScopeList().get(1), WorkingDayFactory.create(2011, Calendar.OCTOBER, 6));

		assertEquals(earliestScope.getProgress().getStartDay(), release.getStartDay());
	}

	@Test
	public void startDateShouldBeNullIfThereIsNoStartedScope() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		for (final Scope scope : scopeList) {
			assertNull(scope.getProgress().getStartDay());
		}
		assertNull(release.getStartDay());
	}

	@Test
	public void endDateShouldBeTheLatestEndDate() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		for (int i = 0; i < scopeList.size(); i++) {
			setScopeToDoneInDay(scopeList.get(i), WorkingDayFactory.create().add(i));
		}
		assertReleaseEndDateIsEqualsScopeStartDateOnIndex(release, getLastIndex(scopeList));
	}

	@Test
	public void endDateShouldBeTheScopesLatestEndDate2() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		final int lastIndex = getLastIndex(scopeList);
		for (int i = lastIndex; i >= 0; i--) {
			setScopeToDoneInDay(scopeList.get(i), WorkingDayFactory.create().add(lastIndex - i));
		}
		assertReleaseEndDateIsEqualsScopeStartDateOnIndex(release, 0);
	}

	@Test
	public void endDateShouldBeTheScopesLatestEndDateRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();

		setScopeToDoneInDay(release.getChild(1).getScopeList().get(2), WorkingDayFactory.create(2011, Calendar.OCTOBER, 3));
		setScopeToDoneInDay(release.getChild(0).getScopeList().get(0), WorkingDayFactory.create(2011, Calendar.OCTOBER, 7));

		final List<Scope> scopeList = release.getScopeList();
		for (final Scope scope : scopeList) {
			setScopeToDoneInDay(scope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 10));
		}

		final Scope latestScope = release.getChild(2).getScopeList().get(1);
		setScopeToDoneInDay(latestScope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 12));

		assertEquals(latestScope.getProgress().getEndDay(), release.getEndDay());
	}

	@Test
	public void endDateShouldBeNullIfThereIsNoDoneScopes() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		for (final Scope scope : scopeList) {
			assertNull(scope.getProgress().getEndDay());
		}
		assertNull(release.getEndDay());
	}

	@Test
	public void effortSumShouldBeTheSumOfScopesEffort() throws Exception {
		final Scope rootScope = ScopeTestUtils.getScopeWithEffort();
		final Release release = ReleaseTestUtils.getRelease().getChild(0);
		for (final Scope childScope : rootScope.getChildren()) {
			release.addScope(childScope);
		}

		assertEquals(50, release.getEffortSum(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void effortSumShouldBeTheSumOfScopesEffortRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();

		release.getChild(0).getScopeList().get(0).getEffort().setDeclared(5);
		release.getChild(1).getScopeList().get(1).getEffort().setDeclared(15);
		release.getChild(2).getScopeList().get(2).getEffort().setDeclared(25);
		release.getScopeList().get(0).getEffort().setDeclared(25);

		assertEquals(70, release.getEffortSum(), 0.01);
	}

	private void setScopeToUnderWorkInDay(final Scope scope, final WorkingDay day) throws Exception {
		ScopeTestUtils.setProgress(scope, ProgressState.UNDER_WORK);
		setWorkingDayToField(scope, "startDate", day);
	}

	private void setScopeToDoneInDay(final Scope scope, final WorkingDay day) throws Exception {
		ScopeTestUtils.setProgress(scope, ProgressState.DONE);
		setWorkingDayToField(scope, "endDate", day);
	}

	private void assertReleaseStartDateIsEqualsScopeStartDateOnIndex(final Release release, final int index) {
		final List<Scope> scopeList = release.getScopeList();
		assertEquals(scopeList.get(index).getProgress().getStartDay(), release.getStartDay());
		for (int i = 0; i < scopeList.size(); i++) {
			if (i == index) continue;
			assertNotEquals(scopeList.get(i).getProgress().getStartDay(), release.getStartDay());
		}
	}

	private void assertReleaseEndDateIsEqualsScopeStartDateOnIndex(final Release release, final int index) {
		final List<Scope> scopeList = release.getScopeList();
		assertEquals(scopeList.get(index).getProgress().getEndDay(), release.getEndDay());
		for (int i = 0; i < scopeList.size(); i++) {
			if (i == index) continue;
			assertNotEquals(scopeList.get(i).getProgress().getEndDay(), release.getEndDay());
		}

	}

	private void assertNotEquals(final WorkingDay day1, final WorkingDay day2) {
		assertFalse(day1.equals(day2));
	}

	private int getLastIndex(final List<Scope> scopeList) {
		return scopeList.size() - 1;
	}

	private void setWorkingDayToField(final Scope scope, final String fieldName, final WorkingDay date) throws Exception {
		final Field startDateField = Progress.class.getDeclaredField(fieldName);
		startDateField.setAccessible(true);
		startDateField.set(scope.getProgress(), date.copy());
	}

}
