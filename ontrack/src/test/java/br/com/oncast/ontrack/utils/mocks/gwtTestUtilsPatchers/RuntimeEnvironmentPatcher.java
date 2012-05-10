package br.com.oncast.ontrack.utils.mocks.gwtTestUtilsPatchers;

import br.com.oncast.ontrack.client.utils.RuntimeEnvironment;

import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

@PatchClass(RuntimeEnvironment.class)
class RuntimeEnvironmentPatcher {

	@PatchMethod
	static boolean evaluateWhetherIsMac() {
		return false;
	}

}