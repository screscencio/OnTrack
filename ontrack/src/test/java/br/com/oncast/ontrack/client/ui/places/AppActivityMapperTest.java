package br.com.oncast.ontrack.client.ui.places;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;

import com.octo.gwt.test.GwtTest;

public class AppActivityMapperTest extends GwtTest {

	private static final int PROJECT_ID = 1;
	private Boolean isLoggedIn;
	private AppActivityMapper appActivityMapper;
	private Boolean isContextAvailable;

	@Mock
	private ContextProviderService contextProvider;
	@Mock
	private ClientServiceProvider clientServiceProvider;
	@Mock
	private AuthenticationService authenticationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(clientServiceProvider.getAuthenticationService()).thenReturn(authenticationService);
		when(clientServiceProvider.getContextProviderService()).thenReturn(contextProvider);

		when(authenticationService.isUserLoggedIn()).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return isLoggedIn;
			}
		});

		when(contextProvider.isContextAvailable(PROJECT_ID)).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return isContextAvailable;
			}
		});

		appActivityMapper = new AppActivityMapper(clientServiceProvider);
	}

	@Test
	public void whenUserIsNotLoggedInItShouldRedirectToLoginActivity() {
		isLoggedIn = false;

		assertTrue(appActivityMapper.getActivity(null) instanceof LoginActivity);
	}

	@Test
	public void whenUserLoggedInAndContextProviderNotAvailableShouldCreateContextLoadingActivity() {
		isLoggedIn = true;
		isContextAvailable = false;

		assertTrue(appActivityMapper.getActivity(new PlanningPlace(PROJECT_ID)) instanceof ContextLoadingActivity);
	}

	@Test
	public void whenUserLoggedInAndContextProviderIsAvailableShouldCreatePlanningPlaceActivity() {
		isLoggedIn = true;
		isContextAvailable = true;

		assertTrue(appActivityMapper.getActivity(new PlanningPlace(PROJECT_ID)) instanceof PlanningActivity);
	}

	@Test
	public void activityShouldBeNullWhenUserLoggedInAndPlaceNotInstanceOfPlanningPlaceNorContextLoadingPlaceAndContextAvailable() {
		isLoggedIn = true;
		isContextAvailable = true;
		assertNull(appActivityMapper.getActivity(null));
	}

	@Test
	public void contextAvaliabilityShouldBeCheckedWhenProjectDependentPlaceIsPassed() {
		isLoggedIn = true;
		isContextAvailable = true;

		final ProjectDependentPlace projectDependentPlace = mock(ProjectDependentPlace.class);
		when(projectDependentPlace.getRequestedProjectId()).thenReturn(1L);
		appActivityMapper.getActivity(projectDependentPlace);

		verify(contextProvider).isContextAvailable(Mockito.anyInt());
	}

	@Test
	public void contextAvaliabilityShouldNotBeCheckedWhenPassedPlaceIsNotAProjectDependentPlace() {
		isLoggedIn = true;

		final LoginPlace projectIndependentPlace = mock(LoginPlace.class);

		appActivityMapper.getActivity(projectIndependentPlace);

		verify(contextProvider, times(0)).isContextAvailable(Mockito.anyInt());
	}

	@Test
	public void contextShouldBeLoadedForProjectDependentPlaces() {
		isLoggedIn = true;
		isContextAvailable = false;

		final ProjectDependentPlace projectDependentPlace = mock(ProjectDependentPlace.class);
		when(projectDependentPlace.getRequestedProjectId()).thenReturn(1L);

		assertTrue(appActivityMapper.getActivity(projectDependentPlace) instanceof ContextLoadingActivity);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
