package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import java.util.Arrays;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.ShortcutLabel;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;

public class ScopeTreeInstructionGuide {

	private final static ScopeTreeInstructionMessages messages = GWT.create(ScopeTreeInstructionMessages.class);
	private final HasInstructions targetWidget;
	private ScopeTreeInstructionSteps currentStep = ScopeTreeInstructionSteps.NOT_CONFIGURED;

	public ScopeTreeInstructionGuide(final HasInstructions targetWidget) {
		this.targetWidget = targetWidget;
	}

	public void onActionExecution(final ModelAction action) {
		currentStep = currentStep.nextStep(action, targetWidget);
	}

	public void onSetContext(final ProjectContext context) {
		currentStep = ScopeTreeInstructionSteps.getStep(context, targetWidget);
	}

	private enum ScopeTreeInstructionSteps {
		NOT_CONFIGURED() {
			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				return this;
			}
		},
		NO_SCOPES(messages.insertChild()) {
			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				if (action instanceof ScopeInsertChildAction) return ONE_SCOPE_ONLY.updateWidget(targetWidget);
				targetWidget.clearInstructions();
				return this;
			}
		},
		ONE_SCOPE_ONLY(messages.insertBelow(), messages.insertAbove()) {
			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				if (action instanceof ScopeInsertSiblingAction) return FEW_SCOPES_ONE_LEVEL_ONLY.updateWidget(targetWidget);
				if (action instanceof ScopeInsertChildAction) {
					FEW_SCOPES_ONE_LEVEL_ONLY.updateWidget(targetWidget);
					return TWO_LEVELS.updateWidgetWithoutCleaning(targetWidget);
				}
				targetWidget.clearInstructions();
				return this;
			}
		},
		FEW_SCOPES_ONE_LEVEL_ONLY(messages.moveItems()) {
			public int scopesCount = 2;

			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				if (action instanceof ScopeInsertChildAction) return TWO_LEVELS.updateWidget(targetWidget);
				if (action instanceof ScopeMoveRightAction) return TWO_LEVELS.updateWidget(targetWidget);

				if (action instanceof ScopeInsertAction && ++scopesCount > 5) return END.updateWidget(targetWidget);
				targetWidget.clearInstructions();
				return this;
			}

			@Override
			public void reset() {
				super.reset();
				scopesCount = 2;
			}

			@Override
			protected ScopeTreeInstructionSteps setup(final int scopesCount) {
				this.scopesCount = scopesCount;
				return super.setup(scopesCount);
			}

		},
		TWO_LEVELS(messages.insertParent()) {
			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				return END.updateWidget(targetWidget);
			}
		},
		END {
			@Override
			protected ScopeTreeInstructionSteps nextStep(final ModelAction action, final HasInstructions targetWidget) {
				return this;
			}

			@Override
			public ScopeTreeInstructionSteps updateWidget(final HasInstructions targetWidget) {
				targetWidget.clearInstructions();
				return this;
			}
		};

		private List<String> instructions;
		private boolean firstTime = true;

		ScopeTreeInstructionSteps(final String... instructions) {
			this.instructions = Arrays.asList(instructions);
		}

		protected ScopeTreeInstructionSteps updateWidgetWithoutCleaning(final HasInstructions targetWidget) {
			if (!firstTime) return this;

			for (final String instruction : instructions) {
				targetWidget.addInstruction(new ShortcutLabel(instruction));
			}
			firstTime = false;
			return this;
		}

		public static ScopeTreeInstructionSteps getStep(final ProjectContext context, final HasInstructions targetWidget) {
			final Scope projectScope = context.getProjectScope();
			final int childCount = projectScope.getChildCount();

			if (childCount == 0) return NO_SCOPES.updateWidget(targetWidget);

			final Scope firstChild = projectScope.getChild(0);
			if (childCount == 1 && firstChild.isLeaf()) return ONE_SCOPE_ONLY.updateWidget(targetWidget);

			if (childCount == 1 && firstChild.getChildCount() == 1 && firstChild.getChild(0).isLeaf()) {
				FEW_SCOPES_ONE_LEVEL_ONLY.updateWidget(targetWidget).updateWidget(targetWidget);
				return TWO_LEVELS.updateWidgetWithoutCleaning(targetWidget);
			}

			final int scopesCount = projectScope.getAllDescendantScopes().size();
			final boolean hasManyScopes = scopesCount > 5;
			if (hasManyScopes) return END.updateWidget(targetWidget);

			boolean hasOneLevelOnly = true, thereAreTwoLevelsOnly = true;
			for (final Scope child : projectScope.getChildren()) {
				if (child.isLeaf()) continue;

				hasOneLevelOnly = false;
				for (final Scope grandChild : child.getChildren()) {
					if (!grandChild.isLeaf()) {
						thereAreTwoLevelsOnly = false;
						break;
					}
				}
			}

			if (hasOneLevelOnly) return FEW_SCOPES_ONE_LEVEL_ONLY.setup(scopesCount).updateWidget(targetWidget);

			if (thereAreTwoLevelsOnly) return TWO_LEVELS.updateWidget(targetWidget);

			return END.updateWidget(targetWidget);
		}

		protected ScopeTreeInstructionSteps setup(final int scopesCount) {
			return this;
		}

		public ScopeTreeInstructionSteps updateWidget(final HasInstructions targetWidget) {
			targetWidget.clearInstructions();
			return updateWidgetWithoutCleaning(targetWidget);
		}

		protected abstract ScopeTreeInstructionSteps nextStep(final ModelAction action, HasInstructions targetWidget);

		public void reset() {
			firstTime = true;
		}
	}

	public void reset() {
		for (final ScopeTreeInstructionSteps steps : ScopeTreeInstructionSteps.values()) {
			steps.reset();
		}
	}
}
