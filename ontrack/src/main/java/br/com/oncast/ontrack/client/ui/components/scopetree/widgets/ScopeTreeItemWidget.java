package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.CENTER;
import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.LEFT;
import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.RIGHT;
import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment.BOTTOM;
import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment.TOP;
import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.user.Selection;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.SubjectDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetEffortCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetProgressCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetReleaseCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetTagCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetValueCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FastLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ReleaseTag;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.impediment.ImpedimentListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.TagAssociationWidget;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn.VisibilityChangeListener;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.PrioritizationCriteria;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.common.base.Joiner;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTreeItemWidget extends Composite {

	interface ScopeTreeItemWidgetUiBinder extends UiBinder<Widget, ScopeTreeItemWidget> {}

	private static ScopeTreeItemWidgetMessages messages = GWT.create(ScopeTreeItemWidgetMessages.class);

	private static ScopeTreeItemWidgetUiBinder uiBinder = GWT.create(ScopeTreeItemWidgetUiBinder.class);

	interface Style extends CssResource {
		String done();

		String amountInfered();

		String amountConflicted();

		String checklistComplete();
	}

	@UiField
	@IgnoredByDeepEquality
	protected Style style;

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel deckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel openImpedimentIcon;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel annotationIcon;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel checklistIcon;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel descriptionIcon;

	@UiField
	@IgnoredByDeepEquality
	protected Label descriptionLabel;

	@UiField
	@IgnoredByDeepEquality
	protected HTMLPanel valuePanel;

	@UiField
	@IgnoredByDeepEquality
	protected SpanElement valueLabel;

	@UiField
	@IgnoredByDeepEquality
	protected HTMLPanel effortPanel;

	@UiField
	@IgnoredByDeepEquality
	protected SpanElement effortLabel;

	@UiField
	@IgnoredByDeepEquality
	protected FastLabel progressLabel;

	@UiField
	@IgnoredByDeepEquality
	protected TextBox editionBox;

	@UiField
	@IgnoredByDeepEquality
	protected HTMLPanel releasePanel;

	@UiField
	@IgnoredByDeepEquality
	protected ReleaseTag releaseTag;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel focusPanel;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel borderPanel;

	@UiField
	@IgnoredByDeepEquality
	protected Label selectedMembers;

	@UiField(provided = true)
	@IgnoredByDeepEquality
	protected ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget> tags;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetEditionHandler editionHandler;

	private Scope scope;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetReleaseCommandMenuItemFactory releaseCommandMenuItemFactory;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetEffortCommandMenuItemFactory effortCommandMenuItemFactory;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetProgressCommandMenuItemFactory progressCommandMenuItemFactory;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetValueCommandMenuItemFactory valueCommandMenuItemFactory;

	@IgnoredByDeepEquality
	private final ScopeTreeItemWidgetTagCommandMenuItemFactory tagCommandMenuItemFactory;

	@IgnoredByDeepEquality
	private final List<Selection> selectionsList;

	@IgnoredByDeepEquality
	private final Animation fadeAnimation;

	@IgnoredByDeepEquality
	private final Timer fadeAnimationTimer;

	@IgnoredByDeepEquality
	private final IPadFocusWorkaround ipadFocusWorkaround;

	public ScopeTreeItemWidget(final Scope scope, final ScopeTreeItemWidgetEditionHandler editionHandler) {
		tags = createTagsContainer();
		initWidget(uiBinder.createAndBindUi(this));

		selectionsList = ClientServices.get().colorProvider().getMembersSelectionsFor(scope);

		fadeAnimation = new Animation() {
			@Override
			protected void onUpdate(final double progress) {
				selectedMembers.getElement().getStyle().setOpacity(1 - progress);
			}
		};

		fadeAnimationTimer = new Timer() {
			@Override
			public void run() {
				fadeAnimation.run(1000);
			}
		};

		this.editionHandler = editionHandler;
		this.releaseCommandMenuItemFactory = new ScopeTreeItemWidgetReleaseCommandMenuItemFactory(editionHandler);
		this.effortCommandMenuItemFactory = new ScopeTreeItemWidgetEffortCommandMenuItemFactory(editionHandler);
		this.valueCommandMenuItemFactory = new ScopeTreeItemWidgetValueCommandMenuItemFactory(editionHandler);
		this.tagCommandMenuItemFactory = new ScopeTreeItemWidgetTagCommandMenuItemFactory(editionHandler);
		this.progressCommandMenuItemFactory = new ScopeTreeItemWidgetProgressCommandMenuItemFactory(editionHandler);
		this.ipadFocusWorkaround = new IPadFocusWorkaround(editionBox);

		setScope(scope);

		focusPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (isEditing()) editionHandler.onDeselectTreeItemRequest();
			}
		});

		focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				if (!isEditing()) editionHandler.onEditionStart();
			}
		});

		editionBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				if (ipadFocusWorkaround.shouldNotAllowBlur()) {
					editionBox.setFocus(true);
					event.preventDefault();
				}
				else switchToVisualization(true);
			}
		});

		registerColumnVisibilityChangeListeners();

		updateDetails(ClientServices.get().details().getDetailUpdateEvent(scope.getId()));

		deckPanel.showWidget(0);

		updateSelection();
		showSelectedMembersLabel();
	}

	private ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget> createTagsContainer() {
		return new ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget>(new ModelWidgetFactory<TagAssociationMetadata, TagAssociationWidget>() {
			@Override
			public TagAssociationWidget createWidget(final TagAssociationMetadata modelBean) {
				return new TagAssociationWidget(modelBean, editionHandler);
			}
		}, new AnimatedContainer(new HorizontalPanel()));
	}

	@UiHandler("editionBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!isEditing()) return;

		event.stopPropagation();

		final boolean isEnter = event.getNativeKeyCode() == KEY_ENTER;
		if (isEnter || event.getNativeKeyCode() == KEY_ESCAPE) {
			event.preventDefault();
			if (!isEnter || !editionBox.getText().trim().isEmpty()) switchToVisualization(isEnter);
		}
	}

	@UiHandler("borderPanel")
	protected void onMouseOver(final MouseMoveEvent event) {
		if (!selectionsList.isEmpty()) showSelectedMembersLabel();
	}

	@UiHandler("editionBox")
	protected void onKeyUp(final KeyUpEvent event) {
		if (!isEditing()) return;
		event.stopPropagation();
	}

	@UiHandler("releaseTag")
	protected void onTagClick(final ClickEvent e) {
		e.stopPropagation();
		ClientServices.get().eventBus().fireEventFromSource(new ScopeSelectionEvent(scope), releaseTag);
	}

	@UiHandler("openImpedimentIcon")
	protected void openImpediments(final ClickEvent e) {
		e.stopPropagation();
		showImpedimentMenu();
	}

	@UiHandler({ "annotationIcon", "checklistIcon", "descriptionIcon" })
	protected void openDetails(final ClickEvent e) {
		e.stopPropagation();
		ClientServices.get().details().showDetailsFor(scope.getId());
	}

	public void setValue(final String value) {
		descriptionLabel.setText(value);
		descriptionLabel.setTitle(value);
		editionBox.setText(value);
	}

	public String getValue() {
		return new ScopeRepresentationBuilder(scope).includeEverything().toString();
	}

	private String getSimpleDescription() {
		return new ScopeRepresentationBuilder(scope).includeScopeDescription().toString();
	}

	public void switchToEditionMode() {
		if (isEditing()) return;

		editionBox.setText(getSimpleDescription());
		deckPanel.showWidget(1);
		if (!ipadFocusWorkaround.focus()) {
			new Timer() {
				@Override
				public void run() {
					editionBox.selectAll();
					editionBox.setFocus(true);
				}
			}.schedule(100);
		}
	}

	public void switchToVisualization(final boolean shouldTryToUpdateChanges) {
		if (!isEditing()) return;
		deckPanel.showWidget(0);

		if (!shouldTryToUpdateChanges) {
			editionBox.setText(descriptionLabel.getText());
			editionHandler.onEditionCancel();
		}
		else {
			if (!getSimpleDescription().equals(editionBox.getText())) editionHandler
					.onEditionEnd(editionBox.getText());
			else editionHandler.onEditionCancel();
		}
	}

	public boolean isEditing() {
		return deckPanel.getVisibleWidget() == 1;
	}

	public void setScope(final Scope scope) {
		this.scope = scope;
		descriptionLabel.setText(scope.getDescription());
		descriptionLabel.setTitle(scope.getDescription());
		editionBox.setText(scope.getDescription());
		updateDisplay();
	}

	public Scope getScope() {
		return scope;
	}

	public void updateDisplay() {
		updateProgressDisplay();
		updateEffortDisplay();
		updateValueDisplay();
		updateReleaseDisplay();
		updateTagsDisplay();
	}

	public void updateTagsDisplay() {
		tags.update(ClientServices.getCurrentProjectContext().<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType()));
	}

	public void updateDetails(final SubjectDetailUpdateEvent event) {
		if (event == null) return;
		this.annotationIcon.setVisible(event.hasAnnotations());
		this.annotationIcon.setTitle(messages.annotationsIconTitle("" + event.getAnnotationsCount()));
		this.descriptionIcon.setVisible(event.hasDescription());
		this.descriptionIcon.setTitle(removeHtmlTags(event.getDescriptionText()));

		updateChecklistIndicator(event);

		this.openImpedimentIcon.setVisible(event.hasOpenImpediments());
		final int count = event.getOpenImpedimentsCount();
		this.openImpedimentIcon.setTitle(count == 1 ? event.getOpenImpediments().get(0).getMessage() : messages.openImpediments("" + count));
	}

	private String removeHtmlTags(String html) {
		html = html.replaceAll("(<([^>]+)>)", "");
		return html;
	}

	private void updateChecklistIndicator(final SubjectDetailUpdateEvent event) {
		this.checklistIcon.setVisible(event.hasChecklists());

		final boolean isComplete = event.isChecklistComplete();
		this.checklistIcon.setStyleName(style.checklistComplete(), isComplete);
		this.checklistIcon.setTitle(messages.checklistCompletition("" + event.getCheckedItemCount(), "" + event.getTotalChecklistItemCount()));
	}

	private void updateValueDisplay() {
		updatePrioritizationCriteriaDisplay(scope.getValue(), valuePanel, valueLabel, "vp");
	}

	private void updateEffortDisplay() {
		updatePrioritizationCriteriaDisplay(scope.getEffort(), effortPanel, effortLabel, "ep");
	}

	private void updatePrioritizationCriteriaDisplay(final PrioritizationCriteria criteria, final HTMLPanel panel, final SpanElement label, final String unit) {
		final float declared = criteria.getDeclared();
		final float infered = criteria.getInfered();

		final boolean inferedDefined = declared != infered;
		final boolean hasDeclared = criteria.hasDeclared();
		final boolean hasDifference = inferedDefined && hasDeclared;

		panel.setStyleName(style.amountConflicted(), hasDifference);
		panel.setStyleName(style.amountInfered(), inferedDefined);

		label.setTitle(hasDifference ? messages.conflicted(format(declared, unit)) : "");
		label.setInnerText(hasDeclared || infered > 0 ? format(infered, unit) : "");
	}

	private String format(final float number, final String unit) {
		return ClientDecimalFormat.roundFloat(number, 1) + unit;
	}

	public void updateReleaseDisplay() {
		// TODO+++ Consider using FastLabel and other fast components to increase cache encapsulation.
		final Release release = scope.getRelease();

		final boolean isReleasePresent = (release != null);
		releaseTag.setVisible(isReleasePresent);
		releaseTag.setText(isReleasePresent ? release.getDescription() : "");
		releaseTag.setTitle(isReleasePresent ? release.getFullDescription().replaceAll("/", " > ") : "");
	}

	private void updateProgressDisplay() {
		final String progress = scope.isLeaf() || scope.getProgress().hasDeclared() ? scope.getProgress().getDescription() : getPercentageProgress();

		progressLabel.setText(progress);
		progressLabel.setTitle(progress);

		focusPanel.setStyleName(style.done(), scope.getProgress().isDone());
	}

	private String getPercentageProgress() {
		if (scope.getProgress().isDone()) return "100%";
		if (scope.getEffort().getInfered() == 0 || scope.getEffort().getAccomplishedPercentual() == 0) return "";
		return ClientDecimalFormat.roundFloat(scope.getEffort().getAccomplishedPercentual(), 1) + "%";
	}

	public void showReleaseMenu(final List<Release> releaseList) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(releaseCommandMenuItemFactory.createItem("None", ""));

		CommandMenuItem scopeReleaseItem = null;
		Release release = scope.getRelease();

		if (release == null) release = getCurrentRelease();

		for (final Release releaseItem : releaseList) {
			final SimpleCommandMenuItem item = releaseCommandMenuItemFactory.createItem(releaseItem.getFullDescription(),
					releaseItem.getFullDescription());

			if (release.equals(releaseItem)) scopeReleaseItem = item;
			items.add(item);
		}

		final CommandMenuItem scopeReleaseItemFinal = scopeReleaseItem;
		final FiltrableCommandMenu commandsMenu = createCommandMenu(releaseCommandMenuItemFactory, 350, 264);

		commandsMenu.setOrderedItems(items);

		commandsMenu.addCloseHandler(createCloseHandler());

		align(configPopup(), releaseTag)
				.popup(commandsMenu)
				.pop();

		if (scopeReleaseItem != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					commandsMenu.setSelected(scopeReleaseItemFinal);
				}
			});
		}

	}

	private Release getProjectRelease() {
		return ClientServices.getCurrentProjectContext().getProjectRelease();
	}

	private Release getCurrentRelease() {
		for (final Release r : getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (r.isDone()) continue;

			for (final Scope s : r.getScopeList()) {
				if (s.getProgress().isUnderWork()) return r;
			}
		}
		return getFirstNotCompleteRelease();
	}

	private Release getFirstNotCompleteRelease() {
		for (final Release r : getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (!r.isDone()) return r;
		}
		return getProjectRelease();
	}

	private CloseHandler<FiltrableCommandMenu> createCloseHandler() {
		return new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				focusPanel.setFocus(true);
			}
		};
	}

	public void showProgressMenu(final List<String> list) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		final String notStartedDescription = ProgressState.NOT_STARTED.getDescription();
		items.add(progressCommandMenuItemFactory.createItem("Not Started", notStartedDescription));
		for (final String progressDefinition : list)
			if (!notStartedDescription.equals(progressDefinition)) items.add(progressCommandMenuItemFactory.createItem(progressDefinition, progressDefinition));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, progressCommandMenuItemFactory, 200, 264);

		commandsMenu.addCloseHandler(createCloseHandler());

		align(configPopup(), progressLabel)
				.popup(commandsMenu)
				.pop();
	}

	public void showEffortMenu(final List<String> fibonacciScaleForEffort) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(effortCommandMenuItemFactory.createItem("None", ""));
		for (final String effort : fibonacciScaleForEffort)
			items.add(effortCommandMenuItemFactory.createItem(effort, effort));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, effortCommandMenuItemFactory, 100, 264);

		commandsMenu.addCloseHandler(createCloseHandler());

		commandsMenu.setHelpText("");
		align(configPopup(), effortPanel)
				.popup(commandsMenu)
				.pop();
	}

	public void showValueMenu(final List<String> fibonacciScaleForValue) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(valueCommandMenuItemFactory.createItem("None", ""));
		for (final String value : fibonacciScaleForValue)
			items.add(valueCommandMenuItemFactory.createItem(value, value));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, valueCommandMenuItemFactory, 100, 264);

		commandsMenu.addCloseHandler(createCloseHandler());

		commandsMenu.setHelpText("");
		align(configPopup(), valuePanel)
				.popup(commandsMenu)
				.pop();
	}

	public void showTagMenu(final List<Tag> tags) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		for (final Tag tag : tags) {
			items.add(tagCommandMenuItemFactory.createItem(tag.getDescription(), tag.getDescription()));
		}

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, tagCommandMenuItemFactory, 300, 264);

		commandsMenu.addCloseHandler(createCloseHandler());

		commandsMenu.setHelpText("");
		align(configPopup(), valuePanel)
				.popup(commandsMenu)
				.pop();
	}

	public void showImpedimentMenu() {
		configPopup()
				.popup(new ImpedimentListWidget(scope))
				.alignHorizontal(LEFT, new AlignmentReference(this, LEFT, -1))
				.alignVertical(TOP, new AlignmentReference(this, BOTTOM, 11))
				.onClose(new PopupCloseListener() {
					@Override
					public void onHasClosed() {
						focusPanel.setFocus(true);
					}
				})
				.pop();
	}

	private PopupConfig align(final PopupConfig config, final Widget widget) {
		config.alignVertical(TOP, new AlignmentReference(this, BOTTOM, 5));

		if (widget.isVisible()) return config.alignHorizontal(CENTER, new AlignmentReference(widget, CENTER));
		else return config.alignHorizontal(RIGHT, new AlignmentReference(descriptionLabel, RIGHT));
	}

	private FiltrableCommandMenu createCommandMenu(final CustomCommandMenuItemFactory customItemFactory, final int maxWidth, final int maxHeight) {
		final FiltrableCommandMenu menu = new FiltrableCommandMenu(customItemFactory, maxWidth, maxHeight);
		menu.addCloseHandler(new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				editionHandler.onEditionMenuClose();
			}
		});
		return menu;
	}

	private FiltrableCommandMenu createCommandMenu(final List<CommandMenuItem> itens, final CustomCommandMenuItemFactory customItemFactory,
			final int maxWidth, final int maxHeight) {
		final FiltrableCommandMenu menu = new FiltrableCommandMenu(customItemFactory, maxWidth, maxHeight);
		menu.addCloseHandler(new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				editionHandler.onEditionMenuClose();
			}
		});
		menu.setOrderedItems(itens);
		return menu;
	}

	private void registerColumnVisibilityChangeListeners() {
		ScopeTreeColumn.RELEASE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				releasePanel.setVisible(isVisible);
			}
		});

		ScopeTreeColumn.PROGRESS.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				progressLabel.setVisible(isVisible);
			}
		});
		ScopeTreeColumn.EFFORT.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				effortPanel.setVisible(isVisible);
			}
		});
		ScopeTreeColumn.VALUE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				valuePanel.setVisible(isVisible);
			}
		});
	}

	public void addSelectedMember(final UserRepresentation member, final Color selectionColor) {
		selectionsList.add(new Selection(member, selectionColor));

		updateSelection();
		showSelectedMembersLabel();
	}

	private void showSelectedMembersLabel() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				fadeAnimation.cancel();
				fadeAnimationTimer.cancel();
				selectedMembers.getElement().getStyle().setOpacity(1);
				fadeAnimationTimer.schedule(5000);
			}
		});
	}

	public void removeSelectedMember(final UserRepresentation member) {
		selectionsList.remove(new Selection(member));
		updateSelection();
	}

	private void updateSelection() {
		Color selectionColor = Color.TRANSPARENT;
		String membersText = "";

		if (!selectionsList.isEmpty()) {
			selectionColor = selectionsList.get(0).getColor();
			final ArrayList<String> selectionNames = new ArrayList<String>();
			for (final Selection s : selectionsList) {
				final User user = ClientServices.get().userData().getRealUser(s.getUser());
				final String name = user.getName();
				if (!selectionNames.contains(name)) selectionNames.add(name);
			}
			membersText = Joiner.on(", ").join(selectionNames);
		}

		borderPanel.getElement().getStyle().setBorderColor(selectionColor.toCssRepresentation());
		selectedMembers.getElement().getStyle().setBackgroundColor(selectionColor.toCssRepresentation());
		selectedMembers.setText(membersText);
	}

}
