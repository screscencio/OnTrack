package br.com.oncast.ontrack.client.ui.components.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class ImpedimentDatabase {

	private static final DetailService ANNOTATION_SERVICE = ClientServiceProvider.getInstance().getAnnotationService();
	private static final ReportMessages MESSAGES = GWT.create(ReportMessages.class);;

	public static class ImpedimentItem implements Comparable<ImpedimentItem> {

		public static final ProvidesKey<ImpedimentItem> KEY_PROVIDER = new ProvidesKey<ImpedimentItem>() {
			@Override
			public Object getKey(final ImpedimentItem item) {
				return item == null ? null : item.getPriority();
			}
		};

		private final int priority;

		private final Scope scope;

		private final ProjectContext context;

		private final Annotation annotation;

		public ImpedimentItem(final ProjectContext context, final Scope scope, final Annotation annotation, final int priority) {
			this.context = context;
			this.scope = scope;
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

		public String getHumandReadableId() {
			return context.getHumanId(scope);
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

	public ImpedimentDatabase(final List<Scope> scopeList, final ProjectContext context) {
		final List<ImpedimentItem> list = getImpedimentList(scopeList, context);
		dataProvider.getList().addAll(list);
	}

	private List<ImpedimentItem> getImpedimentList(final List<Scope> scopeList, final ProjectContext context) {
		final List<ImpedimentItem> list = new ArrayList<ImpedimentItem>();
		int priority = 0;
		for (final Scope scope : scopeList) {
			for (final Annotation annotation : ANNOTATION_SERVICE.getAnnotationsFor(scope.getId()))
				if (annotation.isImpediment()) list.add(new ImpedimentItem(context, scope, annotation, priority++));
			for (final Scope descendantScope : scope.getAllDescendantScopes()) {
				for (final Annotation annotation : ANNOTATION_SERVICE.getAnnotationsFor(descendantScope.getId()))
					if (annotation.isImpediment()) list.add(new ImpedimentItem(context, scope, annotation, priority++));
			}
		}
		return list;
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
