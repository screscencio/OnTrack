package br.com.oncast.ontrack.client.ui.components.report;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.report.ImpedimentDatabase.ImpedimentItem;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class ImpedimentReportTable extends Composite {

	private static ScopeReportTableUiBinder uiBinder = GWT.create(ScopeReportTableUiBinder.class);

	interface ScopeReportTableUiBinder extends UiBinder<Widget, ImpedimentReportTable> {}

	@UiField(provided = true)
	CellTable<ImpedimentItem> cellTable;

	private final ImpedimentDatabase impedimentDatabase;

	private final ReportMessages messages;

	public ImpedimentReportTable(final List<Scope> scopeList, final ProjectContext context, final ReportMessages messages) {
		this.messages = messages;
		cellTable = new CellTable<ImpedimentItem>(ImpedimentDatabase.ImpedimentItem.KEY_PROVIDER);
		impedimentDatabase = new ImpedimentDatabase(scopeList, context);
		final ListHandler<ImpedimentItem> sortHandler = new ListHandler<ImpedimentItem>(impedimentDatabase.getDataProvider().getList());
		final SelectionModel<ImpedimentItem> selectionModel = new NoSelectionModel<ImpedimentItem>(ImpedimentDatabase.ImpedimentItem.KEY_PROVIDER);
		cellTable.addColumnSortHandler(sortHandler);
		cellTable.setSelectionModel(selectionModel);
		cellTable.setWidth("100%", true);
		cellTable.setPageSize(Integer.MAX_VALUE);
		initTableColumns(selectionModel, sortHandler);
		impedimentDatabase.addDataDisplay(cellTable);

		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTableColumns(final SelectionModel<ImpedimentItem> selectionModel, final ListHandler<ImpedimentItem> sortHandler) {

		final Column<ImpedimentItem, String> stateColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				return object.getState();
			}
		};
		stateColumn.setSortable(true);
		sortHandler.setComparator(stateColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getState().compareTo(o2.getState());
			}
		});
		cellTable.addColumn(stateColumn, new TextHeader(messages.status()));
		stateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.setColumnWidth(stateColumn, 90, Unit.PX);

		final Column<ImpedimentItem, String> descriptionColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				return object.getDescription();
			}
		};
		descriptionColumn.setSortable(true);
		sortHandler.setComparator(descriptionColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		cellTable.addColumn(descriptionColumn, new TextHeader(messages.impedimentDescription()));

		final Column<ImpedimentItem, String> idColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				return object.getHumandReadableId();
			}
		};
		idColumn.setSortable(true);
		sortHandler.setComparator(idColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getHumandReadableId().compareTo(o2.getHumandReadableId());
			}
		});
		cellTable.setColumnWidth(idColumn, 36, Unit.PX);
		cellTable.addColumn(idColumn, new TextHeader(messages.id()));

		final Column<ImpedimentItem, String> startDateColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				final Date date = object.getStartDate();
				return (date == null) ? "---" : HumanDateFormatter.getShortAbsuluteDate(date);
			}
		};
		startDateColumn.setSortable(true);
		sortHandler.setComparator(startDateColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});
		cellTable.addColumn(startDateColumn, new TextHeader(messages.startDate()));
		startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.setColumnWidth(startDateColumn, 90, Unit.PX);

		final Column<ImpedimentItem, String> endDateColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				final Date date = object.getEndDate();
				return (date == null) ? "---" : HumanDateFormatter.getShortAbsuluteDate(date);
			}
		};

		endDateColumn.setSortable(true);
		sortHandler.setComparator(endDateColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getEndDate().compareTo(o2.getEndDate());
			}
		});
		cellTable.addColumn(endDateColumn, new TextHeader(messages.endDate()));
		endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cellTable.setColumnWidth(endDateColumn, 90, Unit.PX);

		final Column<ImpedimentItem, String> leadTimeColumn = new Column<ImpedimentItem, String>(new TextCell()) {
			@Override
			public String getValue(final ImpedimentItem object) {
				final Long time = object.getCycletime();
				return time == 0 ? "---" : HumanDateFormatter.getDifferenceText(time, 0);
			}
		};
		leadTimeColumn.setSortable(true);
		sortHandler.setComparator(leadTimeColumn, new Comparator<ImpedimentItem>() {
			@Override
			public int compare(final ImpedimentItem o1, final ImpedimentItem o2) {
				return o1.getCycletime().compareTo(o2.getCycletime());
			}
		});
		cellTable.addColumn(leadTimeColumn, new TextHeader(messages.leadTime()));
		cellTable.setColumnWidth(leadTimeColumn, 108, Unit.PX);
	}

	public boolean isEmpty() {
		return impedimentDatabase.isEmpty();
	}
}
