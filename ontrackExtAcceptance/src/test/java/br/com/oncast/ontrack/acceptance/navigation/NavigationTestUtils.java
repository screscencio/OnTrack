package br.com.oncast.ontrack.acceptance.navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.com.oncast.ontrack.acceptance.ConditionVerifier;
import br.com.oncast.ontrack.acceptance.ConditionVerifier.Condition;

public class NavigationTestUtils {

	public enum NavigationPlaces {
		LOGIN("gwt-debug-loginPlace"),
		PROJECT_SELECTION("gwt-debug-projectSelectionPlace");

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
		ConditionVerifier.verify(By.id(place.panelDebugId), Condition.IS_DIPLAYED, "The browser is not at '" + place.name() + "'.", driver).during(500);

		return this;
	}

}
