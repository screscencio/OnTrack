package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ScopeTreeShortcutMappingsMessage extends BaseMessages {

	@Description("Description for update scope shortcut")
	@DefaultMessage("Edit the selected scope")
	String updateScope();

	@Description("Description for insert sibling down shortcut")
	@DefaultMessage("Insert a scope under the selected scope")
	String insertSiblingDown();

	@Description("Description for insert sibling up shortcut")
	@DefaultMessage("Insert a scope above the selected scope")
	String insertSiblingUp();

	@Description("Description for insert child scope shortcut")
	@DefaultMessage("Insert a scope as a child of the selected scope")
	String insertChild();

	@Description("Description for insert parent scope shortcut")
	@DefaultMessage("Insert a scope as the parent of the selected scope")
	String insertParent();

	@Description("Description for move scope up shortcut")
	@DefaultMessage("Move the selected scope up")
	String moveUp();

	@Description("Description for move scope down shortcut")
	@DefaultMessage("Move the selected scope down")
	String moveScopeDown();

	@Description("Description for move scope right shortcut")
	@DefaultMessage("Move the selected scope right as a child of the scope above")
	String moveRight();

	@Description("Description for move scope left shortcut")
	@DefaultMessage("Move the selected scope left as a sibling of the parent scope")
	String moveLeft();

	@Description("Description for scope remove shortcut")
	@DefaultMessage("Removes the selected scope")
	String deleteScope();

	@Description("open impediments of the selected scope shortcut")
	@DefaultMessage("View and add impediments for the selected scope")
	String declareImpediment();

	@Description("Description for bind release shortcut")
	@DefaultMessage("Associates the selected scope with a release of your choice")
	String bindRelease();

	@Description("Description for declare progress shortcut")
	@DefaultMessage("Declares a progress of your choice to the selected scope")
	String declareProgress();

	@Description("Description for declare effort shortcut")
	@DefaultMessage("Declares a effort of your choice to the selected scope")
	String declareEffort();

	@Description("Description for declare value shortcut")
	@DefaultMessage("Declares a value of your choice to the selected scope")
	String declareValue();

	@Description("Description for open details panel shortcut")
	@DefaultMessage("Opens a panel with details of the selected scope")
	String openDetailsPanel();

	@Description("Description for toggle value column shortcut")
	@DefaultMessage("Show / hide values of all scopes")
	String toggleValueColumn();

	@Description("Description for toggle progress column shortcut")
	@DefaultMessage("Show / hide progresses of all scopes")
	String toggleProgressColumn();

	@Description("Description for toggle release column shortcut")
	@DefaultMessage("Show / hide releases of all scopes")
	String toggleReleaseColumn();

	@Description("Description for toggle effort column shortcut")
	@DefaultMessage("Show / hide efforts of all scopes")
	String toggleEffortColumn();

	@Description("Description for find scope inside a release shortcut")
	@DefaultMessage("Find scope inside a release")
	String findScopeAtReleaseWidget();

	@Description("select previous selected scope shortcut")
	@DefaultMessage("Jump to previous selection")
	String selectPreviousSelectedScope();

	@Description("select next selected scope shortcut")
	@DefaultMessage("Jump to next selection")
	String selectNextSelectedScope();

	@Description("add or remove a tag on the selected scope shortcut")
	@DefaultMessage("Add or remove a tag on the selected scope")
	String addOrRemoveTagToScope();

	@Description("Can''t move the scope because both the selected scope and the targeted parent scope are in a release")
	@DefaultMessage("Can''t move the scope because both the selected scope and the targeted parent scope are in a release")
	String cantMoveBecauseCantCascadeScopesWithReleases();

	@Description("Quick add an annotation to scope shortcut")
	@DefaultMessage("Quickly add an annotation to the selected scope")
	String quickAddAnnotationToScope();

}
