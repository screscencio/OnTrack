package br.com.oncast.ontrack.shared.model.scope;

import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScopeComparator {

	public static List<Scope> sortByLatestEndDate(final List<Scope> scopes) {
		Collections.sort(scopes, getDescendingEndDateComparator());
		return scopes;
	}

	private static Comparator<Scope> getDescendingEndDateComparator() {
		return new Comparator<Scope>() {
			@Override
			public int compare(final Scope scope1, final Scope scope2) {
				return getEndDate(scope2).compareTo(getEndDate(scope1));
			}
		};
	}

	private static WorkingDay getEndDate(final Scope scope) {
		final WorkingDay endDate = scope.getProgress().getEndDay();
		return (endDate != null) ? endDate : WorkingDayFactory.create(new Date(0));
	}
}
