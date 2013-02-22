package br.com.oncast.ontrack.client.ui.places.timesheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.timesheet.widgets.ScopeTimeSpentWidget;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareTimeSpentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.Release.Condition;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Widget;

public class TimesheetPanel extends Composite implements ModelWidget<Release>, PopupAware, HasCloseHandlers<TimesheetPanel> {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static TimesheetPanelUiBinder uiBinder = GWT.create(TimesheetPanelUiBinder.class);

	interface TimesheetPanelStyle extends CssResource {

		String scopeDescriptionCell();

		String sumCell();

		String row();

		String currentUserColumn();

	}

	@UiField
	TimesheetPanelStyle style;

	@UiField(provided = true)
	EditableLabel releaseTitle;

	@UiField
	FlexTable timesheet;

	@UiField
	Button previousReleaseButton;

	@UiField
	Button nextReleaseButton;

	private final Release release;

	private ActionExecutionListener actionExecutionListener;

	private final SetMultimap<UUID, ScopeTimeSpentWidget> widgetsByScopeCache = HashMultimap.create();

	private final SetMultimap<UUID, ScopeTimeSpentWidget> widgetsByUserCache = HashMultimap.create();

	private Release previousRelease;

	private Release nextRelease;

	private final List<Float> rowSums = new ArrayList<Float>();

	private final List<Float> columnSums = new ArrayList<Float>();

	interface TimesheetPanelUiBinder extends UiBinder<Widget, TimesheetPanel> {}

	public TimesheetPanel(final Release release) {
		this.release = release;

		initializeReleaseTitle();
		initWidget(uiBinder.createAndBindUi(this));

		update();

		setupReleaseNavigationButtons();
	}

	private void setupReleaseNavigationButtons() {
		previousRelease = release.getLatestPastRelease(new Condition() {
			@Override
			public boolean eval(final Release release) {
				return !release.isRoot();
			}
		});
		previousReleaseButton.setEnabled(previousRelease != null);

		nextRelease = release.getFirstFutureRelease(new Condition() {
			@Override
			public boolean eval(final Release release) {
				return !release.isRoot();
			}
		});

		nextReleaseButton.setEnabled(nextRelease != null);
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
		updateReleaseTitle();

		final List<UserRepresentation> users = ClientServiceProvider.getCurrentProjectContext().getUsers();
		final List<Scope> scopes = release.getScopeList();
		final int lastRow = scopes.size() + 1;
		final int lastColumn = users.size() + 1;

		mountTable(lastRow, lastColumn);

		populateTable(scopes, users);

		return false;
	}

	private void updateSum(final UUID scopeId, final UUID userId) {
		float difference = 0;
		for (final ScopeTimeSpentWidget w : widgetsByScopeCache.get(scopeId)) {
			if (w.getUser().getId().equals(userId)) {
				final float previous = w.getTimeSpent();
				w.update();
				difference = w.getTimeSpent() - previous;
			}
		}

		final int row = getScopeRowIndex(scopeId);
		final int column = getUserColumnIndex(userId);
		if (row == -1 || column == -1) return;

		final float newRowSum = rowSums.get(row) + difference;
		rowSums.add(row, newRowSum);
		timesheet.setText(row, timesheet.getCellCount(row) - 1, round(newRowSum));
		final float newColumnsSum = columnSums.get(column) + difference;
		columnSums.add(column, newColumnsSum);
		timesheet.setText(timesheet.getRowCount() - 1, column, round(newColumnsSum));

	}

	private void populateTable(final List<Scope> scopes, final List<UserRepresentation> users) {
		widgetsByScopeCache.clear();
		widgetsByUserCache.clear();

		for (int i = 0; i < scopes.size(); i++) {
			final Scope scope = scopes.get(i);
			for (int j = 0; j < users.size(); j++) {
				final UserRepresentation user = users.get(j);

				final ScopeTimeSpentWidget widget = new ScopeTimeSpentWidget(scope, user);
				widgetsByScopeCache.put(scope.getId(), widget);
				widgetsByUserCache.put(user.getId(), widget);

				timesheet.setWidget(i + 1, j + 1, widget);
			}
		}

		rowSums.clear();
		rowSums.add(-1F);
		for (int i = 1; i < scopes.size() + 1; i++) {
			final Scope scope = scopes.get(i - 1);
			timesheet.setText(i, 0, scope.getDescription());
			final float sum = getTimeSpentSum(widgetsByScopeCache.get(scope.getId()));
			rowSums.add(sum);
			timesheet.setText(i, users.size() + 1, round(sum));
		}

		columnSums.clear();
		columnSums.add(-1F);
		float totalSum = 0;
		for (int j = 1; j < users.size() + 1; j++) {
			final UserRepresentation user = users.get(j - 1);
			timesheet.setWidget(0, j, new UserWidget(user));
			final float sum = getTimeSpentSum(widgetsByUserCache.get(user.getId()));
			columnSums.add(sum);
			timesheet.setText(scopes.size() + 1, j, round(sum));
			totalSum += sum;
		}

		timesheet.setText(scopes.size() + 1, users.size() + 1, round(totalSum));
	}

