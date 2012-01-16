package br.com.oncast.ontrack.client.ui.places.login;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.oncast.ontrack.client.ApplicationEntryPoint;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.place.shared.Place;

public class LoginPlaceTest {

	@Test
	public void defaultDestinationPlaceIsTheApplicationEntryPointsDefaultAppPlace() throws Exception {
		assertEquals(ApplicationEntryPoint.DEFAULT_APP_PLACE, new LoginPlace().getDestinationPlace());
	}

	@Test
	public void destinationPlaceShouldBeTheGivenPlace() throws Exception {
		final Place givenPlace = new ProjectSelectionPlace();
		assertEquals(givenPlace, new LoginPlace(givenPlace).getDestinationPlace());
	}

	@Test
	public void destinationPlaceShouldBeTheDefaultPlaceWhenTheGivenPlaceIsLoginPlace() throws Exception {
		assertEquals(ApplicationEntryPoint.DEFAULT_APP_PLACE, new LoginPlace(new LoginPlace()).getDestinationPlace());
	}

}
