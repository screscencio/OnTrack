package br.com.oncast.ontrack.acceptance;

import org.openqa.selenium.WebDriver;

public class WebDriverFactory<T extends WebDriver> {

	Class<T> clazz;

	public WebDriverFactory(final Class<T> clazz) {
		this.clazz = clazz;
	}

	public T createWebDriver() {
		try {
			return clazz.newInstance();
		}
		catch (final Exception _ex) {
			_ex.printStackTrace();
			throw new RuntimeException();
		}
	}
}
