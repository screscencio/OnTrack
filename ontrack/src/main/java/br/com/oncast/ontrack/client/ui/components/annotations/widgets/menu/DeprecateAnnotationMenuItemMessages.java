package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface DeprecateAnnotationMenuItemMessages extends BaseMessages {

	@Description("Mark as Deprecated tooltip")
	@DefaultMessage("Mark as Deprecated")
	String deprecate();

	@Description("Remove Deprecation tooltip")
	@DefaultMessage("Remove deprecation")
	String removeDeprecation();

}
