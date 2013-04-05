package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ScopeTreeItemWidgetMessages extends BaseMessages {

	@Description("message shown when the sum of the values declared in the descendants of a scope is greater than the declared in the scope")
	@DefaultMessage("The sum of the values ​​declared in the descendants should NOT be greater than the value declared in here ({0}).")
	String conflicted(String parentAmount);

}
