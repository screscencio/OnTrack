package br.com.oncast.ontrack.utils.mocks.gwtTestUtilsPatchers;

import br.com.oncast.ontrack.client.utils.speedtracer.SpeedTracerConsole;

import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

@PatchClass(SpeedTracerConsole.class)
public class SpeedTracerConsolePatcher {

	@PatchMethod
	static void log(final String msg) {
		System.out.println(msg);
	}
}
