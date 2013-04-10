package br.com.oncast.ontrack.client.ui.components.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class ImpedimentDatabase {

	private static final String ID_SYMBOL = "#";
	private static final DetailService DETAIL_SERVICE = ClientServiceProvider.get().details();
	private static final ReportMessages MESSAGES = GWT.create(ReportMessages.class);

	public static class ImpedimentItem implements Comparable<ImpedimentItem> {

		public static final ProvidesKey<ImpedimentItem> KEY_PROVIDER = new ProvidesKey<ImpedimentItem>() {
			@Override
			public Object getKey(final ImpedimentItem item) {
				return item == null ? null : item.getPriority();
			}
		};

		private final int priority;

		private final Annotation annotation;

		private final String relatedTo;

		public ImpedimentItem(final String relatedTo, final Annotation annotation, final int priority) {
			this.relatedTo = relatedTo;
			this.annotation = annotation;
			this.priority = priority;
		}

		@Override
		public int compareTo(final ImpedimentItem o) {
			if (o.getPriority() == this.getPriority()) return 0;
			return o.getPriority() < this.getPriority() ? -1 : 1;
		}

		protected int getPriority() {
			return priority;
		}

		public String getRelatedTo() {
			return relatedTo;
		}

		public String getState() {
			return annotation.isImpeded() ? MESSAGES.openImpediment() : MESSAGES.solvedImpediment();
		}

		public String getDescription() {
			return annotation.getMessage();
		}

		public Date getEndDate() {
			return annotation.getLastOcuurenceOf(AnnotationType.SOLVED_IMPEDIMENT);
		}

		public Long getCycletime() {
			return annotation.getDurationOf(AnnotationType.OPEN_IMPEDIMENT);
		}

		public Date getStartDate() {
			return annotation.getLastOcuurenceOf(AnnotationType.OPEN_IMPEDIMENT);
		}
	}

	private final ListDataProvider<ImpedimentItem> dataProvider = new ListDataProvider<ImpedimentItem>();

	public ImpedimentDatabase(final Release release, final ProjectContext context) {
		dataProvider.getList().addAll(getImpedimentsList(release, context));
	}

	private List<ImpedimentItem> getImpedimentsList(final Release release, final ProjectContext context) {
		final List<ImpedimentItem> list = new ArrayList<ImpedimentItem>();
		int priority = 0;

		priority = addReleaseImpediments(release, list, priority);

		for (final Scope scope : release.getAllScopesIncludingDescendantReleases()) {
			final String humanId = ID_SYMBOL + context.getHumanId(scope);
			priority = addScopeImpediments(humanId, list, priority, scope);

			for (final Scope descendantScope : scope.getAllDescendantScopes()) {
				priority = addScopeImpediments(humanId, list, priority, descendantScope);
			}
		}
		return list;
	}

	private int addReleaseImpediments(final Release release, final List<ImpedimentItem> list, int priority) {
		for (final Annotation annotation : DETAIL_SERVICE.getImpedimentsFor(release.getId())) {
			list.add(new ImpedimentItem(MESSAGES.release(), annotation, priority++));
		}
		return priority;
	}

	private int addScopeImpediments(final String humanId, final List<ImpedimentItem> list, int priority, final Scope scope) {
		for (final Annotation annotation : DETAIL_SERVICE.getImpedimentsFor(scope.getId()))
			list.add(new ImpedimentItem(humanId, annotation, priority++));
		return priority;
	}

	public ListDataProvider<ImpedimentItem> getDataProvider() {
		return dataProvider;
	}

	public void addDataDisplay(final CellTable<ImpedimentItem> display) {
		dataProvider.addDataDisplay(display);
	}

	public void refreshDisplays() {
		dataProvider.refresh();
	}

	public boolean isEmpty() {
		return dataProvider.getList().isEmpty();
	}
}
