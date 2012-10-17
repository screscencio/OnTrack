package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

public interface MembersScopeSelectionService {

	String getSelectionColor(User user);

	List<Selection> getSelectionsFor(Scope scope);

}
