package br.com.oncast.ontrack.client.ui.components.scope;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdater;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdaterStyle;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.SpacerCommandMenuItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PercentualBar;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.impediment.ImpedimentListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedTagsWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.date.HumanDateUnit;
import br.com.oncast.ontrack.client.utils.date.TimeDifferenceFormat;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.PrioritizationCriteria;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeCardWidget extends Composite implements ScopeWidget, ModelWidget<Scope>, ActionExecutionListener, HasClickHandlers {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static final CommandMenuMessages MESSAGES = GWT.create(CommandMenuMessages.class);

	private static ScopeCardWidgetUiBinder uiBinder = GWT.create(ScopeCardWidgetUiBinder.class);

	interface ScopeCardWidgetUiBinder extends UiBinder<Widget, ScopeCardWidget> {}

	interface ScopeCardWidgetStyle extends ProgressIconUpdaterStyle {
		String selected();

		String targetHighlight();

		String associationHighlight();

		String largeDetails();
	}

	@UiField
	ScopeCardWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	SpanElement humanIdLabel;

	@UiField
	SpanElement descriptionLabel;

	@UiField
	FocusPanel progressIcon;

	@UiField
	HTMLPanel internalPanel;

	@UiField
	PercentualBar percentualBar;

	@UiField
	DivElement detailsContainer;

	@UiField
	SpanElement effortLabel;

	@UiField
	SpanElement valueLabel;

	@UiField
	DivElement dueDateContainer;

	@UiField
	SpanElement dueDateLabel;

	@UiField(provided = true)
	ScopeAssociatedMembersWidget associatedUsers;

	@UiField(provided = true)
	ScopeAssociatedTagsWidget tags;

	private final Scope scope;

	private String currentScopeDescription;
	private String currentScopeProgress;

	private final boolean releaseSpecific;

	private boolean selected = false;
	private boolean targetHighlight = false;
	private boolean associationHighlight = false;
	private boolean skipScopeSelectionEventOnPopupClose = false;
	private boolean currentScopeHasOpenImpediments = false;

	public ScopeCardWidget(final Scope scope) {
		this(scope, false, null);
	}

	public ScopeCardWidget(final Scope scope, final boolean releaseSpecific, final DragAndDropManager userDragAndDropMananger) {
		associatedUsers = createAssociatedUsersListWidget(scope, userDragAndDropMananger);
		tags = new ScopeAssociatedTagsWidget(scope);
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		this.releaseSpecific = releaseSpecific;

		update();
	}

	@Override
	public boolean update() {
		final boolean shouldBeLarge = updateAssociatedUsers() | updateValues();
		ElementUtils.setClassName(detailsContainer, style.largeDetails(), shouldBeLarge);
		updateTags();
		return updateHumanId() | updateDescription() | updateProgress() | updateTitle();
	}

	private boolean updateAssociatedUsers() {
		boolean isAssociatedUsersVisible = !scope.getProgress().isDone();
		associatedUsers.setShouldShowDone(isAssociatedUsersVisible);
		associatedUsers.update();
		isAssociatedUsersVisible &= associatedUsers.getWidgetCount() > 0;
		return isAssociatedUsersVisible;
	}

	private void updateTags() {
		tags.update();
	}

	private boolean updateTitle() {
		final String title = buildLineageRepresentationText();
		if (title.isEmpty() || title.equals(panel.getTitle())) return false;

		descriptionLabel.setTitle(title);
		return true;
	}

	private boolean updateValues() {
		boolean hasAnyDetails = false;
		hasAnyDetails |= setPriorizationCriteria(effortLabel, "#", scope.getEffort(), "effort points");
		hasAnyDetails |= setPriorizationCriteria(valueLabel, "$", scope.getValue(), "value points");
		final boolean hasDueDate = scope.hasDueDate();
		hasAnyDetails |= hasDueDate;
		if (hasDueDate) {
			dueDateLabel.setInnerText(HumanDateFormatter.get().setMinimum(HumanDateUnit.ONE_DAY).formatDateRelativeToNow(scope.getDueDate()));
			final long remainingTime = ClientServices.get().scopeEstimator().get().getRemainingTime(scope);
			final TimeDifferenceFormat format = HumanDateFormatter.get().setMinimum(HumanDateUnit.ONE_DAY).getTimeDifferenceFormat(remainingTime);
			dueDateLabel.setTitle(format.formatWith(MESSAGES.left(), MESSAGES.late()));
		}
		ElementUtils.setVisible(dueDateContainer, hasDueDate);
		ElementUtils.setBackgroundColor(dueDateContainer, SERVICE_PROVIDER.colorProvider().getDueDateColor(scope), true);
		percentualBar.setPercentual(calculatePercentual(scope));
		return hasAnyDetails;
	}

	private boolean setPriorizationCriteria(final SpanElement label, final String symbol, final PrioritizationCriteria criteria, final String titlePosfix) {
		final String effortStr = ClientDecimalFormat.roundFloat(criteria.getInfered(), 1);
		label.setInnerText(symbol + " " + effortStr);
		label.setTitle(effortStr + " " + titlePosfix);
		final boolean visible = criteria.hasDeclared() || criteria.getInfered() >= 0.1F;
		ElementUtils.setVisible(label, visible);
		return visible;
	}

	private int calculatePercentual(final Scope scope) {
		if (scope.getProgress().isDone()) return 100;
		return (int) (scope.getEffort().getAccomplishedPercentual());
	}

	private String buildLineageRepresentationText() {
		if (scope.isRoot()) return "";

		final StringBuilder builder = new StringBuilder();
		Scope current = scope;
		while (!current.isRoot()) {
			builder.insert(0, current.getDescription());
			builder.insert(0, " > ");
			current = current.getParent();
		}
		builder.insert(0, current.getDescription());
		final String title = builder.toString();
		return title;
	}

	/**
	 * @return if the humanId was updated.
	 */
	private boolean updateHumanId() {
		final String humanId = ClientServices.getCurrentProjectContext().getHumanId(scope.getStory());
		humanIdLabel.setInnerText(humanId);
		ElementUtils.setVisible(humanIdLabel, !humanId.isEmpty());
		return true;
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		final String description = scope.getDescription();
		if (description.equals(currentScopeDescription)) return false;
		currentScopeDescription = description;

		descriptionLabel.setInnerText(currentScopeDescription);

		return true;
	}

	private boolean updateProgress() {
		final Progress progress = scope.getProgress();
		final String description = progress.getDescription();
		final boolean hasOpenImpediments = SERVICE_PROVIDER.details().hasOpenImpediment(scope.getId());

		if (this.currentScopeHasOpenImpediments == hasOpenImpediments && !description.isEmpty() && description.equals(currentScopeProgress)) return false;
		this.currentScopeProgress = description;
		this.currentScopeHasOpenImpediments = hasOpenImpediments;

		final ProgressIconUpdater updater = ProgressIconUpdater.getUpdater(scope, currentScopeHasOpenImpediments);
		progressIcon.setStyleName(updater.getStyle(style));
		progressIcon.setTitle(updater.getTitle(scope));
		if (!description.isEmpty()) updater.animate(internalPanel);

		if (releaseSpecific) updateStoryColor(progress);

		return true;
	}

	private void updateStoryColor(final Progress progress) {
		final Style s = ElementUtils.getStyle(humanIdLabel);

		if (!progress.isNotStarted()) s.setBackgroundColor(SERVICE_PROVIDER.colorProvider().getColorFor(scope.getStory()).toCssRepresentation());
		else s.clearBackgroundColor();
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}

	public Widget getDraggableAnchor() {
		return panel;
	}

	@UiHandler("progressIcon")
	public void onProgressIconMouseDown(final MouseDownEvent e) {
		fireScopeSelectionEvent(); // Showing the item that will be changed.
	}

	@UiHandler("progressIcon")
	public void onProgressIconClick(final ClickEvent e) {
		e.stopPropagation();
		showPopup(currentScopeHasOpenImpediments ? new ImpedimentListWidget(scope) : createProgressMenu());
	}

	private Widget createProgressMenu() {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();
		final ProjectContext context = ClientServices.getCurrentProjectContext();

		for (final String progressDefinition : context.getProgressDefinitions(scope))
			items.add(createItem(ProgressState.getLabelForDescription(progressDefinition), progressDefinition));
		items.add(new SpacerCommandMenuItem());
		items.add(new TextAndImageCommandMenuItem("icon-flag", MESSAGES.impediments(), new Command() {
			@Override
			public void execute() {
				skipScopeSelectionEventOnPopupClose = true;
				showPopup(new ImpedimentListWidget(scope));
			}
		}));

		final FiltrableCommandMenu commandsMenu = new FiltrableCommandMenu(getProgressCommandMenuItemFactory(), 200, 264);
		commandsMenu.setOrderedItems(items);
		return commandsMenu;
	}

	private void showPopup(final Widget finalPopupWidget) {
		// Scheduled because of selection event steels focus if not
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				PopupConfig.configPopup().alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.RIGHT))
						.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.BOTTOM)).popup(finalPopupWidget).onClose(new PopupCloseListener() {
							@Override
							public void onHasClosed() {
								if (!skipScopeSelectionEventOnPopupClose) fireScopeSelectionEvent(); // Return focus to scopeTree;
								else skipScopeSelectionEventOnPopupClose = false;
							}
						}).pop();
			}
		});
	}

	public SimpleCommandMenuItem createItem(final String itemText, final String progressToDeclare) {
		return new SimpleCommandMenuItem(itemText, progressToDeclare, new Command() {

			@Override
			public void execute() {
				declareProgress(progressToDeclare);
			}
		});
	}

	private void declareProgress(final String progressDescription) {
		SERVICE_PROVIDER.actionExecution().onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), progressDescription));
	}

	private CustomCommandMenuItemFactory getProgressCommandMenuItemFactory() {
		return new CustomCommandMenuItemFactory() {

			@Override
			public String getNoItemText() {
				return null;
			}

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				return new SimpleCommandMenuItem(MESSAGES.markAs(inputText), inputText, new Command() {
					@Override
					public void execute() {
						declareProgress(inputText);
					}
				});
			}

			@Override
			public boolean shouldPrioritizeCustomItem() {
				return false;
			}
		};
	}

	@UiHandler("panel")
	public void onScopeWidgetClick(final ClickEvent e) {
		fireScopeSelectionEvent();
	}

	@UiHandler("panel")
	public void onScopeWidgetDoubleClick(final DoubleClickEvent e) {
		SERVICE_PROVIDER.details().showDetailsFor(scope.getId());
	}

	@UiHandler("panel")
	protected void onScopeWidgetMouseDown(final MouseDownEvent event) {
		panel.setStyleName("dragdrop-dragging", true);
	}

	@UiHandler("panel")
	protected void onScopeWidgetMouseOver(final MouseMoveEvent event) {
		panel.setStyleName("dragdrop-dragging", false);
	}

	private void fireScopeSelectionEvent() {
		SERVICE_PROVIDER.eventBus().fireEventFromSource(new ScopeSelectionEvent(scope, false), this);
	}

	@Override
	public void setTargetHighlight(final boolean shouldHighlight) {
		targetHighlight = shouldHighlight;
		panel.setStyleName(style.targetHighlight(), targetHighlight);
	}

	@Override
	public boolean isTargetHighlight() {
		return targetHighlight;
	}

	public void setAssociationHighlight(final boolean shouldHighlight) {
		associationHighlight = shouldHighlight;
		panel.setStyleName(style.associationHighlight(), associationHighlight);
	}

	public void setSelected(final boolean shouldSelect) {
		selected = shouldSelect;
		panel.setStyleName(style.selected(), selected);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setHasOpenImpediments(final boolean hasOpenImpediments) {
		update();
	}

	@Override
	public void addAssociatedUsers(final DraggableMemberWidget widget) {
		widget.setSizeSmall();
		associatedUsers.add(widget);
	}

	private ScopeAssociatedMembersWidget createAssociatedUsersListWidget(final Scope scope, final DragAndDropManager userDragAndDropMananger) {
		return new ScopeAssociatedMembersWidget(scope, userDragAndDropMananger, 2);
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServices.get().actionExecution().removeActionExecutionListener(this);
	}

	@Override
	public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
		final ModelAction action = execution.getModelAction();
		if (action instanceof ScopeAction && action.getReferenceId().equals(scope.getId())) update();
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return panel.addClickHandler(handler);
	}
}
