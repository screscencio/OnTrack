package br.com.oncast.ontrack.acceptance.login;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.oncast.ontrack.acceptance.AcceptanceTestUtils;

public class LoginTestUtils {

	private static final String EMAIL_INPUT_DEBUG_ID = "gwt-debug-emailInput";
	private static final String PASSWORD_INPUT_DEBUG_ID = "gwt-debug-passwordInput";
	private static final String SUBMIT_BUTTON_DEBUG_ID = "gwt-debug-submitButton";
	private static final String MESSAGE_OUTPUT_DEBUG_ID = "gwt-debug-messageOutput";

	private final WebDriver driver;

	public LoginTestUtils(final WebDriver driver) {
		this.driver = driver;
	}

	public LoginTestUtils authenticate(final String username, final String password) {
		driver.findElement(By.id(EMAIL_INPUT_DEBUG_ID)).sendKeys(username);
		driver.findElement(By.id(PASSWORD_INPUT_DEBUG_ID)).sendKeys(password);
		driver.findElement(By.id(SUBMIT_BUTTON_DEBUG_ID)).click();

		AcceptanceTestUtils.sleep(200);

		return this;
	}

	public LoginTestUtils authenticateWithAdminCredentials() {
		// TODO ++++ Use credential's reference constants
		final String username = "admin@ontrack.com";
		final String password = "ontrackpoulain";

		return authenticate(username, password);
	}

	public LoginTestUtils verifyMessageIsShowing() {
		Assert.assertTrue("The login message is not being displayed.", driver.findElement(By.id(MESSAGE_OUTPUT_DEBUG_ID)).isDisplayed());

		return this;
	}

	public LoginTestUtils verifyMessage(final String message) {
		final WebElement messageElement = driver.findElement(By.id(MESSAGE_OUTPUT_DEBUG_ID));
		Assert.assertEquals(message, messageElement.getText());

		return this;
	}

	public LoginTestUtils verifyMessageIsNotShowing() {
		try {
			Assert.assertFalse("The login message is being displayed.", driver.findElement(By.id(MESSAGE_OUTPUT_DEBUG_ID)).isDisplayed());
		}
		catch (final NoSuchElementException e) {
			// Purposefully ignored.
		}

		return this;
	}

}
