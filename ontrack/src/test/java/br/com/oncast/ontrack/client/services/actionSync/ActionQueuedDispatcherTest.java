package br.com.oncast.ontrack.client.services.actionSync;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncServiceTestUtils.ValueHolder;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.VoidResult;

public class ActionQueuedDispatcherTest {

	private interface DispatchListener {
		void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, DispatchCallback<VoidResult> callback);
	}

	private final class DispatchRequestServiceTestImplementation implements DispatchService {
		private DispatchListener listener;

		public void registerDispatchListener(final DispatchListener listener) {
			this.listener = listener;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends DispatchRequest<R>, R extends DispatchResponse> void dispatch(final T request, final DispatchCallback<R> dispatchCallback) {
			if (!(request instanceof ModelActionSyncRequest)) throw new RuntimeException("The test should not try to dispatch '" + request.getClass().getName()
					+ "'.");
			if (listener == null) throw new RuntimeException("The listener was not set.");
			listener.onDispatch((ModelActionSyncRequest) request, (DispatchCallback<VoidResult>) dispatchCallback);
		}

		@Override
		public <T extends FailureHandler<R>, R extends Throwable> void addFailureHandler(final Class<R> throwableClass, final T handler) {
			throw new RuntimeException("The test should not use this method.");
		}
	}

	private ActionSyncServiceTestUtils actionSyncServiceTestUtils;
	private DispatchRequestServiceTestImplementation requestDispatchServiceMock;
	private ActionQueuedDispatcher actionQueuedDispatcher;

	@Before
	public void setUp() {
		actionSyncServiceTestUtils = new ActionSyncServiceTestUtils();
		requestDispatchServiceMock = new DispatchRequestServiceTestImplementation();
		actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchServiceMock,
				actionSyncServiceTestUtils.getClientIdentificationProviderMock(),
				actionSyncServiceTestUtils.getProjectRepresentationProviderMock(), actionSyncServiceTestUtils.getErrorTreatmentServiceMock());
	}

	@Test
	public void testQueueingWhileDispatchServiceDoesNotReturn() {
		final ValueHolder<DispatchCallback<VoidResult>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<VoidResult>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<VoidResult> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});
		for (int i = 0; i < 10; i++)
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
}
