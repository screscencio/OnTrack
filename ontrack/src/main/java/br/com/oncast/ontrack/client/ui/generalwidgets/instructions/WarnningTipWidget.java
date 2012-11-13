package br.com.oncast.ontrack.client.ui.generalwidgets.instructions;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WarnningTipWidget extends Composite implements PopupAware, HasCloseHandlers<WarnningTipWidget> {

	private static WarnningTipWidgetUiBinder uiBinder = GWT.create(WarnningTipWidgetUiBinder.class);

	private static WarnningTipWidgetMessages messages = GWT.create(WarnningTipWidgetMessages.class);

	interface WarnningTipWidgetUiBinder extends UiBinder<Widget, WarnningTipWidget> {}

	@UiField
	FocusPanel root;

	@UiField
	Label title;

	@UiField
	HTMLPanel tips;

	@UiField
	DeckPanel deck;

	@UiField
	Label infoLabel;

	@UiField
	Label dismiss;

	private boolean mouseOver = false;

	private final DismissListener listener;

	private final Animation fadeAnimation;

	private final Timer fadeAnimationTimer;

	private boolean disableAutoHide = false;

	public WarnningTipWidget(final String title, final String tips, final DismissListener listener) {
		this.listener = listener;

		initWidget(uiBinder.createAndBindUi(this));

		fadeAnimation = createFadeAnimation();
		fadeAnimationTimer = createAnimationTimer();

		this.title.setText(title);
		this.dismiss.setText(messages.dismiss());
		setupTips(tips);
		setVisibleWidgetInDeck(0);
	}

	@UiHandler("dismiss")
	void onDismissClick(final ClickEvent e) {
		listener.onDismissRequested();
		hide();
	}

	@UiHandler("root")
	void onRootClick(final ClickEvent e) {
		setVisibleWidgetInDeck((deck.getVisibleWidget() + 1) % 2);
		disableAutoHide = true;
	}

	@UiHandler("root")
	void onMouseOver(final MouseOverEvent e) {
		mouseOver = true;
		showTips();
	}

	@UiHandler("root")
	void onMouseOout(final MouseOutEvent e) {
		mouseOver = false;
		scheduleFadeAnimation();
	}

	public boolean isTheMouseOver() {
		return mouseOver;
	}

	@Override
	public void show() {
		disableAutoHide = false;
		showTips();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		this.deck.showWidget(0);
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<WarnningTipWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	private void setVisibleWidgetInDeck(final int index) {
		deck.showWidget(index);
		infoLabel.setText(index == 0 ? messages.sugestions() : messages.back());
	}

	private void showTips() {
		fadeAnimation.cancel();
		fadeAnimationTimer.cancel();
		root.getElement().getStyle().setOpacity(1);
	}

	public void scheduleFadeAnimation() {
		if (disableAutoHide) return;

		fadeAnimationTimer.schedule(1300);
	}

	private void setupTips(final String tips) {
		this.tips.clear();
		for (final String line : tips.split("\\n")) {
			this.tips.add(new HTMLPanel(SimpleHtmlSanitizer.sanitizeHtml(line)));
		}
	}

	private Timer createAnimationTimer() {
		return new Timer() {
			@Override
			public void run() {
				fadeAnimation.run(800);
			}
		};
	}

	private Animation createFadeAnimation() {
		return new Animation() {
			@Override
			protected void onUpdate(final double progress) {
				root.getElement().getStyle().setOpacity(1 - progress);
			}

			@Override
			protected void onComplete() {
				hide();
			};
		};
	}

	public interface DismissListener {
		void onDismissRequested();
	}

}
