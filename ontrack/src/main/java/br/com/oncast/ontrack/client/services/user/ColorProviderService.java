package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

public interface ColorProviderService {

	String getSelectionColorFor(User user);

	String getColorFor(Scope scope);

	List<Selection> getMembersSelectionsFor(Scope scope);

}
