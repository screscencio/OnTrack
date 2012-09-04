package br.com.oncast.ontrack.client.utils.forms;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class ResponseParser {

	/**
	 * Returns the plain text of an submit complete event results. <br />
	 * <p>
	 * {@link FormPanel} always send accept-content request as html or xml, so when the response is plain text or json, the result might be wrapped inside a
	 * {@code pre} tag.
	 * </p>
	 * <p>
	 * The source of this code can be found at <a href="http://stackoverflow.com/a/8318859">http://stackoverflow.com/a/8318859</a>.
	 * </p>
	 * 
	 * @param event the {@link SubmitCompleteEvent} with the server's response
	 * @return plain text of event result, removing eventual {@code pre} tags
	 */
	public static String getPlainTextResult(final SubmitCompleteEvent event) {
		final Element label = DOM.createLabel();
		label.setInnerHTML(event.getResults());
		return label.getInnerText();
	}

}
