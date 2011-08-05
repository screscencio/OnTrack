package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressTestUtils {

	public static void setProgressState(final Scope scope, final ProgressState state) {
		scope.getProgress().setState(state);
	}

}
