package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ScopeBindReleaseActionHelperMessages extends BaseMessages {

	@DefaultMessage("Can''t bind release because the descendant ''{0}'' is already bound to a release")
	@Description("unable to bind release because a descendant is already bound")
	String cantBindReleaseBecauseOfADescendant(String scopeDescription);

	@DefaultMessage("Can''t bind release because the ancestor ''{0}'' is already bound to a release")
	@Description("unable to bind release because an ancestor is already bound")
	String cantBindReleaseBecauseOfAnAncestor(String scopeDescription);

}
