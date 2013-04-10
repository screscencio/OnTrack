package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedTagsWidget;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanScopeWidget extends Composite implements ScopeWidget, ModelWidget<Scope> {

	private static KanbanScopeWidgetUiBinder uiBinder = GWT.create(KanbanScopeWidgetUiBinder.class);

	interface KanbanScopeWidgetUiBinder extends UiBinder<Widget, KanbanScopeWidget> {}

	interface KanbanScopeWidgetStyle extends CssResource {
		String selected();

		String targetHighlight();

		String descriptionLabelWithAssociatedUsers();

		String associationHighlight();

		String draggingMousePointer();
	}

	@UiField
	KanbanScopeWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	// TODO use FastLabel
	Label descriptionLabel;

	@UiField
	FocusPanel draggableAnchor;

	@UiField(provided = true)
	ScopeAssociatedTagsWidget tags;

	@UiField(provided = true)
	ScopeAssociatedMembersWidget associatedUsers;

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	private boolean selected = false;
	private boolean targetHighlight = false;
	private boolean associationHighlight = false;

	// IMPORTANT Used to refresh DOM only when needed.
	public KanbanScopeWidget(final Scope scope, final ProgressPanelWidgetInteractionHandler progressPanelInteractionHandler,
			final DragAndDropManager userDragAndDropMananger) {
		associatedUsers = new ScopeAssociatedMembersWidget(scope, userDragAndDropMananger);
		tags = new ScopeAssociatedTagsWidget(scope);

		initWidget(uiBinder.createAndBindUi(this));

		final Scope story = findStory(scope);
		draggableAnchor.getElement().getStyle()
				.setBackgroundColor(ClientServices.get().colorProvider().getColorFor(story).toCssRepresentation());
		this.scope = scope;
		updateDescription();
	}

	private Scope findStory(final Scope scope) {
		Release release = scope.getRelease();
		Scope currentScope = scope;

		while (scope.isLeaf() && release == null && !currentScope.isRoot()) {
			currentScope = currentScope.getParent();
			release = currentScope.getRelease();
		}
		return currentScope;
	}

	@UiHandler("panel")
	public void onScopeWidgetDoubleClick(final DoubleClickEvent e) {
		ClientServices.get().details().showAnnotationsFor(scope.getId());
	}

	@UiHandler("panel")
	public void onScopeWidgetClick(final ClickEvent e) {
		ClientServices.get().eventBus().fireEventFromSource(new ScopeSelectionEvent(scope), this);
	}

	@UiHandler("panel")
	protected void onScopeWidgetMouseDown(final MouseDownEvent event) {
		panel.setStyleName(style.draggingMousePointer(), true);
	}

	@UiHandler("panel")
	protected void onScopeWidgetUpDown(final MouseUpEvent event) {
		panel.setStyleName(style.draggingMousePointer(), false);
	}

	@Override
	public boolean update() {
		associatedUsers.setShouldShowDone(!scope.getProgress().isDone());
		associatedUsers.update();
		tags.update();

		final boolean isShowingAssociatedUsers = !scope.getProgress().isDone() && associatedUsers.getWidgetCount() > 0;
		descriptionLabel.setStyleName(style.descriptionLabelWithAssociatedUsers(), isShowingAssociatedUsers);
		return updateDescription();
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		final String description = scope.getDescription();
		if (description.equals(currentScopeDescription)) return false;
		currentScopeDescription = description;

		descriptionLabel.setText(currentScopeDescription);

		return true;
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

	public boolean isAssociationHighlight() {
		return associationHighlight;
	}

	public void setSelected(final boolean shouldSelect) {
		selected = shouldSelect;
		panel.setStyleName(style.selected(), selected);
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public void addAssociatedUsers(final DraggableMemberWidget memberWidget) {
		memberWidget.setSizeSmall();
		associatedUsers.add(memberWidget);
	}

}
