package com.google.gwt.user.client.ui;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class CustomGwtTree extends Tree {

	@Override
	public void onBrowserEvent(final Event event) {
		final int eventType = DOM.eventGetType(event);

		switch (eventType) {
		// Intentional fallthrough.
		case Event.ONKEYPRESS:
		case Event.ONKEYUP:
			// Issue 1890: Do not block history navigation via alt+left/right
			if (DOM.eventGetAltKey(event) || DOM.eventGetMetaKey(event)) {
				super.onBrowserEvent(event);
				return;
			}
			break;
		}

		switch (eventType) {
		case Event.ONCLICK: {
			final Element e = DOM.eventGetTarget(event);
			if (shouldTreeDelegateFocusToElement(e)) {
				// The click event should have given focus to this element already.
				// Avoid moving focus back up to the tree (so that focusable widgets
				// attached to TreeItems can receive keyboard events).
			} else if (curSelection != null) {
				setFocus(true);
			}
			break;
		}

		case Event.ONMOUSEDOWN: {
			// Currently, the way we're using image bundles causes extraneous events
			// to be sunk on individual items' open/close images. This leads to an
			// extra event reaching the Tree, which we will ignore here.
			// Also, ignore middle and right clicks here.
			if ((DOM.eventGetCurrentTarget(event) == getElement()) && (event.getButton() == Event.BUTTON_LEFT)) {
				elementClicked(DOM.eventGetTarget(event));
			}
			break;
		}
		case Event.ONKEYDOWN: {
			keyboardNavigation(event);
			lastWasKeyDown = true;
			break;
		}

		case Event.ONKEYPRESS: {
			if (!lastWasKeyDown) {
				keyboardNavigation(event);
			}
			lastWasKeyDown = false;
			break;
		}

		case Event.ONKEYUP: {
			if (DOM.eventGetKeyCode(event) == KeyCodes.KEY_TAB) {
				final ArrayList<Element> chain = new ArrayList<Element>();
				collectElementChain(chain, getElement(), DOM.eventGetTarget(event));
				final TreeItem item = findItemByChain(chain, 0, root);
				if (item != getSelectedItem()) {
					setSelectedItem(item, true);
				}
			}
			lastWasKeyDown = false;
			break;
		}
		}

		switch (eventType) {
		case Event.ONKEYDOWN:
		case Event.ONKEYUP: {
			if (isArrowKey(DOM.eventGetKeyCode(event)) && !(event.getAltKey() || event.getCtrlKey() || event.getShiftKey())) {
				DOM.eventCancelBubble(event, true);
				DOM.eventPreventDefault(event);
				return;
			}
		}
		}

		// We must call super for all handlers.
		super.onBrowserEvent(event);
	}
}
