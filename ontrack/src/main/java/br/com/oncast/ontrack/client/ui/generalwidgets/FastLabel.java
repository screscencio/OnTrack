package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;

/**
 * {@link FastLabel}s are extensions to the plain GWT {@link Label} widget that caches some of its values to perform better by not consulting DOM so often.<br />
 * The cached properties currently are:
 * <ul>
 * <li>text</li>
 * <li>style name</li>
 * </ul>
 */
public class FastLabel extends Label {
	private String text;
	private final Set<String> styleNames = new HashSet<String>();

	/**
	 * The values entered in this method are cached. Be sure to remove them using the {@link #removeStyleName(String)} name; the cache will be corrupted if not
	 * so.
	 * @see com.google.gwt.user.client.ui.UIObject#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(final String style) {
		if (hasStyleName(style)) return;

		styleNames.add(style);
		super.addStyleName(style);
	}

	@Override
	public void removeStyleName(final String style) {
		if (styleNames.remove(style)) super.removeStyleName(style);
	}

	@Override
	public String getText() {
		if (text != null) return text;
		String superText = super.getText();
		if (superText == null) super.setText(superText = "");
		return text = superText;
	}

	@Override
	public void setText(final String text) {
		if (getText().equals(this.text)) return;
		this.text = text;
		super.setText(text);
	}

	@Override
	public void setText(final String text, final Direction dir) {
		if (getText().equals(this.text)) return;
		this.text = text;
		super.setText(text, dir);
	}

	/**
	 * Optimized search for a style names. It uses cache and hash to make it faster.<br />
	 * <b>Important</b>: This method only consider styles added by the {@link #addStyleName(String)} method.
	 * @param style the style to be checked.
	 * @return <tt>true</tt> in case the style is found, <tt>false</tt> otherwise.
	 */
	public boolean hasStyleName(final String style) {
		return styleNames.contains(style);
	}
}
