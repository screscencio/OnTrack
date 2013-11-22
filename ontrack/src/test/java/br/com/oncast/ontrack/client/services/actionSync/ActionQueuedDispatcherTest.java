package br.com.oncast.ontrack.client.services.actionSync;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

import br.com.oncast.ontrack.client.i18n.ClientMessages;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.DispatchListener;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.DispatchRequestServiceTestImplementation;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.ValueHolder;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@GwtModule("br.com.oncast.ontrack.Application")
public class ActionQueuedDispatcherTest extends GwtTest {

	private ActionQueuedDispatcherTestUtils actionSyncServiceTestUtils;
	private DispatchRequestServiceTestImplementation requestDispatchServiceMock;
	private ActionDispatcher actionQueuedDispatcher;

	@Mock
	private ClientAlertingService alertingService;

	@Mock
	private ClientMessages messages;

	@Mock
	private EventBus eventBus;

	@Mock
	private ClientMetricsService metrics;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		actionSyncServiceTestUtils = new ActionQueuedDispatcherTestUtils();
		requestDispatchServiceMock = actionSyncServiceTestUtils.new DispatchRequestServiceTestImplementation();
		actionQueuedDispatcher = new ActionDispatcher(requestDispatchServiceMock, getProjectRepresentationProviderMock(), metrics, eventBus);
	}

	@Test
	public void shouldQueueWhileDispatchServiceDoesNotReturn() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionDispatcher.class.getSimpleName() + " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});
		for (int i = 0; i < 100; i++)
			dispatch();
	}

	@Test
	public void shouldDispatchWhenDispatchServiceCallbackIsCalled() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionDispatcher.class.getSimpleName() + " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});

		Assert.assertNull("It should not exist any callback registered.", callbackHolder.getValue());

		for (int i = 0; i < 10; i++)
			dispatch();

		Assert.assertNotNull("It should exist a callback registered.", callbackHolder.getValue());

		final DispatchCallback<VoidResult> callback = callbackHolder.getValue();
		callbackHolder.setValue(null);

		Assert.assertNull("It should not exist any callback registered.", callbackHolder.getValue());

		callback.onSuccess(null);

		Assert.assertNotNull("It should exist a callback registered.", callbackHolder.getValue());
	}

	@Test
	public void shouldDispatchAllQueuedActionsWhenDispatchServiceCallbackIsCalled() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		final ValueHolder<ModelActionSyncRequest> request = actionSyncServiceTestUtils.new ValueHolder<ModelActionSyncRequest>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionDispatcher.class.getSimpleName() + " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
				request.setValue(modelActionSyncRequest);
			}
		});

		for (int i = 0; i < 10; i++)
			dispatch();

		Assert.assertEquals("The first action sync request should have only one action.", 1, request.getValue().getActionList().size());

		final DispatchCallback<VoidResult> callback = callbackHolder.getValue();
		callbackHolder.setValue(null);
		callback.onSuccess(null);

		Assert.assertEquals("The second action sync request should have all the remaining actions.", 9, request.getValue().getActionList().size());
	}

	@Test
	public void shouldQueueActionsWhileDispatchServiceCallbackIsNotCalled() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		final ValueHolder<ModelActionSyncRequest> request = actionSyncServiceTestUtils.new ValueHolder<ModelActionSyncRequest>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				callbackHolder.setValue(callback);
				request.setValue(modelActionSyncRequest);
			}
		});

		for (int i = 0; i < 10; i++)
			dispatch();

		Assert.assertEquals("The first action sync request should have only one action.", 1, request.getValue().getActionList().size());

		callbackHolder.getValue().onSuccess(null);

		for (int i = 0; i < 10; i++)
			dispatch();

		Assert.assertEquals("The second action sync request should have 9 actions.", 9, request.getValue().getActionList().size());

		callbackHolder.getValue().onSuccess(null);

		Assert.assertEquals("The second action sync request should have all the remaining actions.", 10, request.getValue().getActionList().size());
	}

	private void dispatch() {
		actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));
	}

	private ProjectRepresentationProvider getProjectRepresentationProviderMock() {
		final ProjectRepresentationProvider provider = mock(ProjectRepresentationProvider.class);
		when(provider.getCurrent()).thenReturn(new ProjectRepresentation(new UUID(), "Default project"));

		return provider;
	}
}
