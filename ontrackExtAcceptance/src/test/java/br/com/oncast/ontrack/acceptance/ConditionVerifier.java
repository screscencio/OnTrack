package br.com.oncast.ontrack.acceptance;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class ConditionVerifier implements ConditionVerifierMomentSpecification {

	public enum Condition {
		IS_DIPLAYED {
			@Override
			void executeFor(final By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException {
				final boolean isDisplayed = driver.findElement(elementReference).isDisplayed();
				if (!isDisplayed) throw new ConditionNotMeetException("The element '" + elementReference + "' is not being displayed.");
			}
		},
		IS_NOT_DISPLAYED {
			@Override
			void executeFor(final By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException {
				final boolean isDisplayed = driver.findElement(elementReference).isDisplayed();
				if (isDisplayed) throw new ConditionNotMeetException("The element '" + elementReference + "' is being displayed.");
			}
		},
		IS_ENABLED {
			@Override
			void executeFor(final By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException {
				final boolean isEnabled = driver.findElement(elementReference).isEnabled();
				if (!isEnabled) throw new ConditionNotMeetException("The element '" + elementReference + "' is not enabled.");
			}
		},
		IS_DISABLED {
			@Override
			void executeFor(final By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException {
				final boolean isEnabled = driver.findElement(elementReference).isEnabled();
				if (isEnabled) throw new ConditionNotMeetException("The element '" + elementReference + "' is enabled.");
			}
		},
		IS_SELECTED {
			@Override
			void executeFor(final By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException {
				final boolean isSelected = driver.findElement(elementReference).isSelected();
				if (!isSelected) throw new ConditionNotMeetException("The element '" + elementReference + "' is not selected.");
			}
		};

		abstract void executeFor(By elementReference, final WebDriver driver) throws NoSuchElementException, ConditionNotMeetException;
	}

	private final Condition condition;
	private final By elementReference;
	private final WebDriver driver;
	private final String conditionFailureMessage;

	private ConditionVerifier(final WebDriver driver, final Condition condition, final String conditionFailureMessage, final By elementReference) {
		this.driver = driver;
		this.condition = condition;
		this.elementReference = elementReference;
		this.conditionFailureMessage = conditionFailureMessage;
	}

	public ConditionVerifier(final WebDriver driver, final Condition condition, final By elementReference) {
		this(driver, condition, null, elementReference);
	}

	public static ConditionVerifierMomentSpecification verify(final By elementReference, final Condition condition, final WebDriver driver) {
		return new ConditionVerifier(driver, condition, elementReference);
	}

	public static ConditionVerifier verify(final By elementReference, final Condition condition, final String conditionFailureMessage, final WebDriver driver) {
		return new ConditionVerifier(driver, condition, conditionFailureMessage, elementReference);
	}

	@Override
	public void now() {
		try {
			condition.executeFor(elementReference, driver);
		}
		catch (final NoSuchElementException e) {
			Assert.fail("The specified element '" + elementReference.toString() + "' could not be found.");
		}
		catch (final ConditionNotMeetException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Override
	public void during(final long millis) {
		boolean conditionWasMeet = true;
		String conditionFailureMessage = "";

		final long end = System.currentTimeMillis() + millis;
		while (System.currentTimeMillis() < end) {
			try {
				condition.executeFor(elementReference, driver);
				return;
			}
			catch (final NoSuchElementException e) {
				// Purposefully ignoring;
			}
			catch (final ConditionNotMeetException e) {
				conditionWasMeet = false;
				conditionFailureMessage = e.getMessage();
			}
		}
		if (!conditionWasMeet) Assert.fail(this.conditionFailureMessage == null ? conditionFailureMessage : this.conditionFailureMessage);
		else Assert.fail("The specified element '" + elementReference.toString() + "' could not be found during the specified '" + millis + "' millis.");
	}
}
