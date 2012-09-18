package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface ReleaseWidgetMessages extends Messages {

	@Description("increase release priority menu text")
	@DefaultMessage("Increase priority")
	String increasePriority();

	@Description("decrease release priority menu text")
	@DefaultMessage("Decrease priority")
	String decreasePriority();

	@Description("delete release menu text")
	@DefaultMessage("Delete Release")
	String deleteRelease();

}
