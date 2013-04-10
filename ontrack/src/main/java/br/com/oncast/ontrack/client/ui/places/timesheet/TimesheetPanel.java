package br.com.oncast.ontrack.client.ui.places.timesheet;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.timesheet.widgets.TimesheetWidget;
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
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TimesheetPanel extends Composite implements ModelWidget<Release>, PopupAware, HasCloseHandlers<TimesheetPanel> {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static TimesheetPanelUiBinder uiBinder = GWT.create(TimesheetPanelUiBinder.class);

	@UiField
	HorizontalPanel timesheetContainer;

	@UiField
	SimplePanel contentContainer;

	@UiField(provided = true)
	EditableLabel releaseTitle;

	@UiField
	Button previousReleaseButton;

	@UiField
	Button nextReleaseButton;

	TimesheetWidget timesheet;

	TimesheetWidget lastTimesheet;

	private Release release;

	private ActionExecutionListener actionExecutionListener;

	private Release previousRelease;

	private Release nextRelease;

	private final TimesheetAnimation animation;

	interface TimesheetPanelUiBinder extends UiBinder<Widget, TimesheetPanel> {}

	private class TimesheetAnimation extends Animation {

		private int duration;
		private boolean toRight;

		@Override
		protected void onStart() {
			updateOpacity(timesheet, 0);
			updatePosition(timesheet, -1000);

			if (timesheetContainer.getOffsetWidth() <= contentContainer.getOffsetWidth()) contentContainer.getElement().getStyle()
					.setOverflowX(Overflow.HIDDEN);

			if (animation.isSlideDirectionToRight()) timesheetContainer.insert(timesheet, 0);
			else timesheetContainer.add(timesheet);
		}

		@Override
		protected void onUpdate(final double progress) {
			final double interpolatedProgress = interpolate(progress);
			updatePosition(timesheet, -timesheet.getOffsetWidth() + interpolatedProgress * timesheet.getOffsetWidth());
			updateOpacity(timesheet, interpolatedProgress);
			updatePosition(lastTimesheet, -(interpolatedProgress * lastTimesheet.getOffsetWidth()));
			updateOpacity(lastTimesheet, 1 - interpolatedProgress);
		}

		@Override
		protected void onComplete() {
			setDuration(0);

			if (lastTimesheet != null) {
				lastTimesheet.removeFromParent();
				lastTimesheet = null;
			}

			contentContainer.getElement().getStyle().setOverflowX(Overflow.AUTO);

			timesheet.setVisible(true);
			clear(timesheet);
		}

		public void setSlideDirection(final boolean toRight) {
			this.toRight = toRight;
			this.duration = 3000;
		}

		public boolean isSlideDirectionToRight() {
			return duration > 0 && toRight;
		}

		public void slide() {
			if (duration <= 0) {
				timesheetContainer.add(timesheet);
				onComplete();
			}
			else Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					run(duration);
				}
			});
		}

		private void updateOpacity(final Widget widget, final double opacity) {
			widget.getElement().getStyle().setOpacity(opacity);
		}

		private void updatePosition(final Widget widget, final double value) {
			if (toRight) widget.getElement().getStyle().setMarginRight(value, Unit.PX);
			else widget.getElement().getStyle().setMarginLeft(value, Unit.PX);
		}

		public void setDuration(final int duration) {
			this.duration = duration;
		}

		private void clear(final Widget widget) {
			final Style s = widget.getElement().getStyle();
			s.clearMarginLeft();
			s.clearMarginRight();
			s.clearOpacity();
		}

	}

	public TimesheetPanel() {
		initializeReleaseTitle();
		animation = new TimesheetAnimation();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setRelease(final Release release, final ProjectContext projectContext) {
		if (release == null) throw new RuntimeException("Release cannot be null");

		if (release.equals(previousRelease)) animation.setSlideDirection(true);
		else if (release.equals(nextRelease)) animation.setSlideDirection(false);
		else animation.setDuration(0);

		this.release = release;
		setupReleaseNavigationButtons();

		update();
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

		lastTimesheet = timesheet;
		timesheet = new TimesheetWidget(release, false);

		animation.slide();
		return false;
	}

	private void updateReleaseTitle() {
		releaseTitle.setValue(release.getDescription());
	}

	@UiHandler("previousReleaseButton")
	public void onPreviousReleaseClick(final ClickEvent event) {
		SERVICE_PROVIDER.getTimesheetService().showTimesheetFor(previousRelease.getId());
	}

	@UiHandler("nextReleaseButton")
	public void onNextReleaseClick(final ClickEvent event) {
		SERVICE_PROVIDER.getTimesheetService().showTimesheetFor(nextRelease.getId());
	}

	@UiHandler("closeIcon")
	public void onCloseClick(final ClickEvent event) {
		hide();
	}

	@Override
	public Release getModelObject() {
		return release;
	}

	public void unregisterActionExecutionListener() {
		ClientServices.get().actionExecution().removeActionExecutionListener(actionExecutionListener);
	}

	public void registerActionExecutionListener() {
		ClientServices.get().actionExecution().addActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener == null ? actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {

				if (action instanceof ScopeDeclareTimeSpentAction) timesheet.updateTimeSpent(action.getReferenceId(), actionContext.getUserId());
				else if (action instanceof ScopeBindReleaseAction && (isRemovingFromMyRelease(action) || isAddingToMyRelease((ScopeBindReleaseAction) action))) update();
				else if (action instanceof TeamAction) update();
				else if (action instanceof ReleaseRenameAction) updateReleaseTitle();
				else if (action instanceof ScopeUpdateAction) timesheet.updateScopeDescriptions(action.getReferenceId());
			}

			private boolean isAddingToMyRelease(final ScopeBindReleaseAction action) {
				try {
					return release.equals(getContext().findRelease(action.getNewReleaseDescription()));
				}
				catch (final ReleaseNotFoundException e) {
					return false;
				}
			}

			private boolean isRemovingFromMyRelease(final ModelAction action) {
				for (final Scope s : release.getScopeList()) {
					if (s.getId().equals(action.getReferenceId())) return true;
				}
				return false;
			}

		}
				: actionExecutionListener;
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<TimesheetPanel> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

	private void initializeReleaseTitle() {
		releaseTitle = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				final ProjectContext projectContext = getContext();
				final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.actionExecution();

				try {
					projectContext.findRelease(release.getId());
					actionExecutionService.onUserActionExecutionRequest(new ReleaseRenameAction(release.getId(), text));
				}
				catch (final ReleaseNotFoundException e1) {
					throw new RuntimeException("Impossible to create an editable label for this annotation.");
				}
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		});
	}

	private ProjectContext getContext() {
		return ClientServices.getCurrentProjectContext();
	}
}
