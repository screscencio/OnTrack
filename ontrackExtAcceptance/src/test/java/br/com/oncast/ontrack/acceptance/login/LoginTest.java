package br.com.oncast.ontrack.acceptance.login;

import org.junit.Test;

import br.com.oncast.ontrack.acceptance.AbstractAcceptanceTest;
import br.com.oncast.ontrack.acceptance.WebDriverFactory;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils.NavigationPlaces;

public class LoginTest extends AbstractAcceptanceTest {

	public LoginTest(final WebDriverFactory<?> driverFactory) {
		super(driverFactory);
	}

	@Test
	public void browserStartsAtLoginPlace() {
		new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
	}

	@Test
	public void loginFailsWhenUserAndPasswordAreBlank() {
		final String username = "";
		final String password = "";

		new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
		new LoginTestUtils(getCurrentWebDriver()).authenticate(username, password).verifyMessageIsShowing()
				.verifyMessage("Please provide a valid e-mail.");
	}

	@Test
	public void loginFailsWhenUserDoesNotExist() {
		final String username = "blablabal";
		final String password = "";

		new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
		new LoginTestUtils(getCurrentWebDriver()).authenticate(username, password).verifyMessageIsShowing().verifyMessage("Incorrect user or password.");
	}

	@Test
	public void loginFailsWhenBothUserAndPasswordAreWrong() {
		final String username = "bla";
		final String password = "bli";

		new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
		new LoginTestUtils(getCurrentWebDriver()).authenticate(username, password).verifyMessageIsShowing().verifyMessage("Incorrect user or password.");
	}

	@Test
	public void loginSuccedesWhenUserAndPasswordAreAdminCredentials() {
		final NavigationTestUtils navigationTestUtils = new NavigationTestUtils(getCurrentWebDriver()).goToApplicationEntryPoint().verifyBrowserIsAt(
				NavigationPlaces.LOGIN);
		new LoginTestUtils(getCurrentWebDriver()).authenticateWithAdminCredentials().verifyMessageIsNotShowing();
		navigationTestUtils.verifyBrowserIsAt(NavigationPlaces.PROJECT_SELECTION);
	}
}
