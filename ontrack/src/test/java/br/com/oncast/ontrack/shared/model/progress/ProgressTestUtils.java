package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Date;

public class ProgressTestUtils {

	public static void setProgressState(final Scope scope, final ProgressState state) {
		scope.getProgress().setState(state, UserRepresentationTestUtils.getAdmin(), new Date());
	}

	public static Progress create() {
		return new Progress(UserRepresentationTestUtils.getAdmin(), new Date(), new Date());
	}

	public static Progress create(final Date stateDate, final Date today) {
		return new Progress(UserRepresentationTestUtils.getAdmin(), stateDate, today);
	}
}