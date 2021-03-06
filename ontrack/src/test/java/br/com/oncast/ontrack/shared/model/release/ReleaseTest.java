package br.com.oncast.ontrack.shared.model.release;

import br.com.oncast.ontrack.shared.model.release.Release.Condition;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

public class ReleaseTest {

	private Condition condition;

	@Before
	public void setUpCondition() {
		condition = new Condition() {
			@Override
			public boolean eval(final Release release) {
				return release.getDescription().contains("*");
			}
		};
	}

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
			release.addScope(ScopeTestUtils.createScope("scope"));
		}
		assertEquals(nScopes, release.getAllScopesIncludingDescendantReleases().size());
	}

	@Test
	public void shouldReturnAllScopesRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final int nScopes = 12;
		assertEquals(nScopes, release.getAllScopesIncludingDescendantReleases().size());
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDate() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		for (int i = 0; i < scopeList.size(); i++) {
			setStartDayOnScope(scopeList.get(i), WorkingDayFactory.create().add(i));
		}

		assertReleaseStartDateIsEqualsScopeStartDateOnIndex(release, 0);
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDate2() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();
		final List<Scope> scopeList = release.getScopeList();

		final int lastElementIndex = getLastIndex(scopeList);
		for (int i = lastElementIndex; i >= 0; i--) {
			setStartDayOnScope(scopeList.get(i), WorkingDayFactory.create().add(lastElementIndex - i));
		}
		assertReleaseStartDateIsEqualsScopeStartDateOnIndex(release, 2);
	}

	@Test
	public void startDateShouldBeTheScopesEarliestStartDateRegardingChildReleases() throws Exception {
		final Release release = ReleaseTestUtils.getReleaseWithScopes();

		final Scope earliestScope = release.getChild(1).getScopeList().get(2);
		setStartDayOnScope(earliestScope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 3));
		setStartDayOnScope(release.getChild(0).getScopeList().get(0), WorkingDayFactory.create(2011, Calendar.OCTOBER, 7));
		setStartDayOnScope(release.getChild(2).getScopeList().get(1), WorkingDayFactory.create(2011, Calendar.OCTOBER, 6));

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
		final Release release = spy(ReleaseTestUtils.getReleaseWithScopes());
		when(release.isDone()).thenReturn(true);

		final List<Scope> scopeList = release.getScopeList();

		for (int i = 0; i < scopeList.size(); i++) {
			setEndDayOnScope(scopeList.get(i), WorkingDayFactory.create().add(i));
		}
		assertReleaseEndDateIsEqualsScopeStartDateOnIndex(release, getLastIndex(scopeList));
	}

	@Test
	public void endDateShouldBeTheScopesLatestEndDate2() throws Exception {
		final Release release = spy(ReleaseTestUtils.getReleaseWithScopes());
		when(release.isDone()).thenReturn(true);

		final List<Scope> scopeList = release.getScopeList();

		final int lastIndex = getLastIndex(scopeList);
		for (int i = lastIndex; i >= 0; i--) {
			setEndDayOnScope(scopeList.get(i), WorkingDayFactory.create().add(lastIndex - i));
		}
		assertReleaseEndDateIsEqualsScopeStartDateOnIndex(release, 0);
	}

	@Test
	public void endDateShouldBeTheScopesLatestEndDateRegardingChildReleases() throws Exception {
		final Release release = spy(ReleaseTestUtils.getReleaseWithScopes());
		when(release.isDone()).thenReturn(true);

		setEndDayOnScope(release.getChild(1).getScopeList().get(2), WorkingDayFactory.create(2011, Calendar.OCTOBER, 3));
		setEndDayOnScope(release.getChild(0).getScopeList().get(0), WorkingDayFactory.create(2011, Calendar.OCTOBER, 7));

		final List<Scope> scopeList = release.getScopeList();
		for (final Scope scope : scopeList) {
			setEndDayOnScope(scope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 10));
		}

		final Scope latestScope = release.getChild(2).getScopeList().get(1);
		setEndDayOnScope(latestScope, WorkingDayFactory.create(2011, Calendar.OCTOBER, 12));

		assertEquals(latestScope.getProgress().getEndDay(), release.getEndDay());
	}

	@Test
	public void endDateShouldBeNullIfTheReleaseIsNotDone() throws Exception {
		final Release release = spy(ReleaseTestUtils.getReleaseWithScopes());
		when(release.isDone()).thenReturn(false);
		assertNull(release.getEndDay());
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

	@Test
	public void firstPastSimblingIsTheLatestPastReleaseWhenItDoesntHaveChildren() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son *");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son *");

		parent.addChild(firstSon);
		parent.addChild(secondSon);

		assertEquals(firstSon, secondSon.getLatestPastRelease(condition));
	}

	@Test
	public void futureSimblingReleasesDoesNotCount() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son *");
		final Release thirdSon = ReleaseTestUtils.createRelease("Third Son *");

		parent.addChild(firstSon);
		parent.addChild(secondSon);
		parent.addChild(thirdSon);

		assertNull(secondSon.getLatestPastRelease(condition));
	}

	@Test
	public void parentReleaseIsConsideredFutureRelease() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son *");

		parent.addChild(firstSon);
		parent.addChild(secondSon);

		assertNull(secondSon.getLatestPastRelease(condition));
	}

	@Test
	public void testingWhenTheLatestPastReleaseIsTheSecondNephew() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son");
		final Release firstNephew = ReleaseTestUtils.createRelease("First Nephew *");
		final Release secondNephew = ReleaseTestUtils.createRelease("Second Nephew *");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son *");

		parent.addChild(firstSon);
		parent.addChild(secondSon);
		firstSon.addChild(firstNephew);
		firstSon.addChild(secondNephew);

		assertEquals(secondNephew, secondSon.getLatestPastRelease(condition));
	}

	@Test
	public void returnsNullWhenNoReleaseSatisfiesTheGivenCondition() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son");
		final Release firstNephew = ReleaseTestUtils.createRelease("First Nephew");
		final Release secondNephew = ReleaseTestUtils.createRelease("Second Nephew");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son");

		parent.addChild(firstSon);
		parent.addChild(secondSon);
		firstSon.addChild(firstNephew);
		firstSon.addChild(secondNephew);

		assertNull(secondNephew.getLatestPastRelease(condition));
	}

	@Test
	public void firstFutureReleaseWhenThereIsOnlyOneRelease() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("ROOT *");
		assertNull(release.getFirstFutureRelease(condition));
	}

	@Test
	public void parentReleaseIsTheFirstFutureReleaseWhenThereIsNoSimbling() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son *");
		parent.addChild(firstSon);

		assertEquals(parent, firstSon.getFirstFutureRelease(condition));

		final Release secondSon = ReleaseTestUtils.createRelease("Second Son");

		parent.addChild(secondSon);

		assertEquals(parent, firstSon.getFirstFutureRelease(condition));
	}

	@Test
	public void childReleaseIsConsideredPastReleases() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son *");
		parent.addChild(firstSon);

		assertNull(parent.getFirstFutureRelease(condition));
	}

	@Test
	public void firstFutureReleaseIsTheNextSimblingRelease() throws Exception {
		final Release parent = ReleaseTestUtils.createRelease("Parent *");
		final Release firstSon = ReleaseTestUtils.createRelease("First Son *");
		final Release secondSon = ReleaseTestUtils.createRelease("Second Son *");

		parent.addChild(firstSon);
		parent.addChild(secondSon);

		assertEquals(secondSon, firstSon.getFirstFutureRelease(condition));
	}

	@Test
	public void getStartDayShouldReturnDeclaredStartDayInsteadOfInferedOneWhenDeclared() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		final WorkingDay declaredDay = WorkingDayFactory.create(2000, 1, 27);
		final WorkingDay inferedDay = WorkingDayFactory.create();

		ReleaseTestUtils.setInferedStartDay(release, inferedDay);

		assertFalse(release.hasDeclaredStartDay());
		assertEquals(inferedDay, release.getInferedStartDay());
		assertEquals(inferedDay, release.getStartDay());

		release.declareStartDay(declaredDay);

		assertTrue(release.hasDeclaredStartDay());
		assertEquals(inferedDay, release.getInferedStartDay());
		assertEquals(declaredDay, release.getStartDay());
	}

	@Test
	public void getEndDayShouldReturnDeclaredStartDayInsteadOfInferedOneWhenDeclared() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		final WorkingDay declaredDay = WorkingDayFactory.create(2000, 1, 27);
		final WorkingDay inferedDay = WorkingDayFactory.create();

		ReleaseTestUtils.setInferedEndDay(release, inferedDay);

		assertFalse(release.hasDeclaredEndDay());
		assertEquals(inferedDay, release.getInferedEndDay());
		assertEquals(inferedDay, release.getEndDay());

		release.declareEndDay(declaredDay);

		assertTrue(release.hasDeclaredEndDay());
		assertEquals(inferedDay, release.getInferedEndDay());
		assertEquals(declaredDay, release.getEndDay());
	}

	@Test
	public void getEstimatedVelocityShouldReturnNullIfThereIsNoDeclaredVelocity() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		assertNull(release.getEstimatedSpeed());
	}

	@Test
	public void getEstimatedVelocityShouldReturnTheDeclaredVelocity() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		final Float declaredVelocity = 1.4f;

		release.declareEstimatedVelocity(declaredVelocity);

		assertEquals(declaredVelocity, release.getEstimatedSpeed());
	}

	@Test
	public void shouldNotHaveDeclaredEstimatedVelocityWhenNoOneDeclared() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		assertFalse(release.hasDeclaredEstimatedSpeed());
	}

	@Test
	public void shouldHaveDeclaredEstimatedVelocityWhenAlreadyDeclaredOne() throws Exception {
		final Release release = ReleaseTestUtils.createRelease("Any Release");
		final Float declaredVelocity = 1.4f;

		release.declareEstimatedVelocity(declaredVelocity);

		assertTrue(release.hasDeclaredEstimatedSpeed());
	}

	private void setStartDayOnScope(final Scope scope, final WorkingDay day) throws Exception {
		ScopeTestUtils.setStartDate(scope, day);
	}

	private void setEndDayOnScope(final Scope scope, final WorkingDay day) throws Exception {
		ScopeTestUtils.setEndDate(scope, day);
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

}
