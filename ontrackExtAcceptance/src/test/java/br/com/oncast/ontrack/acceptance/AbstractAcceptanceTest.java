package br.com.oncast.ontrack.acceptance;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;

@RunWith(Parameterized.class)
public abstract class AbstractAcceptanceTest {

	private final WebDriverFactory<?> driverFactory;
	private WebDriver driver;

	public AbstractAcceptanceTest(final WebDriverFactory<?> driverFactory) {
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

	public WebDriver getCurrentWebDriver() {
		return driver;
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		return AcceptanceTestUtils.getTestingWebDriverFactories();
	}
}
