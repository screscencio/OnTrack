package br.com.oncast.ontrack.client.ui.components.report;

import br.com.oncast.ontrack.client.ui.components.report.ScopeDatabase.ScopeItem;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import java.util.Comparator;
import java.util.List;

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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class ScopeReportTable extends Composite {

	private static ScopeReportTableUiBinder uiBinder = GWT.create(ScopeReportTableUiBinder.class);

	interface ScopeReportTableUiBinder extends UiBinder<Widget, ScopeReportTable> {}

	@UiField(provided = true)
	CellTable<ScopeItem> cellTable;

	private final ScopeDatabase scopeDatabase;

	private final ReportMessages messages;

	private Column<ScopeItem, String> idColumn;

	public ScopeReportTable(final List<Scope> scopeList, final ProjectContext context, final ReportMessages messages) {
		this.messages = messages;
		cellTable = new CellTable<ScopeItem>(Integer.MAX_VALUE, ScopeDatabase.ScopeItem.KEY_PROVIDER);
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

		idColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return object.getHumandReadableId();
			}
		};
		idColumn.setSortable(true);
		sortHandler.setComparator(idColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return Long.valueOf(o1.getHumandReadableId()).compareTo(Long.valueOf(o2.getHumandReadableId()));
			}
		});
		cellTable.setColumnWidth(idColumn, 36, Unit.PX);
		idColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.addColumn(idColumn, new TextHeader(messages.id()));

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
		cellTable.setColumnWidth(descriptionColumn, 100, Unit.PCT);
		cellTable.addColumn(descriptionColumn, new TextHeader(messages.scopeDescription()));

		final Column<ScopeItem, String> effortColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return ClientDecimalFormat.roundFloat(object.getEffort(), 1) + "ep";
			}
		};
		effortColumn.setSortable(true);
		sortHandler.setComparator(effortColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getEffort().compareTo(o2.getEffort());
			}
		});
		cellTable.addColumn(effortColumn, new TextHeader(messages.effort()));
		effortColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cellTable.setColumnWidth(effortColumn, 54, Unit.PX);

		final Column<ScopeItem, String> valueColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return ClientDecimalFormat.roundFloat(object.getValue(), 1) + "vp";
			}
		};
		valueColumn.setSortable(true);
		sortHandler.setComparator(valueColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		cellTable.addColumn(valueColumn, new TextHeader(messages.value()));
		valueColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cellTable.setColumnWidth(valueColumn, 54, Unit.PX);

		final Column<ScopeItem, String> progressColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				return ClientDecimalFormat.roundFloat(object.getProgress(), 0) + "%";
			}
		};
		progressColumn.setSortable(true);
		sortHandler.setComparator(progressColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return o1.getProgress().compareTo(o2.getProgress());
			}
		});
		cellTable.addColumn(progressColumn, new TextHeader(messages.progress()));
		progressColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cellTable.setColumnWidth(progressColumn, 72, Unit.PX);

		final Column<ScopeItem, String> cycleTimeColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				final Long cycleTime = object.getCycleTime();
				return cycleTime == null ? "---" : HumanDateFormatter.get().formatTimeDifference(cycleTime);
			}
		};
		cycleTimeColumn.setSortable(true);
		sortHandler.setComparator(cycleTimeColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return compareLong(o1.getCycleTime(), o2.getCycleTime());
			}
		});
		cellTable.addColumn(cycleTimeColumn, new TextHeader(messages.cycleTime()));
		cycleTimeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.setColumnWidth(cycleTimeColumn, 108, Unit.PX);

		final Column<ScopeItem, String> leadTimeColumn = new Column<ScopeItem, String>(new TextCell()) {
			@Override
			public String getValue(final ScopeItem object) {
				final Long leadTime = object.getLeadTime();
				return leadTime == null ? "---" : HumanDateFormatter.get().formatTimeDifference(leadTime);
			}
		};
		leadTimeColumn.setSortable(true);
		sortHandler.setComparator(leadTimeColumn, new Comparator<ScopeItem>() {
			@Override
			public int compare(final ScopeItem o1, final ScopeItem o2) {
				return compareLong(o1.getLeadTime(), o2.getLeadTime());
			}
		});
		cellTable.addColumn(leadTimeColumn, new TextHeader(messages.leadTime()));
		leadTimeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.setColumnWidth(leadTimeColumn, 108, Unit.PX);

	}

	private int compareLong(Long l1, Long l2) {
		if (l1 == null) l1 = Long.MIN_VALUE;
		if (l2 == null) l2 = Long.MIN_VALUE;
		return l1.compareTo(l2);
	}

	public void showOnlyEpicColumns() {
		cellTable.removeColumn(idColumn);
		cellTable.getElement().addClassName("cellTableEpicTable");
	}
}
