package br.com.oncast.ontrack.client.utils.html;

import com.google.gwt.user.client.ui.InlineHTML;

public class HTMLTextUtils {

	private static final String HYPER_LINK_TAG_START = "<a ";

	public static String getTextOnly(final String html) {
		return new InlineHTML(html).getText();
	}

	public static String setTargetBlankInHyperLinks(final String html) {
		String injectedHtml = html;
		int index = -1;
		while ((index = injectedHtml.indexOf(HYPER_LINK_TAG_START, index + 1)) > -1) {
			final String tag = injectedHtml.substring(index, injectedHtml.indexOf('>', index));
			if (tag.toLowerCase().contains("target=\"")) {
				injectedHtml = injectedHtml.substring(0, index) + tag.replaceAll("target=\"[^\"]*\"", "target=\"_blank\"") + injectedHtml.substring(index + tag.length());
			} else injectedHtml = injectedHtml.substring(0, index) + "<a target=\"_blank\" " + injectedHtml.substring(index + HYPER_LINK_TAG_START.length());
		}
		return injectedHtml;
	}

}
