package br.com.oncast.ontrack.shared.exceptions;

import br.com.oncast.ontrack.shared.messageCode.BaseMessageCode;

public enum ActionExecutionErrorMessageCode implements BaseMessageCode<ActionExecutionErrorMessages> {
	UNKNOWN {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.unknownError(args[0]);
		}
	},
	MOVE_ROOT_NODE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.moveRootNode();
		}
	},
	MOVE_DOWN_LAST_NODE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.moveDownLastNode();
		}
	},
	ANNOTATION_WITH_EMPTY_MESSAGE_AND_ATTACHMENT {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.annotationWithEmptyMessageAndAttachment();
		}
	},
	CREATE_ROOT_SIBLING {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.creatingRootSibling();
		}
	},
	TREE_ITEM_NOT_FOUND {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.treeItemNotFound();
		}
	},
	ANNOTATION_REMOVE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.annotationRemove();
		}
	},
	VOTE_REMOVE_FROM_DEPRECATED_ANNOTATION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.voteRemoveFromDeprecatedAnnotation();
		}
	},
	REMOVE_UNGIVEN_VOTE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeUngivenVote();
		}
	},
	CHECKLIST_ITEM_NOT_FOUND {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.checklistItemNotFound();
		}
	},
	OPERATION_OVER_DEPRECATED_ANNOTATION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.operationOverDeprecatedAnnotation();
		}
	},
	REMOVE_IMPEDIMENT_FROM_NOT_IMPEDED_ANNOTATION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeImpedimentFromNotImpededAnnotation();
		}
	},
	REMOVE_IMPEDIMENT_OF_ANOTHER_AUTHOR {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeImpedimentFromAnotherAuthor();
		}
	},
	SOLVE_NOT_IMPEDED_ANNOTATION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.solveNotImpededAnnotation();
		}
	},
	KANBAN_COLUMN_ALREADY_SET {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.kanbanColumnAlreadySet();
		}
	},
	KANBAN_COLUMN_NOT_FOUND {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.kanbanColumnNotFound();
		}
	},
	REMOVE_STATIC_KANBAN_COLUMN {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeStaticKanbanColumn();
		}
	},
	EMPTY_DESCRIPTION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.emptyDescription();
		}
	},
	DECLARE_ESTIMATED_VELOCITY_AS_ZERO {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.declareEstimatedVelocityAsZero();
		}
	},
	REMOVE_ROOT_NODE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeRootNode();
		}
	},
	INVALID_RELEASE_DESCRIPTION {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.invalidReleaseDescription();
		}
	},
	RELEASE_NOT_CONTAINS_SCOPE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.releaseNotContainsScope();
		}
	},
	ALREADY_THE_MOST_PRIORITARY {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.alreadyTheMostPrioritary();
		}
	},
	ALREADY_THE_LEAST_PRIORITARY {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.alreadyTheLeastPrioritary();
		}
	},
	CHANGE_ROOT_RELEASE_PRIORITY {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.changeRootReleasePriority();
		}
	},
	MOVE_UP_FIRST_NODE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.moveUpFirstNode();
		}
	},
	MOVE_RIGHT_FIRST_NODE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.moveRightFirstNode();
		}
	},
	MOVE_LEFT_ROOT_NODE_SON {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.moveLeftRootNodeSon();
		}
	},
	ROLLBACK_INCONSITENCY {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.rollbackInconsistency();
		}
	},
	CREATE_ROOT_PARENT {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.createRootParent();
		}
	},
	NO_MAPPED_EXECUTOR {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.noMappedExecutor(args[0]);
		}
	},
	DESCRIPTION_WITH_EMPTY_MESSAGE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.descriptionWithEmptyMessage();
		}
	},
	DESCRIPTION_REMOVE {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.descriptionRemoveAction();
		}
	},
	CREATE_EXISTENT {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.createDuplicated();
		}
	},
	REMOVE_INEXISTENT {
		@Override
		public String selectMessage(final ActionExecutionErrorMessages messages, final String... args) {
			return messages.removeInexitent();
		}
	};

	@Override
	public abstract String selectMessage(final ActionExecutionErrorMessages messages, final String... args);
}
