package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FastLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PercentualBar;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.BgColorAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.impediment.ImpedimentListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedTagsWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseScopeWidget extends Composite implements ScopeWidget, ModelWidget<Scope> {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private static ReleaseScopeWidgetUiBinder uiBinder = GWT.create(ReleaseScopeWidgetUiBinder.class);

	interface ReleaseScopeWidgetUiBinder extends UiBinder<Widget, ReleaseScopeWidget> {}

	interface ReleaseScopeWidgetStyle extends CssResource {
		String selected();

		String targetHighlight();

		String associationHighlight();

		String draggingMousePointer();

		String progressIconDone();

		String progressIconUnderwork();

		String progressIconNotStarted();

		String progressIconHasOpenImpediments();
	}

	@UiField
	ReleaseScopeWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	SpanElement humanIdLabel;

	@UiField
	SpanElement descriptionLabel;

	@UiField
	FocusPanel draggableAnchor;

	@UiField
	FocusPanel progressIcon;

	@UiField
	HorizontalPanel internalPanel;

	@UiField
	PercentualBar percentualBar;

	@UiField
	FastLabel effortLabel;

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

	public ReleaseScopeWidget(final Scope scope) {
		this(scope, false, null);
	}

	public ReleaseScopeWidget(final Scope scope, final boolean releaseSpecific, final DragAndDropManager userDragAndDropMananger) {
		associatedUsers = createAssociatedUsersListWidget(scope, userDragAndDropMananger);
		tags = new ScopeAssociatedTagsWidget(scope);
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		this.releaseSpecific = releaseSpecific;
		effortLabel.setVisible(releaseSpecific);

		update();
	}

	@Override
	public boolean update() {
		updateAssociatedUsers();
		updateTags();
		updateDueDate();
		return updateHumanId() | updateDescription() | updateProgress() | updateTitle() | updateValues();
	}

	private void updateDueDate() {
		final String color = SERVICE_PROVIDER.colorProvider().getDueDateColor(scope).toCssRepresentation();
		panel.getElement().getStyle().setBackgroundColor(color);
	}

	private void updateAssociatedUsers() {
		associatedUsers.setShouldShowDone(!scope.getProgress().isDone());
		associatedUsers.update();
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
		if (!releaseSpecific) return false;

		final float inferedEffort = scope.getEffort().getInfered();
		final String effortStr = ClientDecimalFormat.roundFloat(inferedEffort, 1);
		effortLabel.setText(effortStr);
		effortLabel.setTitle(effortStr + " effort points");
		percentualBar.setPercentual(calculatePercentual(scope));

		return true;
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
		final String humanId = ClientServices.getCurrentProjectContext().getHumanId(scope);
		humanIdLabel.setInnerHTML(humanId);
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

		descriptionLabel.setInnerHTML(currentScopeDescription);

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
		final Style s = ElementUtils.getStyle(draggableAnchor);

		if (!progress.isNotStarted()) s.setBackgroundColor(SERVICE_PROVIDER.colorProvider().getColorFor(scope).toCssRepresentation());
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
		items.add(new TextAndImageCommandMenuItem("icon-flag", messages.impediments(), new Command() {
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
				PopupConfig.configPopup()
						.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.RIGHT))
						.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.BOTTOM))
						.popup(finalPopupWidget)
						.onClose(new PopupCloseListener() {
							@Override
							public void onHasClosed() {
								if (!skipScopeSelectionEventOnPopupClose) fireScopeSelectionEvent(); // Return focus to scopeTree;
								else skipScopeSelectionEventOnPopupClose = false;
							}
						})
						.pop();
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
		SERVICE_PROVIDER.actionExecution()
				.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), progressDescription));
	}

	private CustomCommandMenuItemFactory getProgressCommandMenuItemFactory() {
		return new CustomCommandMenuItemFactory() {

			@Override
			public String getNoItemText() {
				return null;
			}

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				return new SimpleCommandMenuItem(messages.markAs(inputText), inputText, new Command() {
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
		SERVICE_PROVIDER.details().showAnnotationsFor(scope.getId());
	}

	@UiHandler("panel")
	protected void onScopeWidgetMouseDown(final MouseDownEvent event) {
		panel.setStyleName(style.draggingMousePointer(), true);
	}

	@UiHandler("panel")
	protected void onScopeWidgetUpDown(final MouseUpEvent event) {
		panel.setStyleName(style.draggingMousePointer(), false);
	}

	private void fireScopeSelectionEvent() {
		SERVICE_PROVIDER.eventBus().fireEventFromSource(new ScopeSelectionEvent(scope), this);
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

	private enum ProgressIconUpdater {
		NOT_STARTED_UPDATER(ProgressState.NOT_STARTED) {
			@Override
			String getStyle(final ReleaseScopeWidgetStyle style) {
				return style.progressIconNotStarted();
			}

			@Override
			String getTitle(final Scope scope) {
				final float accomplishedPercentual = scope.getEffort().getAccomplishedPercentual();
				return accomplishedPercentual != 0 ? messages.accomplished(ClientDecimalFormat.roundFloat(accomplishedPercentual, 0)) : "";
			}
		},
		UNDER_WORK_UPDATER(ProgressState.UNDER_WORK) {
			@Override
			String getStyle(final ReleaseScopeWidgetStyle style) {
				return style.progressIconUnderwork();
			}

			@Override
			String getTitle(final Scope scope) {
				return scope.getProgress().getDescription();
			}

			@Override
			public void animate(final HorizontalPanel panel) {
				new BgColorAnimation(panel, Color.GRAY).animate();
			}
		},
		DONE_UPDATER(ProgressState.DONE) {
			@Override
			String getStyle(final ReleaseScopeWidgetStyle style) {
				return style.progressIconDone();
			}

			@Override
			String getTitle(final Scope scope) {
				return messages.finishedIn(HumanDateFormatter.getRelativeDate(scope.getProgress().getEndDay().getJavaDate()));
			}

			@Override
			public void animate(final HorizontalPanel panel) {
				new BgColorAnimation(panel, Color.GREEN).animate();
			}
		},
		IMPEDIMENTS_UPDATER(null) {
			@Override
			String getStyle(final ReleaseScopeWidgetStyle style) {
				return "icon-flag " + style.progressIconHasOpenImpediments();
			}

			@Override
			String getTitle(final Scope scope) {
				return messages.hasOpenImpediments();
			}

			@Override
			public void animate(final HorizontalPanel panel) {
				new BgColorAnimation(panel, Color.RED).animate();
			}
		},
		NULL_UPDATER(null) {
			@Override
			String getStyle(final ReleaseScopeWidgetStyle style) {
				return "";
			}

			@Override
			String getTitle(final Scope scope) {
				return "";
			}
		};

		final ProgressState state;

		ProgressIconUpdater(final ProgressState state) {
			this.state = state;
		}

		public void animate(final HorizontalPanel panel) {}

		public static ProgressIconUpdater getUpdater(final Scope scope, final boolean hasImpediments) {
			if (hasImpediments) return IMPEDIMENTS_UPDATER;

			for (final ProgressIconUpdater updater : values())
				if (updater.state == scope.getProgress().getState()) return updater;
			return NULL_UPDATER;
		}

		abstract String getStyle(ReleaseScopeWidgetStyle style);

		abstract String getTitle(Scope scope);
	}

}
