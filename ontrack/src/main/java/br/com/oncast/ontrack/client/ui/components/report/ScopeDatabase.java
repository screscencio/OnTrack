package br.com.oncast.ontrack.client.ui.components.report;

import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public class ScopeDatabase {

	public static class ScopeItem implements Comparable<ScopeItem> {

		public static final ProvidesKey<ScopeItem> KEY_PROVIDER = new ProvidesKey<ScopeItem>() {
			@Override
			public Object getKey(final ScopeItem item) {
				return item == null ? null : item.getPriority();
			}
		};

		private final int priority;

		private final Scope scope;

		public ScopeItem(final Scope scope, final int priority) {
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
	}

	private final ListDataProvider<ScopeItem> dataProvider = new ListDataProvider<ScopeItem>();

	public ScopeDatabase(final List<Scope> scopeList) {
		final List<ScopeItem> list = dataProvider.getList();
		int i = 0;
		for (final Scope scope : scopeList) {
			list.add(new ScopeItem(scope, i++));
		}
	}

	public ListDataProvider<ScopeItem> getDataProvider() {
		return dataProvider;
	}

	public void addDataDisplay(final CellTable<ScopeItem> display) {
		dataProvider.addDataDisplay(display);
	}
}
