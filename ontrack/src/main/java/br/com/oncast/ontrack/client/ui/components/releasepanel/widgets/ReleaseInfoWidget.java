package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseInfoWidget extends Composite {

	private static ReleaseInfoWidgetUiBinder uiBinder = GWT.create(ReleaseInfoWidgetUiBinder.class);

	interface ReleaseInfoWidgetUiBinder extends UiBinder<Widget, ReleaseInfoWidget> {}

	class HideShowAnimation extends Animation implements ShowAnimation, HideAnimation {

		private static final int DURATION = 300;

		private final Widget widget;
		private final Widget container;
		private AnimationCallback listener;

		private double container_margin_bottom_start;
		private double container_margin_bottom_end;
		private double widget_margin_top_start;
		private double widget_margin_top_end;

		public HideShowAnimation(final Widget widget, final Widget container) {
			this.widget = widget;
			this.container = container;
		}

		@Override
		public void show(final AnimationCallback listener) {
			this.listener = listener;
			show();
		}

		@Override
		public void hide(final AnimationCallback listener) {
			this.listener = listener;
			hide();
		}

		@Override
		public void show() {
			container_margin_bottom_start = 0;
			container_margin_bottom_end = -4;
			widget_margin_top_start = -60;
			widget_margin_top_end = -2;
			run(DURATION);
		}

		@Override
		public void hide() {
			container_margin_bottom_start = -4;
			container_margin_bottom_end = 0;
			widget_margin_top_start = -2;
			widget_margin_top_end = -60;
			run(DURATION);
		}

		@Override
		protected void onUpdate(final double progress) {
			onContainerUpdate(progress);
			onWidgetUpdate(progress);
		}

		private void onContainerUpdate(final double progress) {
			final double value = container_margin_bottom_start + ((container_margin_bottom_end - container_margin_bottom_start) * progress);
			container.getElement().getStyle().setMarginBottom(value, Unit.PX);
		}

		private void onWidgetUpdate(final double progress) {
			final double value = widget_margin_top_start + ((widget_margin_top_end - widget_margin_top_start) * progress);
			widget.getElement().getStyle().setMarginTop(value, Unit.PX);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			container.getElement().getStyle().setMarginBottom(container_margin_bottom_end, Unit.PX);
			widget.getElement().getStyle().setMarginTop(widget_margin_top_end, Unit.PX);
			if (listener != null) listener.onComplete();
		}
	}

	interface ReleaseInfoWidgetStyle extends CssResource {
		String speedInputDeclared();

		String speedInputInfered();
	}

	@UiField
	ReleaseInfoWidgetStyle style;

	@UiField
	SimplePanel container;

	@UiField
	HTMLPanel subcontainer;

	@UiField
	Label valueLabel;

	@UiField
	Label effortLabel;

	@UiField(provided = true)
	EditableLabel speedLabel;

	@UiField
	Label durationValueLabel;

	@UiField
	Label durationUnitLabel;

	@UiField
	FocusPanel speedFocus;

	private final HideShowAnimation hideShowAnimation;

	private final Release release;

	private boolean showing;

	private final ReleaseEstimator releaseEstimator;

	private float effortSum;

	private float estimatedVelocity;

	public ReleaseInfoWidget(final Release release) {
		this.release = release;
		releaseEstimator = ClientServiceProvider.getInstance().getReleaseEstimatorProvider().get();
		speedLabel = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public void onEditionStart() {}

			@Override
			public boolean onEditionRequest(final String text) {
				try {
					final Float speed = text == null || text.trim().isEmpty() ? null : Float.valueOf(text);
					ClientServiceProvider.getInstance().getActionExecutionService()
							.onUserActionExecutionRequest(new ReleaseDeclareEstimatedVelocityAction(release.getId(), speed));
				}
				catch (final NumberFormatException e) {}
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}
		});
		initWidget(uiBinder.createAndBindUi(this));
		hideShowAnimation = new HideShowAnimation(subcontainer, container);
		update();
	}

	@UiHandler("speedFocus")
	protected void onDoubleClick(final DoubleClickEvent event) {
		speedLabel.switchToEdit();
	}

	public void show() {
		if (showing) return;
		showing = true;
		hideShowAnimation.show();
	}

	public void hide() {
		if (!showing) return;
		showing = false;
		hideShowAnimation.hide();
	}

	public void toogle() {
		if (showing) hide();
		else show();
	}

	public void update() {
		updateValue();
		updateEffort();
		updateSpeed();
		updateDuration();
	}

	private void updateDuration() {
		if (effortSum < 1) {
			durationValueLabel.setText("-----");
			durationUnitLabel.setText("");
			return;
		}

		final int days = Math.round(effortSum / estimatedVelocity);
		final Date date = new Date();
		final WorkingDay workingDay = WorkingDayFactory.create(date);
		workingDay.add(days);
		final String differenceText[] = HumanDateFormatter.getDifferenceText(workingDay.getJavaDate().getTime() - date.getTime(), 1).split(" ");
		// FIXME LOBO Correct when it is less than 1 day
		// FIXME LOBO REmove the split workaround.
		durationValueLabel.setText(differenceText[0]);
		durationUnitLabel.setText(differenceText[1]);
	}

	private void updateSpeed() {
		speedFocus.setStyleName(style.speedInputDeclared(), release.hasDeclaredEstimatedVelocity());
		speedFocus.setStyleName(style.speedInputInfered(), !release.hasDeclaredEstimatedVelocity());
		estimatedVelocity = release.hasDeclaredEstimatedVelocity() ? release.getEstimatedVelocity() : releaseEstimator
				.getEstimatedVelocity(release);
		speedLabel.setValue(round(estimatedVelocity));
	}

	private void updateEffort() {
		effortSum = release.getEffortSum();
		effortLabel.setText(round(effortSum));
	}

	private void updateValue() {
		valueLabel.setText(round(release.getValueSum()));
	}

	private String round(final float number) {
		final String roundedFloat = ClientDecimalFormat.roundFloat(number, 1);
		if (roundedFloat.endsWith(".0")) return roundedFloat.substring(0, roundedFloat.length() - 2);
		return roundedFloat;
	}
}
