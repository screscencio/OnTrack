package br.com.oncast.ontrack.acceptance.login;

import org.junit.Test;

import br.com.oncast.ontrack.acceptance.AbstractAcceptanceTest;
import br.com.oncast.ontrack.acceptance.WebDriverFactory;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils.NavigationPlaces;

public class CopyOfLoginTest extends AbstractAcceptanceTest {

	public CopyOfLoginTest(final WebDriverFactory<?> driverFactory) {
		super(driverFactory);
	}

	@Test
	public void browserStartsAtLoginPlace() {
		new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
	}
}