	private void mountTable(final int lastRow, final int lastColumn) {
		timesheet.clear();

		timesheet.setText(0, 0, "");
		timesheet.setText(lastRow, lastColumn, "");

		final CellFormatter cellFormatter = timesheet.getCellFormatter();
		final RowFormatter rowFormatter = timesheet.getRowFormatter();

		for (int i = 1; i < lastRow; i++) {
			rowFormatter.addStyleName(i, style.row());
			cellFormatter.addStyleName(i
					, 0, style.scopeDescriptionCell());
			cellFormatter.addStyleName(i, lastColumn, style.sumCell());
		}

		for (int j = 1; j < lastColumn; j++) {
			cellFormatter.addStyleName(lastRow, j, style.sumCell());
		}

		timesheet.getColumnFormatter().addStyleName(getCurrentUserColumnIndex(), style.currentUserColumn());
		cellFormatter.addStyleName(lastRow, lastColumn, style.sumCell());
	}

	private int getCurrentUserColumnIndex() {
		final UUID currentUser = ClientServiceProvider.getCurrentUser();
		return getUserColumnIndex(currentUser);
	}

	private int getUserColumnIndex(final UUID userId) {
		final List<UserRepresentation> users = ClientServiceProvider.getCurrentProjectContext().getUsers();
		for (final UserRepresentation u : users) {
			if (u.getId().equals(userId)) return users.indexOf(u) + 1;
		}
		return -1;
	}

	private int getScopeRowIndex(final UUID scopeId) {
		final List<Scope> scopes = release.getScopeList();
		for (final Scope s : scopes) {
			if (s.getId().equals(scopeId)) return scopes.indexOf(s) + 1;
		}
		return -1;
	}

	private float getTimeSpentSum(final Set<ScopeTimeSpentWidget> set) {
		float sum = 0;
		for (final ScopeTimeSpentWidget widget : set) {
			sum += widget.getTimeSpent();
		}
		return sum;
	}

	private void updateScopeDescriptions(final UUID scopeId) {
		final List<Scope> scopes = release.getScopeList();

		for (final Scope s : scopes) {
			if (s.getId().equals(scopeId)) {
				timesheet.setText(scopes.indexOf(s) + 1, 0, s.getDescription());
			}
		}
	}

	private void updateReleaseTitle() {
		releaseTitle.setValue(release.getDescription());
	}

	private String round(final float number) {
		return ClientDecimalFormat.roundFloat(number, 1);
	}

	@UiHandler("previousReleaseButton")
	public void onPreviousReleaseClick(final ClickEvent event) {
		SERVICE_PROVIDER.getTimesheetService().showTimesheetFor(previousRelease.getId());
	}

	@UiHandler("nextReleaseButton")
	public void onNextReleaseClick(final ClickEvent event) {
		SERVICE_PROVIDER.getTimesheetService().showTimesheetFor(nextRelease.getId());
	}

	@Override
	public Release getModelObject() {
		return release;
	}

	public void unregisterActionExecutionListener() {
		ClientServiceProvider.getInstance().getActionExecutionService().removeActionExecutionListener(actionExecutionListener);
	}

	public void registerActionExecutionListener() {
		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener == null ? actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {

				if (action instanceof ScopeDeclareTimeSpentAction && widgetsByScopeCache.containsKey(action.getReferenceId())) updateSum(
						action.getReferenceId(), actionContext.getUserId());

				else if (action instanceof ScopeBindReleaseAction) update();
				else if (action instanceof TeamAction) update();
				else if (action instanceof ReleaseRenameAction) updateReleaseTitle();
				else if (action instanceof ScopeUpdateAction) updateScopeDescriptions(action.getReferenceId());
			}

		} : actionExecutionListener;
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<TimesheetPanel> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

	private void initializeReleaseTitle() {
		releaseTitle = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				final ProjectContext projectContext = ClientServiceProvider.getCurrentProjectContext();
				final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();

				try {
					projectContext.findRelease(release.getId());
					actionExecutionService.onUserActionExecutionRequest(new ReleaseRenameAction(release.getId(), text));
				}
				catch (final ReleaseNotFoundException e1) {
					throw new RuntimeException("Impossible to create an editable label for this annotation.");
				}
				return true;
			}
		});
	}

}
