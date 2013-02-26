package br.com.oncast.ontrack.client.ui.components.report;

import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.report.ScopeDatabase.ScopeItem;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
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

	public ScopeReportTable(final List<Scope> scopeList) {
		cellTable = new CellTable<ScopeItem>(ScopeDatabase.ScopeItem.KEY_PROVIDER);
		scopeDatabase = new ScopeDatabase(scopeList);
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
		cellTable.addColumn(descriptionColumn);
		// cellTable.setColumnWidth(descriptionColumn, 60, Unit.PCT);

		// cellTable.addColumn(descriptionColumn, constants.cwCellTableColumnFirstName());
		// descriptionColumn.setFieldUpdater(new FieldUpdater<ScopeItem, String>() {
		// @Override
		// public void update(final int index, final ScopeItem object, final String value) {
		// // Called when the user changes the value.
		// object.setFirstName(value);
		// ScopeDatabase.get().refreshDisplays();
		// }
		// });
		// cellTable.setColumnWidth(descriptionColumn, 20, Unit.PCT);
	}
}
