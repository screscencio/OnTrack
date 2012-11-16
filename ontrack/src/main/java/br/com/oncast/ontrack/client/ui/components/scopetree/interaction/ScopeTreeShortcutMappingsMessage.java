package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ScopeTreeShortcutMappingsMessage extends BaseMessages {

	@DefaultMessage("Edit the selected scope")
	@Description("Description for update scope shortcut")
	String updateScope();

	@DefaultMessage("Insert a scope under the selected scope")
	@Description("Description for insert sibling down shortcut")
	String insertSiblingDown();

	@DefaultMessage("Insert a scope above the selected scope")
	@Description("Description for insert sibling up shortcut")
	String insertSiblingUp();

	@DefaultMessage("Insert a scope as a child of the selected scope")
	@Description("Description for insert child scope shortcut")
	String insertChild();

	@DefaultMessage("Insert a scope as the parent of the selected scope")
	@Description("Description for insert parent scope shortcut")
	String insertParent();

	@DefaultMessage("Move the selected scope up")
	@Description("Description for move scope up shortcut")
	String moveUp();

	@DefaultMessage("Move the selected scope down")
	@Description("Description for move scope down shortcut")
	String moveScopeDown();

	@DefaultMessage("Move the selected scope right as a child of the scope above")
	@Description("Description for move scope right shortcut")
	String moveRight();

	@DefaultMessage("Move the selected scope left as a sibling of the parent scope")
	@Description("Description for move scope left shortcut")
	String moveLeft();

	@DefaultMessage("Removes the selected scope")
	@Description("Description for scope remove shortcut")
	String deleteScope();

	@DefaultMessage("Associates the selected scope with a release of your choice")
	@Description("Description for bind release shortcut")
	String bindRelease();

	@DefaultMessage("Declares a progress of your choice to the selected scope")
	@Description("Description for declare progress shortcut")
	String declareProgress();

	@DefaultMessage("Declares a effort of your choice to the selected scope")
	@Description("Description for declare effort shortcut")
	String declareEffort();

	@DefaultMessage("Declares a value of your choice to the selected scope")
	@Description("Description for declare value shortcut")
	String declareValue();

	@DefaultMessage("Show annotations for the selected scope")
	@Description("Description for show annotations shortcut")
	String showAnnotations();

	@DefaultMessage("Show / hide values of all scopes")
	@Description("Description for toggle value column shortcut")
	String toggleValueColumn();

	@DefaultMessage("Show / hide progresses of all scopes")
	@Description("Description for toggle progress column shortcut")
	String toggleProgressColumn();

	@DefaultMessage("Show / hide releases of all scopes")
	@Description("Description for toggle release column shortcut")
	String toggleReleaseColumn();

	@DefaultMessage("Show / hide efforts of all scopes")
	@Description("Description for toggle effort column shortcut")
	String toggleEffortColumn();

	@DefaultMessage("Find scope inside a release")
	@Description("Description for find scope inside a release shortcut")
	String findScopeAtReleaseWidget();

}
