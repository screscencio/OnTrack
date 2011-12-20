package br.com.oncast.ontrack.acceptance.login;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import br.com.oncast.ontrack.acceptance.AcceptanceTestUtils;
import br.com.oncast.ontrack.acceptance.WebDriverFactory;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils;
import br.com.oncast.ontrack.acceptance.navigation.NavigationTestUtils.NavigationPlaces;

@RunWith(ParameterizedType.class)
public class LoginTest {

	private final WebDriverFactory<?> driverFactory;
	private WebDriver driver;

	public LoginTest(final WebDriverFactory<?> driverFactory) {
		this.driverFactory = driverFactory;
	}

	@Before
	public void setUp() {
		driver = driverFactory.createWebDriver();
	}

	@After
	public void tearDown() {
		driver.close();
	}

	@Test
	public void browserStartsAtLoginPlace() {
		new NavigationTestUtils(driver).goToApplicationEntryPoint().verifyBrowserIsAt(NavigationPlaces.LOGIN);
	}

	@Test
	public void loginFailsWhenUserAndPasswordAreBlank() {
		final String username = "";
		final String password = "";

		new NavigationTestUtils(driver).goToApplicationEntryPoint();
		new LoginTestUtils(driver).authenticate(username, password).verifyMessageIsShowing()
				.verifyMessage("Please provide a valid e-mail.");
	}

	@Test
	public void loginFailsWhenUserDoesNotExit() {
		final String username = "blablabal";
		final String password = "";

		new NavigationTestUtils(driver).goToApplicationEntryPoint();
		new LoginTestUtils(driver).authenticate(username, password).verifyMessageIsShowing().verifyMessage("Incorrect user or password.");
	}

	@Test
	public void loginFailsWhenBothUserAndPasswordAreWrong() {
		final String username = "bla";
		final String password = "bli";

		new NavigationTestUtils(driver).goToApplicationEntryPoint();
		new LoginTestUtils(driver).authenticate(username, password).verifyMessageIsShowing().verifyMessage("Incorrect user or password.");
	}

	@Test
	public void loginSuccedesWhenUserAndPasswordAreAdminCredentials() {
		// FIXME Use credential's reference constants
		final String username = "admin@ontrack.com";
		final String password = "ontrackpoulain";

		final NavigationTestUtils navigationTestUtils = new NavigationTestUtils(driver).goToApplicationEntryPoint();
		new LoginTestUtils(driver).authenticate(username, password).verifyMessageIsNotShowing();
		navigationTestUtils.verifyBrowserIsAt(NavigationPlaces.PROJECT_SELECTION);
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		return AcceptanceTestUtils.getTestingWebDriverFactories();
	}
}
