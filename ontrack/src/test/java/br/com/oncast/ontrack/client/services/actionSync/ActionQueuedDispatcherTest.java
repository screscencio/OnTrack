package br.com.oncast.ontrack.client.services.actionSync;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.DispatchListener;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.DispatchRequestServiceTestImplementation;
import br.com.oncast.ontrack.client.services.actionSync.ActionQueuedDispatcherTestUtils.ValueHolder;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

// TODO Refactor this test for better readability
public class ActionQueuedDispatcherTest {

	private ActionQueuedDispatcherTestUtils actionSyncServiceTestUtils;
	private DispatchRequestServiceTestImplementation requestDispatchServiceMock;
	private ActionQueuedDispatcher actionQueuedDispatcher;

	@Mock
	private ClientAlertingService alertingService;

	@Before
	public void setUp() {
		actionSyncServiceTestUtils = new ActionQueuedDispatcherTestUtils();
		requestDispatchServiceMock = actionSyncServiceTestUtils.new DispatchRequestServiceTestImplementation();
		actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchServiceMock,
				getProjectRepresentationProviderMock(),
				alertingService);
	}

	@Test
	public void shouldQueueWhileDispatchServiceDoesNotReturn() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});
		for (int i = 0; i < 100; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));
	}

	@Test
	public void shouldDispatchWhenDispatchServiceCallbackIsCalled() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});

		Assert.assertNull("It should not exist any callback registered.", callbackHolder.getValue());

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

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
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
				request.setValue(modelActionSyncRequest);
			}
		});

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

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
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertEquals("The first action sync request should have only one action.", 1, request.getValue().getActionList().size());

		callbackHolder.getValue().onSuccess(null);

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertEquals("The second action sync request should have 9 actions.", 9, request.getValue().getActionList().size());

		callbackHolder.getValue().onSuccess(null);

		Assert.assertEquals("The second action sync request should have all the remaining actions.", 10, request.getValue().getActionList().size());
	}

	private ProjectRepresentationProvider getProjectRepresentationProviderMock() {
		final ProjectRepresentationProvider provider = mock(ProjectRepresentationProvider.class);
		when(provider.getCurrent()).thenReturn(new ProjectRepresentation(new UUID(), "Default project"));

		return provider;
	}
}
