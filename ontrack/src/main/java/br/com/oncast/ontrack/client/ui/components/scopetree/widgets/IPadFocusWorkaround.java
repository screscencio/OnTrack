package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.TextBox;

public class IPadFocusWorkaround {

	private final TextBox editionBox;
	private final boolean isIPad;

	boolean shouldNotAllowBlur = false;
	private final Timer focusTimer = new Timer() {

		@Override
		public void run() {
			shouldNotAllowBlur = false;
		}
	};

	public IPadFocusWorkaround(final TextBox editionBox) {
		this.editionBox = editionBox;
		isIPad = Navigator.getPlatform().toLowerCase().equals("ipad");
	}

	public boolean focus() {
		if (!isIPad) return false;
		shouldNotAllowBlur = true;
		focusTimer.schedule(500);
		editionBox.setFocus(true);
		return true;
	}

	public boolean shouldNotAllowBlur() {
		return isIPad && shouldNotAllowBlur;
	}
}
