package br.com.oncast.ontrack.client.ui.places;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingPlace;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.octo.gwt.test.GwtTest;

public class AppActivityMapperTest extends GwtTest {

	private Boolean isLoggedIn;
	private AppActivityMapper appActivityMapper;
	private Boolean isContextAvailable;

	@Before
	public void setUp() {
		final ClientServiceProvider clientServiceProvider = Mockito.mock(ClientServiceProvider.class);
		final AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
		Mockito.when(authenticationService.isUserLoggedIn()).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return isLoggedIn;
			}
		});
		Mockito.when(clientServiceProvider.getAuthenticationService()).thenReturn(authenticationService);

		final ContextProviderService contextProvider = Mockito.mock(ContextProviderService.class);
		Mockito.when(contextProvider.isContextAvailable()).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return isContextAvailable;
			}
		});
		Mockito.when(clientServiceProvider.getContextProviderService()).thenReturn(contextProvider);

		appActivityMapper = new AppActivityMapper(clientServiceProvider);
	}

	@Test
	public void whenUserNotLoggedInShouldRedirectToLoginActivity() {
		isLoggedIn = false;

		assertTrue(appActivityMapper.getActivity(null) instanceof LoginActivity);
	}

	@Test
	public void whenUserLoggedInAndPlaceInstanceOfContextLoadingPlaceShouldCreateContextLoadingActivity() {
		isLoggedIn = true;
		assertTrue(appActivityMapper.getActivity(new ContextLoadingPlace(null)) instanceof ContextLoadingActivity);
	}

	@Test
	public void whenUserLoggedInAndContextProviderNotAvailableShouldCreateContextLoadingActivity() {
		isLoggedIn = true;
		isContextAvailable = false;

		assertTrue(appActivityMapper.getActivity(new PlannnigPlace("")) instanceof ContextLoadingActivity);
	}

	@Test
	public void whenUserLoggedInAndContextProviderIsAvailableShouldCreatePlanningPlaceActivity() {
		isLoggedIn = true;
		isContextAvailable = true;

		assertTrue(appActivityMapper.getActivity(new PlannnigPlace("")) instanceof PlanningActivity);
	}

	@Test
	public void whenUserLoggedInAndPlaceNotInstanceOfPlanningPlaceNorContextLoadingPlaceAndContextAvailableShouldBeNull() {
		isLoggedIn = true;
		isContextAvailable = true;
		assertNull(appActivityMapper.getActivity(null));
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
