package br.com.oncast.ontrack.client.ui.components.report;

import java.util.List;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.value.Value;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class ImpedimentDatabase {

	public static class ScopeItem implements Comparable<ScopeItem> {

		public static final ProvidesKey<ScopeItem> KEY_PROVIDER = new ProvidesKey<ScopeItem>() {
			@Override
			public Object getKey(final ScopeItem item) {
				return item == null ? null : item.getPriority();
			}
		};

		private final int priority;

		private final Scope scope;

		private final ProjectContext context;

		public ScopeItem(final ProjectContext context, final Scope scope, final int priority) {
			this.context = context;
			this.scope = scope;
			this.priority = priority;
		}

		@Override
		public int compareTo(final ScopeItem o) {
			if (o.getPriority() == this.getPriority()) return 0;
			return o.getPriority() < this.getPriority() ? -1 : 1;
		}

		protected int getPriority() {
			return priority;
		}

		public String getDescription() {
			return scope.getDescription();
		}

		public String getEffort() {
			final Effort effort = scope.getEffort();

			final float declaredEffort = effort.getDeclared();
			final float inferedEffort = effort.getInfered();

			final float resultantEffort = Math.max(inferedEffort, declaredEffort);
			return ClientDecimalFormat.roundFloat(resultantEffort, 1) + "ep";
		}

		public String getValue() {
			final Value value = scope.getValue();

			final float declaredValue = value.getDeclared();
			final float inferedValue = value.getInfered();

			final float resultantEffort = Math.max(inferedValue, declaredValue);
			return ClientDecimalFormat.roundFloat(resultantEffort, 1) + "vp";
		}

		public String getProgress() {
			return scope.getEffort().getAccomplishedPercentual() + "%";
		}

		public String getCycleTime() {
			final Long cycletime = scope.getProgress().getCycletime();
			return cycletime == null ? "---" : ClientDecimalFormat.roundFloat(cycletime / 86400000, 1);
		}

		public String getLeadTime() {
			final Long leadtime = scope.getProgress().getLeadtime();
			return leadtime == null ? "---" : ClientDecimalFormat.roundFloat(leadtime / 86400000, 1);
		}

		public String getHumandReadableId() {
			return context.getHumanId(scope);
		}
	}

	private final ListDataProvider<ScopeItem> dataProvider = new ListDataProvider<ScopeItem>();

	public ImpedimentDatabase(final List<Scope> scopeList, final ProjectContext context) {
		final List<ScopeItem> list = dataProvider.getList();
		int i = 0;
		for (final Scope scope : scopeList) {
			list.add(new ScopeItem(context, scope, i++));
		}
	}

	public ListDataProvider<ScopeItem> getDataProvider() {
		return dataProvider;
	}

	public void addDataDisplay(final CellTable<ScopeItem> display) {
		dataProvider.addDataDisplay(display);
	}

	public void refreshDisplays() {
		dataProvider.refresh();
	}
}
