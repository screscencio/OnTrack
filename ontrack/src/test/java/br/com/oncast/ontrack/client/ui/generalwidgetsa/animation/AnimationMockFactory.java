package br.com.oncast.ontrack.client.ui.generalwidgetsa.animation;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class AnimationMockFactory implements AnimationFactory {

	private final int duration;

	public AnimationMockFactory(final int duration) {
		this.duration = duration;
	}

	@Override
	public ShowAnimation createShowAnimation(final Widget widget) {
		return new MockHideShowAnimation(widget.asWidget(), duration);
	}

	@Override
	public HideAnimation createHideAnimation(final Widget widget) {
		return new MockHideShowAnimation(widget.asWidget(), duration);
	}

	class MockHideShowAnimation implements ShowAnimation, HideAnimation {

		private AnimationCallback listener;
		private final Timer timer = new Timer() {

			@Override
			public void run() {
				onComplete();
			}

		};

		private final int animationDuration;

		public MockHideShowAnimation(final Widget asWidget, final int animationDuration) {
			this.animationDuration = animationDuration;
		}

		private void onComplete() {
			if (listener != null) listener.onComplete();
		}

		@Override
		public void hide() {
			if (animationDuration <= 0) onComplete();
			else timer.schedule(animationDuration);
		}

		@Override
		public void hide(final AnimationCallback listener) {
			this.listener = listener;
			hide();
		}

		@Override
		public void show() {
			if (animationDuration <= 0) onComplete();
			else timer.schedule(animationDuration);
		}

		@Override
		public void show(final AnimationCallback listener) {
			this.listener = listener;
			show();
		}

	}
}