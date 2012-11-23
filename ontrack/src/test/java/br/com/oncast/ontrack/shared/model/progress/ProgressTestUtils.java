package br.com.oncast.ontrack.shared.model.progress;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ProgressTestUtils {

	public static void setProgressState(final Scope scope, final ProgressState state) {
		scope.getProgress().setState(state, UserTestUtils.getAdmin(), new Date());
	}

	public static Progress create() {
		return new Progress(UserTestUtils.getAdmin(), new Date());
	}

}
