package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

public interface ColorProviderService {

	Color getSelectionColorFor(User user);

	Color getColorFor(Scope scope);

	List<Selection> getMembersSelectionsFor(Scope scope);

}
