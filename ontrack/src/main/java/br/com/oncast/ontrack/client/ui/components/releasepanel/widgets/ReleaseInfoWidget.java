package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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

	@UiField
	SimplePanel container;

	@UiField
	HTMLPanel subcontainer;

	private final HideShowAnimation hideShowAnimation;

	private final Release release;

	private boolean showing;

	public ReleaseInfoWidget(final Release release) {
		this.release = release;
		initWidget(uiBinder.createAndBindUi(this));
		hideShowAnimation = new HideShowAnimation(subcontainer, container);
		update();
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
		// FIXME LOBO
	}

	private void updateSpeed() {
		// FIXME LOBO
	}

	private void updateEffort() {
		// FIXME LOBO
	}

	private void updateValue() {
		// FIXME LOBO
	}
}
