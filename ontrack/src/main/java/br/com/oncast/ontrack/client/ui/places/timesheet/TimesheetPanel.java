package br.com.oncast.ontrack.client.ui.places.timesheet;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.timesheet.widgets.ScopeHourAppointmentWidget;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.Release.Condition;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TimesheetPanel extends Composite implements ModelWidget<Release>, PopupAware, HasCloseHandlers<TimesheetPanel> {

	private static TimesheetPanelUiBinder uiBinder = GWT.create(TimesheetPanelUiBinder.class);

	interface TimesheetPanelStyle extends CssResource {

		String scopeDescriptionCell();

		String sumCell();

		String row();

		String currentUserColumn();

	}

	@UiField
	TimesheetPanelStyle style;

	@UiField
	Label releaseTitle;

	@UiField
	FlexTable timesheet;

	private final Release release;

	private final SetMultimap<Scope, ScopeHourAppointmentWidget> widgetsByScopeCache;

	private final SetMultimap<UserRepresentation, ScopeHourAppointmentWidget> widgetsByUserCache;

	interface TimesheetPanelUiBinder extends UiBinder<Widget, TimesheetPanel> {}

	public TimesheetPanel(final Release release) {
		this.release = release;

		widgetsByScopeCache = HashMultimap.create();
		widgetsByUserCache = HashMultimap.create();

		initWidget(uiBinder.createAndBindUi(this));

		update();
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		CloseEvent.fire(this, this);
	}

	@Override
	public boolean update() {
		releaseTitle.setText(release.getDescription());

		final List<UserRepresentation> users = ClientServiceProvider.getCurrentProjectContext().getUsers();
		final List<Scope> scopes = release.getScopeList();
		final int lastRow = scopes.size() + 1;
		final int lastColumn = users.size() + 1;

		timesheet.setCellSpacing(0);
		timesheet.setCellPadding(5);

		final CellFormatter cellFormatter = timesheet.getCellFormatter();
		final RowFormatter rowFormatter = timesheet.getRowFormatter();

		for (int i = 0; i < scopes.size(); i++) {
			final Scope scope = scopes.get(0);
			for (int j = 0; j < users.size(); j++) {
				final UserRepresentation user = users.get(j);
				final ScopeHourAppointmentWidget widget = new ScopeHourAppointmentWidget(scope, user);
				widgetsByScopeCache.put(scope, widget);
				widgetsByUserCache.put(user, widget);
				timesheet.setWidget(i + 1, j + 1, widget);
			}
		}

		for (int i = 0; i < scopes.size(); i++) {
			final Scope scope = scopes.get(i);
			timesheet.setText(i + 1, 0, scope.getDescription());
			timesheet.setText(i + 1, lastColumn, round(getAppointmentSum(widgetsByScopeCache.get(scope))));
			rowFormatter.addStyleName(i + 1, style.row());
			cellFormatter.addStyleName(i + 1, 0, style.scopeDescriptionCell());
			cellFormatter.addStyleName(i + 1, lastColumn, style.sumCell());
		}

		float totalSum = 0;
		final UUID currentUser = ClientServiceProvider.getCurrentUser();
		int currentUserColumn = -1;
		for (int j = 0; j < users.size(); j++) {
			final UserRepresentation user = users.get(j);
			timesheet.setWidget(0, j + 1, new UserWidget(user));
			final float appointment = getAppointmentSum(widgetsByUserCache.get(user));
			timesheet.setText(lastRow, j + 1, round(appointment));
			cellFormatter.addStyleName(lastRow, j + 1, style.sumCell());
			totalSum += appointment;
			if (user.getId().equals(currentUser)) currentUserColumn = j + 1;
		}

		timesheet.getColumnFormatter().addStyleName(currentUserColumn, style.currentUserColumn());
		cellFormatter.addStyleName(lastRow, lastColumn, style.sumCell());
		timesheet.setText(lastRow, lastColumn, round(totalSum));

		return false;
	}

	private String round(final float number) {
		return ClientDecimalFormat.roundFloat(number, 1);
	}

	private float getAppointmentSum(final Set<ScopeHourAppointmentWidget> set) {
		float sum = 0;
		for (final ScopeHourAppointmentWidget widget : set) {
			sum += widget.getAppointedHour();
		}
		return sum;
	}

	@UiHandler("previousRelease")
	public void onPreviousReleaseClick(final ClickEvent event) {
		final Release previousRelease = release.getLatestPastRelease(new Condition() {
			@Override
			public boolean eval(final Release release) {
				return release.getDescription().contains("*");
			}
		});

		ClientServiceProvider.getInstance().getTimesheetService().showTimesheetFor(previousRelease.getId());
	}

	@UiHandler("nextRelease")
	public void onNextReleaseClick(final ClickEvent event) {
		final Release nextRelease = release.getFirstFutureRelease(new Condition() {
			@Override
			public boolean eval(final Release release) {
				return release.getDescription().contains("*");
			}
		});

		ClientServiceProvider.getInstance().getTimesheetService().showTimesheetFor(nextRelease.getId());
	}

	@Override
	public Release getModelObject() {
		return release;
	}

	public void unregisterActionExecutionListener() {
		// FIXME Auto-generated catch block
	}

	public void registerActionExecutionListener() {
		// FIXME Auto-generated catch block
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<TimesheetPanel> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

}
