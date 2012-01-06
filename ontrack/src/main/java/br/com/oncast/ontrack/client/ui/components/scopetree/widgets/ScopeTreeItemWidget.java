package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetEffortCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetProgressCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.ScopeTreeItemWidgetReleaseCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FastLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.Tag;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationParser;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
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

	interface EditableLabelUiBinder extends UiBinder<Widget, ScopeTreeItemWidget> {}

	private static EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

	interface Style extends CssResource {
		String effortLabelTranslucid();

		String effortLabelStriped();

		String releaseMenuPanel();

		String progressMenuPanel();

		String effortMenuPanel();
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

	public ScopeTreeItemWidget(final Scope scope, final ScopeTreeItemWidgetEditionHandler editionHandler) {

		initWidget(uiBinder.createAndBindUi(this));
		releaseTag.setVisible(false);
		setScope(scope);

		this.editionHandler = editionHandler;
		this.releaseCommandMenuItemFactory = new ScopeTreeItemWidgetReleaseCommandMenuItemFactory(editionHandler);
		this.effortCommandMenuItemFactory = new ScopeTreeItemWidgetEffortCommandMenuItemFactory(editionHandler);
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
				editionHandler.onEditionStart();
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

		deckPanel.showWidget(0);
	}

	@UiHandler("editionBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!isEditing()) return;

		final boolean isEnter = event.getNativeKeyCode() == KEY_ENTER;
		if (isEnter || event.getNativeKeyCode() == KEY_ESCAPE) {

			event.preventDefault();
			event.stopPropagation();
			switchToVisualization(isEnter);
		}
		else return;

	}

	public String getValue() {
		return new ScopeRepresentationBuilder(scope).includeEverything().toString();
	}

	private String getSimpleDescription() {
		return new ScopeRepresentationBuilder(scope).includeScopeDescription().toString();
	}

	public void setValue(final String value) {
		descriptionLabel.setText(value);
		descriptionLabel.setTitle(value);
		editionBox.setText(value);
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
			if (!getValue().equals(editionBox.getText()) || editionBox.getText().isEmpty()) editionHandler.onEditionEnd(completeDescription(editionBox
					.getText()));
			else editionHandler.onEditionCancel();
		}
	}

	private String completeDescription(final String text) {
		final ScopeRepresentationParser parser = new ScopeRepresentationParser(text);
		final ScopeRepresentationBuilder builder = new ScopeRepresentationBuilder(scope);
		final StringBuffer buffer = new StringBuffer(text);
		if (!parser.hasDeclaredEffort()) buffer.append(builder.includeEffort().toString());
		if (parser.getReleaseDescription().isEmpty()) buffer.append(builder.includeReleaseReference().toString());
		if (parser.getProgressDescription() == null) buffer.append(builder.includeProgress().toString());
		return buffer.toString();
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
		updateReleaseDisplay();
	}

	private void updateEffortDisplay() {
		final Effort effort = scope.getEffort();

		final float declaredEffort = effort.getDeclared();
		final float inferedEffort = effort.getInfered();

		final boolean inferedEffortDefined = declaredEffort != inferedEffort;
		final boolean declaredEffortLabelDefined = effort.hasDeclared();
		final boolean hasEffortDifference = inferedEffortDefined && declaredEffortLabelDefined;

		if (hasEffortDifference) declaredEffortLabel.addStyleName(style.effortLabelStriped());
		else declaredEffortLabel.removeStyleName(style.effortLabelStriped());

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
		final String progress = scope.isLeaf() ? getProgressDescriptionForLeaf() : getProgressDescriptionForNonLeaf();
		progressLabel.setText(progress);
		progressLabel.setTitle(progress);
	}

	private String getProgressDescriptionForLeaf() {
		return scope.getProgress().getDescription();
	}

	private String getProgressDescriptionForNonLeaf() {
		if (scope.getProgress().isDone()) return "100%";
		if (scope.getEffort().getInfered() == 0) return "";
		return ClientDecimalFormat.roundFloat(scope.getEffort().getAccomplishedPercentual(), 1) + "%";
	}

	public void showReleaseMenu(final List<Release> releaseList) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(releaseCommandMenuItemFactory.createItem("None", ""));
		for (final Release releaseItem : releaseList)
			items.add(releaseCommandMenuItemFactory.createItem(releaseItem.getFullDescription(), releaseItem.getFullDescription()));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, releaseCommandMenuItemFactory, 670, 300);

		configPopup().alignBelow(descriptionLabel).alignRight(releasePanel).popup(commandsMenu).pop();
	}

	public void showProgressMenu(final Set<String> progressDefinitionSet) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		final String notStartedDescription = ProgressState.NOT_STARTED.getDescription();
		items.add(progressCommandMenuItemFactory.createItem("Not Started", notStartedDescription));
		for (final String progressDefinition : progressDefinitionSet)
			if (!notStartedDescription.equals(progressDefinition)) items.add(progressCommandMenuItemFactory.createItem(progressDefinition, progressDefinition));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, progressCommandMenuItemFactory, 400, 300);

		configPopup().alignBelow(descriptionLabel).alignRight(progressLabel).popup(commandsMenu).pop();
	}

	public void showEffortMenu(final List<String> fibonacciScaleForEffort) {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

		items.add(effortCommandMenuItemFactory.createItem("None", ""));
		for (final String effort : fibonacciScaleForEffort)
			items.add(effortCommandMenuItemFactory.createItem(effort, effort));

		final FiltrableCommandMenu commandsMenu = createCommandMenu(items, effortCommandMenuItemFactory, 100, 300);
		configPopup().alignBelow(descriptionLabel).alignRight(effortPanel).popup(commandsMenu).pop();
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

}
