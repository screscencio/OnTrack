package br.com.oncast.ontrack.utils.model;

import br.com.oncast.ontrack.shared.model.progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.ibm.icu.util.Calendar;

public class ScopeTestUtils {

	private static final Date DEFAULT_TIMESTAMP = new Date(0);
	private static int scopeCounter = 0;

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScope() {
		final Scope root = ScopeTestUtils.createScope("Project");
		root.add(ScopeTestUtils.createScope("1").add(ScopeTestUtils.createScope("1.1").add(ScopeTestUtils.createScope("1.1.1")).add(ScopeTestUtils.createScope("1.1.2")))
				.add(ScopeTestUtils.createScope("1.2")));
		root.add(ScopeTestUtils.createScope("2"));
		root.add(ScopeTestUtils.createScope("3"));

		return root;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScope2() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		final Scope child = ScopeTestUtils.createScope("aaa");
		child.add(ScopeTestUtils.createScope("111"));
		child.add(ScopeTestUtils.createScope("222"));
		child.add(ScopeTestUtils.createScope("333"));
		child.add(ScopeTestUtils.createScope("444"));
		projectScope.add(child);
		projectScope.add(ScopeTestUtils.createScope("bbb"));
		projectScope.add(ScopeTestUtils.createScope("ccc"));
		projectScope.add(ScopeTestUtils.createScope("ddd"));
		projectScope.add(ScopeTestUtils.createScope("eee"));
		projectScope.add(ScopeTestUtils.createScope("fff"));

		return projectScope;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getSimpleScope() {
		final Scope root = ScopeTestUtils.createScope("Project");
		root.add(ScopeTestUtils.createScope("1"));
		root.add(ScopeTestUtils.createScope("2"));
		root.add(ScopeTestUtils.createScope("3"));

		return root;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getComplexScope() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		final Scope child = ScopeTestUtils.createScope("aaa");
		child.add(ScopeTestUtils.createScope("111"));
		child.add(ScopeTestUtils.createScope("222"));
		child.add(ScopeTestUtils.createScope("333").add(ScopeTestUtils.createScope("3.1")).add(ScopeTestUtils.createScope("3.2")));
		child.add(ScopeTestUtils.createScope("444"));
		projectScope.add(child);

		projectScope.add(ScopeTestUtils.createScope("bbb").add(ScopeTestUtils.createScope("b1")));
		projectScope.add(ScopeTestUtils.createScope("ccc").add(ScopeTestUtils.createScope("c1"))
				.add(ScopeTestUtils.createScope("c2").add(ScopeTestUtils.createScope("c21")).add(ScopeTestUtils.createScope("c22"))));
		projectScope.add(ScopeTestUtils.createScope("ddd"));

		return projectScope;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScopeWithEffort() {
		final Scope root = ScopeTestUtils.createScope("Project");

		final Scope scope = ScopeTestUtils.createScope("0");
		scope.getEffort().setDeclared(5);
		root.add(scope);

		final Scope scope2 = ScopeTestUtils.createScope("1");
		scope2.getEffort().setDeclared(10);
		root.add(scope2);

		final Scope scope3 = ScopeTestUtils.createScope("2");
		scope3.getEffort().setDeclared(15);
		root.add(scope3);

		final Scope scope4 = ScopeTestUtils.createScope("3");
		scope4.getEffort().setDeclared(20);
		root.add(scope4);

		root.add(ScopeTestUtils.createScope("4"));

		return root;
	}

	public static Scope setProgress(final Scope scope, final ProgressState progress) {
		return setProgress(scope, progress, DEFAULT_TIMESTAMP);
	}

	public static Scope declareEffort(final Scope scope, final float effort) {
		scope.getEffort().setDeclared(effort);
		return scope;
	}

	public static Scope declareValue(final Scope scope, final float value) {
		scope.getValue().setDeclared(value);
		return scope;
	}

	public static void setEndDate(final Scope scope, final WorkingDay day) {
		scope.getProgress().setDescription(ProgressState.DONE.getDescription(), UserRepresentationTestUtils.getAdmin(), day.getJavaDate());
	}

	public static void setStartDate(final Scope scope, final WorkingDay day) {
		scope.getProgress().setDescription(ProgressState.UNDER_WORK.getDescription(), UserRepresentationTestUtils.getAdmin(), day.getJavaDate());
	}

	public static Scope createScope(final String name, final ProgressState progress, final Integer effort, final WorkingDay startDay, final WorkingDay endDay) {
		final Scope scope = ScopeTestUtils.createScope(name, startDay.getJavaDate());
		if (effort != null) declareEffort(scope, effort);
		if (startDay != null) setStartDate(scope, startDay);
		if (progress != null) setProgress(scope, progress, endDay);
		if (endDay != null) setEndDate(scope, endDay);

		return scope;
	}

	public static void populateWithTestData(final Project result) {
		final Release r1 = new Release("R1", new UUID("R1"));
		final Release r2 = new Release("R2", new UUID("R2"));
		final Release r3 = new Release("R3", new UUID("R3"));

		final WorkingDay startDay = WorkingDayFactory.create(2011, Calendar.SEPTEMBER, 3);

		populateWithScopes(result.getProjectScope(), r1, startDay, 3, 2, 0, 0);
		populateWithScopes(result.getProjectScope(), r2, startDay, 2, 0, 1, 2);
		populateWithScopes(result.getProjectScope(), r3, startDay, 1, 0, 0, 3);

		result.getProjectRelease().addChild(r1);
		result.getProjectRelease().addChild(r2);
		result.getProjectRelease().addChild(r3);
	}

	private static void populateWithScopes(final Scope projectScope, final Release release, final WorkingDay day, final int nChildReleases, final int nDoneScopes, final int nUnderWorkScopes,
			final int nNotStartedScopes) {

		final List<Integer> efforts = Arrays.asList(1, 1, 1, 1, 2, 2, 2, 3, 3, 5, 8);
		final List<Integer> daysSpents = Arrays.asList(0, 1, 2, 3);
		int daysSpent;

		for (int childReleaseNumber = 1; childReleaseNumber <= nChildReleases; childReleaseNumber++) {
			final String childDescription = release.getDescription() + "." + childReleaseNumber;
			final Release child = new Release(childDescription, new UUID(childDescription));
			release.addChild(child);
			for (int scopeNumber = 1; scopeNumber <= nDoneScopes; scopeNumber++) {
				daysSpent = choose(daysSpents);

				final WorkingDay startDay = day.copy();
				final WorkingDay endDay = day.add(daysSpent).copy();

				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.DONE, choose(efforts), startDay, endDay);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
			for (int scopeNumber = 1; scopeNumber <= nUnderWorkScopes; scopeNumber++) {
				final WorkingDay startDay = day.copy();
				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.UNDER_WORK, choose(efforts), startDay, null);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
			for (int scopeNumber = 1; scopeNumber <= nNotStartedScopes; scopeNumber++) {
				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.NOT_STARTED, choose(efforts), null, null);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
		}
	}

	private static <T> T choose(final List<T> list) {
		return list.get(new Random().nextInt(list.size()));
	}

	public static Scope createScope(final String description) {
		return createScope(description, DEFAULT_TIMESTAMP);
	}

	public static Scope createScope(final String description, final Date timestamp) {
		return createScope(description, new UUID(), timestamp);
	}

	public static Scope createScope() {
		return createScope(getDefaultCounterDescription());
	}

	private static String getDefaultCounterDescription() {
		return Scope.class.getSimpleName() + ++scopeCounter;
	}

	public static Scope createScope(final ProgressState progress) {
		final Scope scope = createScope();
		return setProgress(scope, progress);
	}

	public static Scope createScope(final String description, final UUID id) {
		return createScope(description, id, DEFAULT_TIMESTAMP);
	}

	public static Scope createScope(final String description, final UUID id, final Date date) {
		return new Scope(description, id, UserRepresentationTestUtils.getAdmin(), DEFAULT_TIMESTAMP);
	}

	public static Scope setProgress(final Scope scope, final String progressDescription) {
		scope.getProgress().setDescription(progressDescription, UserRepresentationTestUtils.getAdmin(), DEFAULT_TIMESTAMP);
		return scope;
	}

	public static Scope createScope(final WorkingDay day) {
		return createScope(getDefaultCounterDescription(), new UUID(), day.getJavaDate());
	}

	public static Scope setProgress(final Scope scope, final ProgressState progress, final WorkingDay day) {
		return setProgress(scope, progress, day.getJavaDate());
	}

	public static Scope setProgress(final Scope scope, final ProgressState progress, final Date date) {
		scope.getProgress().setDescription(progress.getDescription(), UserRepresentationTestUtils.getAdmin(), date);
		return scope;
	}

	public static Scope declareProgress(final Scope scope, final ProgressState state) {
		return setProgress(scope, state.getDescription());
	}

}
