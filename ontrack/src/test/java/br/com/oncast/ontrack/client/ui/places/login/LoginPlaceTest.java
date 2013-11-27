package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import org.junit.Test;

import com.google.gwt.place.shared.Place;

import static org.junit.Assert.assertTrue;

public class LoginPlaceTest {

	@Test
	public void defaultDestinationPlaceIsTheApplicationEntryPointsDefaultAppPlace() throws Exception {
		assertTrue(new LoginPlace().getDestinationPlace() instanceof ProjectSelectionPlace);
	}

	@Test
	public void destinationPlaceShouldBeTheGivenPlace() throws Exception {
		final Place givenPlace = new ProjectSelectionPlace();
		assertTrue(new LoginPlace(givenPlace).getDestinationPlace() instanceof ProjectSelectionPlace);
	}
}
