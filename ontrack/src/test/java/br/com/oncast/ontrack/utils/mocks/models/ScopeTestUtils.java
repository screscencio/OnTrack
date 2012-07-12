package br.com.oncast.ontrack.utils.mocks.models;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.ibm.icu.util.Calendar;

public class ScopeTestUtils {

	private static int scopeCounter = 0;

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScope() {
		final Scope root = new Scope("Project");
		root.add(new Scope("1").add(new Scope("1.1").add(new Scope("1.1.1")).add(new Scope("1.1.2"))).add(new Scope("1.2")));
		root.add(new Scope("2"));
		root.add(new Scope("3"));

		return root;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScope2() {
		final Scope projectScope = new Scope("Project");
		final Scope child = new Scope("aaa");
		child.add(new Scope("111"));
		child.add(new Scope("222"));
		child.add(new Scope("333"));
		child.add(new Scope("444"));
		projectScope.add(child);
		projectScope.add(new Scope("bbb"));
		projectScope.add(new Scope("ccc"));
		projectScope.add(new Scope("ddd"));
		projectScope.add(new Scope("eee"));
		projectScope.add(new Scope("fff"));

		return projectScope;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getSimpleScope() {
		final Scope root = new Scope("Project");
		root.add(new Scope("1"));
		root.add(new Scope("2"));
		root.add(new Scope("3"));

		return root;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getComplexScope() {
		final Scope projectScope = new Scope("Project");
		final Scope child = new Scope("aaa");
		child.add(new Scope("111"));
		child.add(new Scope("222"));
		child.add(new Scope("333").add(new Scope("3.1")).add(new Scope("3.2")));
		child.add(new Scope("444"));
		projectScope.add(child);

		projectScope.add(new Scope("bbb").add(new Scope("b1")));
		projectScope.add(new Scope("ccc").add(new Scope("c1")).add(new Scope("c2").add(new Scope("c21")).add(new Scope("c22"))));
		projectScope.add(new Scope("ddd"));

		return projectScope;
	}

	// IMPORTANT Doesn't change this scope without changing the tests that use it.
	public static Scope getScopeWithEffort() {
		final Scope root = new Scope("Project");

		final Scope scope = new Scope("0");
		scope.getEffort().setDeclared(5);
		root.add(scope);

		final Scope scope2 = new Scope("1");
		scope2.getEffort().setDeclared(10);
		root.add(scope2);

		final Scope scope3 = new Scope("2");
		scope3.getEffort().setDeclared(15);
		root.add(scope3);

		final Scope scope4 = new Scope("3");
		scope4.getEffort().setDeclared(20);
		root.add(scope4);

		root.add(new Scope("4"));

		return root;
	}

	public static Scope setProgress(final Scope scope, final ProgressState progress) {
		scope.getProgress().setDescription(progress.getDescription());
		return scope;
	}

	public static Scope setDelcaredEffort(final Scope scope, final float averageVelocity) {
		scope.getEffort().setDeclared(averageVelocity);
		return scope;
	}

	public static void setEndDate(final Scope scope, final WorkingDay day) {
		setDayOnField(scope, day, "endDate");
	}

	public static void setStartDate(final Scope scope, final WorkingDay day) {
		setDayOnField(scope, day, "startDate");
	}

	private static void setDayOnField(final Scope scope, final WorkingDay day, final String fieldName) {
		try {
			final Field field = Progress.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(scope.getProgress(), day);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static Scope createScope(final String name, final ProgressState progress, final Integer effort, final WorkingDay startDay, final WorkingDay endDay) {
		final Scope scope = new Scope(name);
		if (progress != null) setProgress(scope, progress);
		if (effort != null) setDelcaredEffort(scope, effort);
		if (startDay != null) setStartDate(scope, startDay);
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

	private static void populateWithScopes(final Scope projectScope, final Release release, final WorkingDay day, final int nChildReleases,
			final int nDoneScopes,
			final int nUnderWorkScopes,
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

				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.DONE, choose(efforts), startDay,
						endDay);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
			for (int scopeNumber = 1; scopeNumber <= nUnderWorkScopes; scopeNumber++) {
				final WorkingDay startDay = day.copy();
				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.UNDER_WORK, choose(efforts),
						startDay,
						null);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
			for (int scopeNumber = 1; scopeNumber <= nNotStartedScopes; scopeNumber++) {
				final Scope createdScope = createScope(childDescription + "-S" + scopeNumber, ProgressState.NOT_STARTED, choose(efforts), null,
						null);
				child.addScope(createdScope);
				projectScope.add(createdScope);
			}
		}
	}

	private static <T> T choose(final List<T> list) {
		return list.get(new Random().nextInt(list.size()));
	}

	public static Scope createScope(final String description) {
		return new Scope(description);
	}

	public static Scope createScope() {
		return createScope(Scope.class.getSimpleName() + ++scopeCounter);
	}

	public static Scope createScope(final ProgressState progress) {
		final Scope scope = createScope();
		return setProgress(scope, progress);
	}

}
