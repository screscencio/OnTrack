package br.com.oncast.ontrack.client.services.user;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class to Wrap Json object following the spec on http://portablecontacts.net/draft-spec.html
 */
public class PortableContactJsonObject extends JavaScriptObject {

	protected PortableContactJsonObject() {}

	public final native String getDisplayName() /*-{
		return this.entry[0].displayName;
	}-*/;

	public final native String getPreferedUsername() /*-{
		return this.entry[0].preferredUsername;
	}-*/;
}