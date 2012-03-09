package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetEffortCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetProgressCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetReleaseCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetValueCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FastLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.Tag;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.VisibilityOf;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.VisibilityOf.VisibilityChangeListener;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;
import br.com.oncast.ontrack.shared.model.value.Value;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTreeItemWidget extends Composite {

	interface ScopeTreeItemWidgetUiBinder extends UiBinder<Widget, ScopeTreeItemWidget> {}

	private static ScopeTreeItemWidgetUiBinder uiBinder = GWT.create(ScopeTreeItemWidgetUiBinder.class);

	interface Style extends CssResource {
		String labelStriped();

		String done();
	}

	@UiField
	@IgnoredByDeepEquality
	protected Style style;

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel deckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected Label descriptionLabel;

	@UiField
	@IgnoredByDeepEquality
	protected HTMLPanel valuePanel;

	@UiField
	@IgnoredByDeepEquality
	protected FastLabel inferedValueLabel;

	@UiField
	@IgnoredByDeepEquality
	protected FastLabel declaredValueLabel;

	@UiField
	@IgnoredByDeepEquality
	protected HTMLPanel effortPanel;

	@UiField
	@IgnoredByDeepEquality
	protected FastLabel inferedEffortLabel;

	@UiField
	@IgnoredByDeepEquality
	protected FastLabel declaredEffortLabel;

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
	protected Tag releaseTag;

	@UiField
	@IgnoredByDeepEquality
	protected FocusPanel focusPanel;

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

	public ScopeTreeItemWidget(final Scope scope, final ScopeTreeItemWidgetEditionHandler editionHandler) {

		initWidget(uiBinder.createAndBindUi(this));
		setScope(scope);

		this.editionHandler = editionHandler;
		this.releaseCommandMenuItemFactory = new ScopeTreeItemWidgetReleaseCommandMenuItemFactory(editionHandler);
		this.effortCommandMenuItemFactory = new ScopeTreeItemWidgetEffortCommandMenuItemFactory(editionHandler);
		this.valueCommandMenuItemFactory = new ScopeTreeItemWidgetValueCommandMenuItemFactory(editionHandler);
		this.progressCommandMenuItemFactory = new ScopeTreeItemWidgetProgressCommandMenuItemFactory(editionHandler);

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
				switchToVisualization(true);
			}
		});

		releaseTag.setCloseButtonClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				editionHandler.onEditionEnd(new ScopeRepresentationBuilder(scope).includeEverything().excludeReleaseReference().toString());
			}
		});

		registerCustomItensChangeListeners();

		deckPanel.showWidget(0);
	}

	@UiHandler("editionBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!isEditing()) return;

		event.stopPropagation();

		final boolean isEnter = event.getNativeKeyCode() == KEY_ENTER;
		if (isEnter || event.getNativeKeyCode() == KEY_ESCAPE) {
			event.preventDefault();
			switchToVisualization(isEnter);
		}
	}

	@UiHandler("editionBox")
	protected void onKeyUp(final KeyUpEvent event) {
		if (!isEditing()) return;
		event.preventDefault();
		event.stopPropagation();
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
		new Timer() {
			@Override
			public void run() {
				editionBox.selectAll();
				editionBox.setFocus(true);
			}
		}.schedule(100);
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
					.onEditionEnd(completeDescription(editionBox
							.getText()));
			else editionHandler.onEditionCancel();
		}
	}

	private String completeDescription(final String text) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(text);
		final ScopeRepresentationBuilder builder = new ScopeRepresentationBuilder(scope);
		final StringBuffer buffer = new StringBuffer(text);
		if (!parser.hasDeclaredEffort()) builder.includeEffort();
		if (!parser.hasDeclaredValue()) builder.includeValue();
		if (parser.getReleaseDescription().isEmpty()) builder.includeReleaseReference();
		if (parser.getProgressDescription() == null) builder.includeProgress();
		return buffer.append(builder.toString()).toString();
	}

	private boolean isEditing() {
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
	}

	private void updateValueDisplay() {
		final Value value = scope.getValue();

		final float declared = value.getDeclared();
		final float infered = value.getInfered();

		final boolean inferedDefined = declared != infered;
		final boolean declaredLabelDefined = value.hasDeclared();
		final boolean hasDifference = inferedDefined && declaredLabelDefined;

		if (hasDifference) declaredValueLabel.addStyleName(style.labelStriped());
		else declaredValueLabel.removeStyleName(style.labelStriped());

		declaredValueLabel.setText(declaredLabelDefined ? ClientDecimalFormat.roundFloat(declared, 1) + "vp" : "");
		inferedValueLabel.setText(inferedDefined ? ClientDecimalFormat.roundFloat(infered, 1) + "vp" : "");
	}

	private void updateEffortDisplay() {
		final Effort effort = scope.getEffort();

		final float declaredEffort = effort.getDeclared();
		final float inferedEffort = effort.getInfered();

		final boolean inferedEffortDefined = declaredEffort != inferedEffort;
		final boolean declaredEffortLabelDefined = effort.hasDeclared();
		final boolean hasEffortDifference = inferedEffortDefined && declaredEffortLabelDefined;

		if (hasEffortDifference) declaredEffortLabel.addStyleName(style.labelStriped());
		else declaredEffortLabel.removeStyleName(style.labelStriped());

		declaredEffortLabel.setText(declaredEffortLabelDefined ? ClientDecimalFormat.roundFloat(declaredEffort, 1) + "ep" : "");
		inferedEffortLabel.setText(inferedEffortDefined ? ClientDecimalFormat.roundFloat(inferedEffort, 1) + "ep" : "");
	}

	public void updateReleaseDisplay() {
		// TODO+++ Consider using FastLabel and other fast components to increase cache encapsulation.
		final Release release = scope.getRelease();

		final boolean isReleasePresent = (release != null);
		releaseTag.setVisible(isReleasePresent);
		releaseTag.setText(isReleasePresent ? release.getFullDescription() : "");
	}

	/*
	 * Decisions: - [02/08/2011] It was decided to display a percentage result even if some child scope not been estimated (effort = 0) and it is not done.
	 */
	private void updateProgressDisplay() {
		final String progress = scope.getProgress().getDescription();

		progressLabel.setText(progress);
		progressLabel.setTitle(progress);

		focusPanel.setStyleName(style.done(), scope.getProgress().isDone());
	}

	public void showReleaseMenu(final List<Release> releaseList) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(releaseCommandMenuItemFactory.createItem("None", ""));
		for (final Release releaseItem : releaseList)
			items.add(releaseCommandMenuItemFactory.createItem(releaseItem.getFullDescription(), releaseItem.getFullDescription()));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, releaseCommandMenuItemFactory, 670, 300);

		configPopup().alignBelow(descriptionLabel).alignRight(releasePanel).popup(commandsMenu).pop();
	}

	public void showProgressMenu(final List<String> list) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		final String notStartedDescription = ProgressState.NOT_STARTED.getDescription();
		items.add(progressCommandMenuItemFactory.createItem("Not Started", notStartedDescription));
		for (final String progressDefinition : list)
			if (!notStartedDescription.equals(progressDefinition)) items.add(progressCommandMenuItemFactory.createItem(progressDefinition, progressDefinition));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, progressCommandMenuItemFactory, 400, 300);

		configPopup().alignBelow(descriptionLabel).alignRight(progressLabel).popup(commandsMenu).pop();
	}

	public void showEffortMenu(final List<String> fibonacciScaleForEffort) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(effortCommandMenuItemFactory.createItem("None", ""));
		for (final String effort : fibonacciScaleForEffort)
			items.add(effortCommandMenuItemFactory.createItem(effort, effort));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, effortCommandMenuItemFactory, 200, 300);
		configPopup().alignBelow(descriptionLabel).alignRight(effortPanel).popup(commandsMenu).pop();
	}

	public void showValueMenu(final List<String> fibonacciScaleForValue) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(valueCommandMenuItemFactory.createItem("None", ""));
		for (final String value : fibonacciScaleForValue)
			items.add(valueCommandMenuItemFactory.createItem(value, value));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, valueCommandMenuItemFactory, 200, 300);
		configPopup().alignBelow(descriptionLabel).alignRight(valuePanel).popup(commandsMenu).pop();
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

		menu.setOrderedItens(itens);
		return menu;
	}

	private void registerCustomItensChangeListeners() {
		VisibilityOf.RELEASE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				releasePanel.setVisible(isVisible);
			}
		});

		VisibilityOf.PROGRESS.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				progressLabel.setVisible(isVisible);
			}
		});
		VisibilityOf.EFFORT.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				effortPanel.setVisible(isVisible);
			}
		});
		VisibilityOf.VALUE.register(new VisibilityChangeListener() {
			@Override
			public void onVisiblityChange(final boolean isVisible) {
				valuePanel.setVisible(isVisible);
			}
		});
	}
}
