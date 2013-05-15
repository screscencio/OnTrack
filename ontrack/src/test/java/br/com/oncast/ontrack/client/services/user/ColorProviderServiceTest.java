package br.com.oncast.ontrack.client.services.user;

import static br.com.oncast.ontrack.client.utils.date.DateUnit.DAY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.estimator.ScopeEstimatorProvider;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.utils.ColorUtil;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressTestUtils;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.ScopeEstimator;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.web.bindery.event.shared.EventBus;

public class ColorProviderServiceTest {

	private ColorProviderService service;

	private Scope scope;

	@Mock
	private DispatchService requestDispatchService;

	@Mock
	private ContextProviderService contextProviderService;

	@Mock
	private ServerPushClientService serverPushClientService;

	@Mock
	private EventBus eventBus;

	@Mock
	private ColorPicker colorPicker;

	@Mock
	private ColorPackPicker colorPackPicker;

	@Mock
	private ScopeEstimator scopeEstimator;

	@Mock
	private UsersStatusService usersStatusServiceImpl;

	@Mock
	private ScopeEstimatorProvider scopeEstimatorProvider;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		scope = spy(ScopeTestUtils.createScope());
		service = new ColorProviderServiceImpl(requestDispatchService, contextProviderService, scopeEstimatorProvider, serverPushClientService, eventBus,
				usersStatusServiceImpl,
				colorPicker, colorPackPicker);
		when(scopeEstimatorProvider.get()).thenReturn(scopeEstimator);
		when(scope.hasDueDate()).thenReturn(true);
	}

	@Test
	public void theScopeDueDateColorShouldBeTransparentWhenTheScopeDoesNotHaveDueDate() throws Exception {
		when(scope.hasDueDate()).thenReturn(false);
		assertEquals(Color.TRANSPARENT, service.getDueDateColor(scope));
	}

	@Test
	public void theScopeDueDateColorShouldBeTransparentWhenTodayIsEarlierThanScopeDueDateMinusTwoTimesTheScopeDuration() throws Exception {
		final long duration = 1 * DAY;
		setScopeDurationAndRemainingTime(duration, 2 * duration + 1);
		assertEquals(Color.TRANSPARENT, service.getDueDateColor(scope));
	}

	@Test
	public void theScopeDueDateColorShouldBeCompletelyYellowWhenTodayIsExactlyTheScopeDueDateMinusTwoTimesTheScopeDuration() throws Exception {
		final long duration = 1 * DAY;

		setScopeDurationAndRemainingTime(duration, 2 * duration);
		assertEquals(Color.YELLOW, service.getDueDateColor(scope));
	}

	@Test
	public void theScopeDueDateColorShouldBeCompletelyRedWhenTodayIsExactlyTheScopeDueDateMinusTheScopeDuration() throws Exception {
		final long duration = 1 * DAY;

		setScopeDurationAndRemainingTime(duration, duration);
		assertEquals(Color.RED, service.getDueDateColor(scope));
	}

	@Test
	public void theScopeDueDateColorShouldBeCompletelyRedWhenTodayIsLaterThanTheScopeDueDateMinusTheScopeDuration() throws Exception {
		final long duration = 3 * DAY;

		setScopeDurationAndRemainingTime(duration, 1 * DAY);
		assertEquals(Color.RED, service.getDueDateColor(scope));
	}

	@Test
	public void theScopeDueDateColorShouldGraduallyChangeFromYellowToRed() throws Exception {
		final long duration = 1 * DAY;

		setScopeDurationAndRemainingTime(duration, (long) (1.5 * duration));
		assertEquals(ColorUtil.getTransitionColor(Color.YELLOW, Color.RED, .5), service.getDueDateColor(scope));
	}

	@Test
	public void whenTheScopeProgressIsDoneTheScopeDueDateColorShouldBeTransparent() throws Exception {
		ProgressTestUtils.setProgressState(scope, ProgressState.DONE);
		assertEquals(Color.TRANSPARENT, service.getDueDateColor(scope));
		setScopeDurationAndRemainingTime(1 * DAY, 2 * DAY);
		assertEquals(Color.TRANSPARENT, service.getDueDateColor(scope));
		setScopeDurationAndRemainingTime(3 * DAY, 1 * DAY);
		assertEquals(Color.TRANSPARENT, service.getDueDateColor(scope));
	}

	private void setScopeDurationAndRemainingTime(final long scopeDuration, final long remainingTime) {
		when(scopeEstimator.getDuration(scope)).thenReturn(scopeDuration);
		when(scopeEstimator.getRemainingTime(scope)).thenReturn(remainingTime);
	}

}
