package br.com.oncast.ontrack.client.ui.places.timesheet.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.selection.SelectionControllerDefault;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ScopeIdAndDescriptionWidget;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.services.ClientServices.getCurrentUser;

public class TimesheetWidget extends Composite {

	private static TimesheetWidgetUiBinder uiBinder = GWT.create(TimesheetWidgetUiBinder.class);

	interface TimesheetWidgetUiBinder extends UiBinder<Widget, TimesheetWidget> {}

	interface TimesheetPanelStyle extends CssResource {

		String scopeDescriptionCell();

		String totalSumCell();

		String row();

		String currentUserColumn();

		String columnSumCell();

		String rowSumCell();

		String boldCell();
	}

	@UiField
	TimesheetPanelStyle style;

	@UiField
	FlexTable timesheet;

	@UiField
	FocusPanel focusPanel;

	private final Float[][] times;

	private final Release release;

	private final boolean readOnly;

	private final SelectionControllerDefault selectionController;

	public TimesheetWidget(final Release release, final boolean readOnly) {
		this.release = release;
		this.readOnly = readOnly;
		this.selectionController = new SelectionControllerDefault();
		times = new Float[release.getScopeList().size()][getUsers().size()];

		initWidget(uiBinder.createAndBindUi(this));
		initTimesheetTable(readOnly);
	}

	public void updateTimeSpent(final UUID scopeId, final UUID userId) {
		final int row = getScopeRowIndex(scopeId);
		final int column = getUserColumnIndex(userId);
		if (row == -1 || column == -1) return;

		final Float previousValue = times[row - 1][column - 1];
		final float newValue = getTimeSpent(scopeId, userId);
		final float difference = newValue - previousValue;

		times[row - 1][column - 1] = newValue;
		final Widget widget = timesheet.getWidget(row, column);
		if (widget == null) timesheet.setText(row, column, newValue + "");
		else ((EditableLabel) widget).setValue(round(newValue), false);

		final int lastColumn = timesheet.getCellCount(row) - 1;
		final int lastRow = timesheet.getRowCount() - 1;

		timesheet.setText(row, lastColumn, round(getRowSum(row)) + "");
		timesheet.setText(lastRow, column, round(getColumnSum(column)) + "");

		final Float previousTotal = Float.valueOf(timesheet.getText(lastRow, lastColumn));
		timesheet.setText(lastRow, lastColumn, round(previousTotal + difference) + "");
	}

	public void updateScopeDescriptions(final UUID scopeId) {
		final List<Scope> scopes = getScopes();

		for (final Scope s : scopes) {
			if (s.getId().equals(scopeId)) {
				timesheet.setText(scopes.indexOf(s) + 1, 0, s.getDescription());
			}
		}
	}

	private void initTimesheetTable(final boolean readOnly) {
		final List<Scope> scopes = getScopes();
		final int lastRow = scopes.size() + 1;
		final List<UserRepresentation> users = getUsers();
		final int lastColumn = users.size() + 1;

		timesheet.setText(0, 0, "");
		timesheet.setText(lastRow, lastColumn, "");

		final CellFormatter cellFormatter = timesheet.getCellFormatter();
		final RowFormatter rowFormatter = timesheet.getRowFormatter();

		for (int i = 1; i < lastRow; i++) {
			rowFormatter.addStyleName(i, style.row());
			cellFormatter.addStyleName(i, 0, style.scopeDescriptionCell());
			cellFormatter.addStyleName(i, lastColumn, style.rowSumCell());
		}

		for (int i = 1; i < lastColumn; i++) {
			cellFormatter.addStyleName(lastRow, i, style.columnSumCell());
		}

		if (!readOnly) timesheet.getColumnFormatter().addStyleName(getUserColumnIndex(getCurrentUser()), style.currentUserColumn());
		cellFormatter.addStyleName(lastRow, lastColumn, style.totalSumCell());

		populateTable(scopes, users, readOnly);
	}

