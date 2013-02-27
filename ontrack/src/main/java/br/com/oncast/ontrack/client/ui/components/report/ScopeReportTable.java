package br.com.oncast.ontrack.client.ui.components.report;

import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.report.ScopeDatabase.ScopeItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class ScopeReportTable extends Composite {

	private static ScopeReportTableUiBinder uiBinder = GWT.create(ScopeReportTableUiBinder.class);

	interface ScopeReportTableUiBinder extends UiBinder<Widget, ScopeReportTable> {}

	@UiField(provided = true)
	CellTable<ScopeItem> cellTable;

	private final ScopeDatabase scopeDatabase;

	public ScopeReportTable(final List<Scope> scopeList, final ProjectContext context) {
		cellTable = new CellTable<ScopeItem>(ScopeDatabase.ScopeItem.KEY_PROVIDER);
		scopeDatabase = new ScopeDatabase(scopeList, context);
		final ListHandler<ScopeItem> sortHandler = new ListHandler<ScopeItem>(scopeDatabase.getDataProvider().getList());
		final SelectionModel<ScopeItem> selectionModel = new NoSelectionModel<ScopeItem>(ScopeDatabase.ScopeItem.KEY_PROVIDER);
		cellTable.addColumnSortHandler(sortHandler);
		cellTable.setSelectionModel(selectionModel);
		cellTable.setWidth("100%", true);
		initTableColumns(selectionModel, sortHandler);
		scopeDatabase.addDataDisplay(cellTable);

		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTableColumns(final SelectionModel<ScopeItem> selectionModel, final ListHandler<ScopeItem> sortHandler) {

		final Column<ScopeItem, String> idColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getHumandReadableId();
			}
		};
		idColumn.setSortable(true);
		sortHandler.setComparator(idColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getHumandReadableId().compareTo(o2.getHumandReadableId());
			}
		});
		// FIXME LOBO I18N
		cellTable.setColumnWidth(idColumn, 55, Unit.PX);
		cellTable.addColumn(idColumn, new TextHeader("ID"));

		final Column<ScopeItem, String> descriptionColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getDescription();
			}
		};
		descriptionColumn.setSortable(true);
		sortHandler.setComparator(descriptionColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(descriptionColumn, new TextHeader("Description"));

		final Column<ScopeItem, String> effortColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getEffort();
			}
		};
		effortColumn.setSortable(true);
		sortHandler.setComparator(effortColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getEffort().compareTo(o2.getEffort());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(effortColumn, new TextHeader("Effort"));
		cellTable.setColumnWidth(effortColumn, 80, Unit.PX);

		final Column<ScopeItem, String> valueColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getValue();
			}
		};
		valueColumn.setSortable(true);
		sortHandler.setComparator(valueColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(valueColumn, new TextHeader("Value"));
		cellTable.setColumnWidth(valueColumn, 80, Unit.PX);

		final Column<ScopeItem, String> progressColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getProgress();
			}
		};
		progressColumn.setSortable(true);
		sortHandler.setComparator(progressColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getProgress().compareTo(o2.getProgress());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(progressColumn, new TextHeader("Progress"));
		cellTable.setColumnWidth(progressColumn, 85, Unit.PX);

		final Column<ScopeItem, String> cycleTimeColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getCycleTime();
			}
		};
		cycleTimeColumn.setSortable(true);
		sortHandler.setComparator(cycleTimeColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getCycleTime().compareTo(o2.getCycleTime());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(cycleTimeColumn, new TextHeader("Cycle Time"));
		cellTable.setColumnWidth(cycleTimeColumn, 80, Unit.PX);

		final Column<ScopeItem, String> leadTimeColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getLeadTime();
			}
		};
		leadTimeColumn.setSortable(true);
		sortHandler.setComparator(leadTimeColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getLeadTime().compareTo(o2.getLeadTime());
			}
		});
		// FIXME LOBO I18N
		cellTable.addColumn(leadTimeColumn, new TextHeader("Lead Time"));
		cellTable.setColumnWidth(leadTimeColumn, 80, Unit.PX);

	}
}
