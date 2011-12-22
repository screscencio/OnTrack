package br.com.oncast.ontrack.acceptance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;

public class AcceptanceTestUtils {

	private static List<Object[]> testingWebDriverFactories;

	// TODO Sleep in a better way.
	public static void sleep(final long millis) {
		final long end = System.currentTimeMillis() + millis;
		while (System.currentTimeMillis() < end)
			;
	}

	public static Collection<Object[]> getTestingWebDriverFactories() {
		if (testingWebDriverFactories != null) return testingWebDriverFactories;
		testingWebDriverFactories = new ArrayList<Object[]>();

		// testingWebDriverFactories.add(new Object[] { new WebDriverFactory<InternetExplorerDriver>(InternetExplorerDriver.class) });
		testingWebDriverFactories.add(new Object[] { new WebDriverFactory<FirefoxDriver>(FirefoxDriver.class) });

		return testingWebDriverFactories;
	}

}
