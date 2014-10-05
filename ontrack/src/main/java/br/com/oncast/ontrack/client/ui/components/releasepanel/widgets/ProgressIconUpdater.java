package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.BgColorAnimation;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public enum ProgressIconUpdater {
	NOT_STARTED_UPDATER(ProgressState.NOT_STARTED) {
		@Override
		public String getStyle(final ProgressIconUpdaterStyle style) {
			return style.progressIconNotStarted();
		}

		@Override
		public String getTitle(final Scope scope) {
			final float accomplishedPercentual = scope.getEffort().getAccomplishedPercentual();
			return accomplishedPercentual != 0 ? MESSAGES.accomplished(ClientDecimalFormat.roundFloat(accomplishedPercentual, 0)) : "";
		}
	},
	UNDER_WORK_UPDATER(ProgressState.UNDER_WORK) {
		@Override
		public String getStyle(final ProgressIconUpdaterStyle style) {
			return style.progressIconUnderwork();
		}

		@Override
		public String getTitle(final Scope scope) {
			return scope.getProgress().getDescription();
		}

		@Override
		public void animate(final Widget panel) {
			new BgColorAnimation(panel, Color.GRAY).animate();
		}
	},
	DONE_UPDATER(ProgressState.DONE) {
		@Override
		public String getStyle(final ProgressIconUpdaterStyle style) {
			return style.progressIconDone();
		}

		@Override
		public String getTitle(final Scope scope) {
			return MESSAGES.finishedIn(HumanDateFormatter.get().formatDateRelativeToNow(scope.getProgress().getEndDay().getJavaDate()));
		}

		@Override
		public void animate(final Widget panel) {
			new BgColorAnimation(panel, Color.GREEN).animate();
		}
	},
	IMPEDIMENTS_UPDATER(null) {
		@Override
		public String getStyle(final ProgressIconUpdaterStyle style) {
			return "icon-flag " + style.progressIconHasOpenImpediments();
		}

		@Override
		public String getTitle(final Scope scope) {
			return MESSAGES.hasOpenImpediments();
		}

		@Override
		public void animate(final Widget panel) {
			new BgColorAnimation(panel, Color.RED).animate();
		}
	},
	NULL_UPDATER(null) {
		@Override
		public String getStyle(final ProgressIconUpdaterStyle style) {
			return "";
		}

		@Override
		public String getTitle(final Scope scope) {
			return "";
		}
	};

	private final ProgressState state;

	private final static CommandMenuMessages MESSAGES = GWT.create(CommandMenuMessages.class);

	ProgressIconUpdater(final ProgressState state) {
		this.state = state;
	}

	public void animate(final Widget panel) {}

	public static ProgressIconUpdater getUpdater(final Scope scope, final boolean hasImpediments) {
		if (hasImpediments) return IMPEDIMENTS_UPDATER;

		for (final ProgressIconUpdater updater : values())
			if (updater.state == scope.getProgress().getState()) return updater;
		return NULL_UPDATER;
	}

	public abstract String getStyle(ProgressIconUpdaterStyle style);

	public abstract String getTitle(Scope scope);
}