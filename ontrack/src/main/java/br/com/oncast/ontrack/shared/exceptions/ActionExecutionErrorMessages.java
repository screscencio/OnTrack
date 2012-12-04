package br.com.oncast.ontrack.shared.exceptions;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ActionExecutionErrorMessages extends BaseMessages {

	@Description("unknown error")
	@DefaultMessage("Unable to execute action: {0}")
	String unknownError(String errorMessage);

	@Description("error caused when the user tries to move the root node")
	@DefaultMessage("It is not possible to move the root node.")
	String moveRootNode();

	@Description("error caused when the user tries to move down the last node")
	@DefaultMessage("It is not possible to move down the last node.")
	String moveDownLastNode();

	@Description("caused when tries to create a annotation with empty message and no attachment")
	@DefaultMessage("A annotation should have a message or an attachment file")
	String annotationWithEmptyMessageAndAttachment();

	@Description("message shown when the user tries to create a sibling for the root node.")
	@DefaultMessage("It is not possible to create a sibling for the root node.")
	String creatingRootSibling();

	@Description("caused when the scope tree item was not found for the given model.")
	@DefaultMessage("The internal insertion action execution was not able to be completed successfuly: It was not possible to find the desired TreeItem.")
	String treeItemNotFound();

	@Description("caused when tries to remove an annotation created by other user.")
	@DefaultMessage("Can''t remove a anotation created by another user.")
	String annotationRemove();

	@Description("caused when tries to remove vote from a deprecated annotation.")
	@DefaultMessage("It''s not possible to remove vote from a deprecated Annotation")
	String voteRemoveFromDeprecatedAnnotation();

	@Description("caused when tries to remove the ungiven vote")
	@DefaultMessage("It''s not possible to remove the ungiven vote")
	String removeUngivenVote();

	@Description("caused when the needed checklist item was not found")
	@DefaultMessage("Unable to complete the operation. the checklist item could not be found.")
	String checklistItemNotFound();

	@Description("caused when tries to execute an action over a deprecated annotation.")
	@DefaultMessage("Unable to complete the operation. the Annotation is deprecated")
	String operationOverDeprecatedAnnotation();

	@Description("caused when tries to remove an impediment from not impeded annotation.")
	@DefaultMessage("Unable to remove an impediment when the annotation is not impeded.")
	String removeImpedimentFromNotImpededAnnotation();

	@Description("caused when tries to remove an impediment opened by another user.")
	@DefaultMessage("Unable to remove an impediment that was opened by another user.")
	String removeImpedimentFromAnotherAuthor();

	@Description("caused when tries to solve a not impeded annotation.")
	@DefaultMessage("Unable to solve an impediment when it was not impeded.")
	String solveNotImpededAnnotation();

	@Description("caused when tries to set a kanban column that was already set.")
	@DefaultMessage("The column is already set.")
	String kanbanColumnAlreadySet();

	@Description("caused when tries to execute an action over an inexistant kanban column.")
	@DefaultMessage("Error! The column does not exist.")
	String kanbanColumnNotFound();

	@Description("caused when tries remove a static kanban column.")
	@DefaultMessage("Can''t remove a static column.")
	String removeStaticKanbanColumn();

	@Description("caused when tries set an empty new description.")
	@DefaultMessage("The new description can''t be empty")
	String emptyDescription();

	@Description("caused when tries declare estimated velocity as 0.")
	@DefaultMessage("Can''t declare estimated velocity as 0.")
	String declareEstimatedVelocityAsZero();

	@Description("caused when tries remove the root node")
	@DefaultMessage("Unable to remove root node.")
	String removeRootNode();

	@Description("caused when tries set invalid description for a release")
	@DefaultMessage("Invalid release description.")
	String invalidReleaseDescription();

	@Description("caused when tries prioritise a scope in a release that don't contains the scope")
	@DefaultMessage("The scope is not part of the referenced release.")
	String releaseNotContainsScope();

	@Description("caused when tries to up the priority when it's already in the most prioritary position")
	@DefaultMessage("It''s already in the most prioritary position.")
	String alreadyTheMostPrioritary();

	@Description("caused when tries to down the priority when it's already in the least prioritary position")
	@DefaultMessage("It''s already in the least prioritary position.")
	String alreadyTheLeastPrioritary();

	@Description("caused when tries change the priority of the root release")
	@DefaultMessage("Unable to change priority of the root release.")
	String changeRootReleasePriority();

	@Description("caused when tries move up the first node")
	@DefaultMessage("It is not possible to move up the first node.")
	String moveUpFirstNode();

	@Description("caused when tries move right the first node")
	@DefaultMessage("It''s not possible to move right the first node")
	String moveRightFirstNode();

	@Description("caused when tries move left the root node's son")
	@DefaultMessage("It is not possible to move left when the parent is the root node.")
	String moveLeftRootNodeSon();

	@Description("caused when rollback failed due to inconsistencies")
	@DefaultMessage("It is not possible to rollback this action due to inconsistencies.")
	String rollbackInconsistency();

	@Description("caused when tries to create a parent node for the root node")
	@DefaultMessage("It is not possible to create a parent node for the root node.")
	String createRootParent();

	@Description("caused when there is no mapped action executer for the given class")
	@DefaultMessage("There is no mapped action executer for {0}.")
	String noMappedExecutor(String className);

	@Description("caused when tries to create a description with empty message")
	@DefaultMessage("A description should have a message")
	String descriptionWithEmptyMessage();

	@Description("caused when tries to remove a description created by other user.")
	@DefaultMessage("Can''t remove a description created by another user.")
	String descriptionRemoveAction();

}
