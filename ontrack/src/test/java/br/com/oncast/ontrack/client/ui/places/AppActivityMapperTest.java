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
import br.com.oncast.ontrack.client.ui.places.loading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.loading.UserInformationLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionActivity;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.googlecode.gwt.test.GwtTest;

public class AppActivityMapperTest extends GwtTest {

	private static final UUID PROJECT_ID = new UUID();
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
		when(authenticationService.isUserAvailable()).thenReturn(true);
		when(clientServiceProvider.getContextProviderService()).thenReturn(contextProvider);
		when(contextProvider.isContextAvailable(PROJECT_ID)).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return isContextAvailable;
			}
		});

		appActivityMapper = new AppActivityMapper(clientServiceProvider);
	}

	@Test
	public void whenContextProviderNotAvailableShouldCreateContextLoadingActivity() {
		isContextAvailable = false;

		assertTrue(appActivityMapper.getActivity(new PlanningPlace(PROJECT_ID)) instanceof ContextLoadingActivity);
	}

	@Test
	public void whenContextProviderIsAvailableShouldCreatePlanningPlaceActivity() {
		isContextAvailable = true;

		assertTrue(appActivityMapper.getActivity(new PlanningPlace(PROJECT_ID)) instanceof PlanningActivity);
	}

	@Test
	public void whenContextProviderIsAvailableAndProjectIdIsZeroShouldCreateAProjectSelectionActivity() {
		isContextAvailable = true;

		assertTrue(appActivityMapper.getActivity(new PlanningPlace(UUID.INVALID_UUID)) instanceof ProjectSelectionActivity);
	}

	@Test
	public void whenContextProviderIsAvailableAndProjectSelectionPlaceRequestedShouldCreateAProjectSelectionActivity() {
		isContextAvailable = true;

		assertTrue(appActivityMapper.getActivity(new ProjectSelectionPlace()) instanceof ProjectSelectionActivity);
	}

	@Test
	public void activityShouldBeNullWhenPlaceNotInstanceOfPlanningPlaceNorContextLoadingPlaceAndContextAvailable() {
		isContextAvailable = true;
		assertNull(appActivityMapper.getActivity(null));
	}

	@Test
	public void contextAvaliabilityShouldBeCheckedWhenProjectDependentPlaceIsPassed() {
		isContextAvailable = true;

		final ProjectDependentPlace projectDependentPlace = mock(ProjectDependentPlace.class);
		final UUID projectId = new UUID();
		when(projectDependentPlace.getRequestedProjectId()).thenReturn(projectId);
		appActivityMapper.getActivity(projectDependentPlace);

		verify(contextProvider).isContextAvailable(Mockito.any(UUID.class));
	}

	@Test
	public void contextAvaliabilityShouldNotBeCheckedWhenPassedPlaceIsNotAProjectDependentPlace() {
		final ProjectSelectionPlace projectIndependentPlace = mock(ProjectSelectionPlace.class);

		appActivityMapper.getActivity(projectIndependentPlace);

		verify(contextProvider, times(0)).isContextAvailable(Mockito.any(UUID.class));
	}

	@Test
	public void contextShouldBeLoadedForProjectDependentPlaces() {
		isContextAvailable = false;

		final ProjectDependentPlace projectDependentPlace = mock(ProjectDependentPlace.class);
		when(projectDependentPlace.getRequestedProjectId()).thenReturn(new UUID());

		assertTrue(appActivityMapper.getActivity(projectDependentPlace) instanceof ContextLoadingActivity);
	}

	@Test
	public void userDataShouldBeLoadedIfNotPresentWhenAnotherPlaceIsRequested() {
		when(authenticationService.isUserAvailable()).thenReturn(false);
		assertTrue(appActivityMapper.getActivity(new ProjectSelectionPlace()) instanceof UserInformationLoadingActivity);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
