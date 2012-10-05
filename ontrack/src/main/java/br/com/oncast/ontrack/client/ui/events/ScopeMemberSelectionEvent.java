package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

public interface ScopeMemberSelectionEvent {

	Scope getTargetScope();

	User getMember();

}