	private void populateTable(final List<Scope> scopes, final List<UserRepresentation> users, final boolean readOnly) {
		for (int i = 0; i < scopes.size(); i++) {
			final Scope scope = scopes.get(i);

			for (int j = 0; j < users.size(); j++) {
				final UserRepresentation user = users.get(j);

				final float value = getTimeSpent(scope.getId(), user.getId());
				times[i][j] = value;

				if (!readOnly && user.equals(getCurrentUser())) {
					final ScopeTimeSpentWidget widget = new ScopeTimeSpentWidget(scope, user, value, selectionController);
					selectionController.addSelectableWidget(widget);
					timesheet.setWidget(i + 1, j + 1, widget);
				}
				else timesheet.setText(i + 1, j + 1, round(value));
			}
		}

		for (int i = 1; i < scopes.size() + 1; i++) {
			final Scope scope = scopes.get(i - 1);
			timesheet.setWidget(i, 0, new ScopeIdAndDescriptionWidget(scope));
			timesheet.getFlexCellFormatter().addStyleName(i, users.size() + 1, style.boldCell());
			timesheet.setText(i, users.size() + 1, round(getRowSum(i)));
		}

		float totalSum = 0;
		for (int j = 1; j < users.size() + 1; j++) {
			final UserRepresentation user = users.get(j - 1);
			timesheet.setWidget(0, j, new UserWidget(user));

			final float sum = getColumnSum(j);
			timesheet.getFlexCellFormatter().addStyleName(scopes.size() + 1, j, style.boldCell());
			timesheet.setText(scopes.size() + 1, j, round(sum));
			totalSum += sum;
		}

		timesheet.getFlexCellFormatter().addStyleName(scopes.size() + 1, users.size() + 1, style.boldCell());
		timesheet.setText(scopes.size() + 1, users.size() + 1, round(totalSum));
	}

	private float getColumnSum(final int column) {
		Float result = 0F;
		for (final Float[] row : times)
			result += row[column - 1];

		return result;
	}

	private float getRowSum(final int row) {
		Float result = 0F;
		for (final Float value : times[row - 1])
			result += value;

		return result;
	}

	private int getUserColumnIndex(final UUID userId) {
		final List<UserRepresentation> users = getUsers();
		for (final UserRepresentation u : users) {
			if (u.getId().equals(userId)) return users.indexOf(u) + 1;
		}
		return -1;
	}

	private int getScopeRowIndex(final UUID scopeId) {
		final List<Scope> scopes = getScopes();
		for (final Scope s : scopes) {
			if (s.getId().equals(scopeId)) return scopes.indexOf(s) + 1;
		}
		return -1;
	}

	private float getTimeSpent(final UUID scopeId, final UUID userId) {
		final Float declaredTimeSpent = getContext().getDeclaredTimeSpent(scopeId, userId);
		return declaredTimeSpent == null ? 0.0f : declaredTimeSpent;
	}

	private List<Scope> getScopes() {
		return release.getScopeList();
	}

	private List<UserRepresentation> getUsers() {
		if (!readOnly) return getContext().getUsers();

		final List<UserRepresentation> users = new ArrayList<UserRepresentation>();
		for (final UserRepresentation user : getContext().getUsers()) {
			if (hasDeclaredSpentHours(user)) users.add(user);
		}
		return users;
	}

	private boolean hasDeclaredSpentHours(final UserRepresentation user) {
		for (final Scope scope : getScopes()) {
			if (getContext().getDeclaredTimeSpent(scope.getId(), user.getId()) != null) return true;
		}

		return false;
	}

	private ProjectContext getContext() {
		return ClientServices.getCurrentProjectContext();
	}

	private String round(final float number) {
		return ClientDecimalFormat.roundFloat(number, 1);
	}

	public boolean isEmpty() {
		return getUsers().isEmpty();
	}

	@UiHandler("focusPanel")
	protected void onClick(final ClickEvent event) {
		selectionController.deselectAll();
	}

}
