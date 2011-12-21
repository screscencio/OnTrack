package br.com.oncast.ontrack.acceptance.navigation;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class NavigationTestUtils {

	public enum NavigationPlaces {
		LOGIN("gwt-debug-loginPlace"), PROJECT_SELECTION("gwt-debug-projectSelectionPlace");

		private final String panelDebugId;

		private NavigationPlaces(final String panelDebugId) {
			this.panelDebugId = panelDebugId;
		}
	}

	private final WebDriver driver;

	public NavigationTestUtils(final WebDriver driver) {
		this.driver = driver;
	}

	public NavigationTestUtils goToApplicationEntryPoint() {
		driver.get("http://localhost:8888/");

		return this;
	}

	public NavigationTestUtils verifyBrowserIsAt(final NavigationPlaces place) {
		try {
			Assert.assertTrue("The browser is not at '" + place.name() + "'.", driver.findElement(By.id(place.panelDebugId)).isDisplayed());
		}
		catch (final NoSuchElementException e) {
			Assert.fail("The browser is not at '" + place.name() + "'.");
		}

		return this;
	}

}
