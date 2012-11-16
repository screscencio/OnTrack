package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.utils.jquery.Event;

import com.google.gwt.dom.client.NativeEvent;

public class Shortcut {

	private final int keyCode;
	private ControlModifier ctrl;
	private ShiftModifier shift;
	private AltModifier alt;

	public Shortcut(final int keyCode, final ControlModifier ctrl, final ShiftModifier shift, final AltModifier alt) {
		this.keyCode = keyCode;
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}

	public Shortcut(final int keyCode) {
		this.keyCode = keyCode;
		this.ctrl = ControlModifier.UNPRESSED;
		this.shift = ShiftModifier.UNPRESSED;
		this.alt = AltModifier.UNPRESSED;
	}

	public Shortcut with(final ControlModifier control) {
		this.ctrl = control;
		return this;
	}

	public Shortcut with(final ShiftModifier shift) {
		this.shift = shift;
		return this;
	}

	public Shortcut with(final AltModifier alt) {
		this.alt = alt;
		return this;
	}

	public boolean accepts(final Event e) {
		return e.which() == keyCode && ctrl.matches(e) && shift.matches(e) && alt.matches(e);
	}

	public boolean accepts(final NativeEvent e) {
		return e.getKeyCode() == keyCode && ctrl.matches(e) && shift.matches(e) && alt.matches(e);
	}

	public int getKeyCode() {
		return keyCode;
	}

	public ShiftModifier getShift() {
		return shift;
	}

	public ControlModifier getCtrl() {
		return ctrl;
	}

	public AltModifier getAlt() {
		return alt;
	}

}
