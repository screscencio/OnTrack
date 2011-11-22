package br.com.oncast.ontrack.client.services.actionSync;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionSync.ActionSyncServiceTestUtils.ValueHolder;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;

public class ActionQueuedDispatcherTest {

	private interface DispatchListener {
		void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, DispatchCallback<Void> callback);
	}

	private final class RequestDispatchServiceTestImplementation implements RequestDispatchService {
		private DispatchListener listener;

		@Override
		public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
			if (listener == null) throw new RuntimeException("The listener was not set.");
			listener.onDispatch(modelActionSyncRequest, dispatchCallback);
		}

		@Override
		public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
			throw new RuntimeException("The test should not use this method.");
		}

		@Override
		public void dispatch(final ProjectCreationRequest projectCreationRequest, final DispatchCallback<ProjectRepresentation> dispatchCallback) {
			throw new RuntimeException("The test should not use this method.");
		}

		public void registerDispatchListener(final DispatchListener listener) {
			this.listener = listener;
		}

		@Override
		public void dispatch(final ProjectListRequest projectListRequest, final DispatchCallback<List<ProjectRepresentation>> dispatchCallback) {
			throw new RuntimeException("The test should not use this method.");
		}
	}

	private ActionSyncServiceTestUtils actionSyncServiceTestUtils;
	private RequestDispatchServiceTestImplementation requestDispatchServiceMock;
	private ActionQueuedDispatcher actionQueuedDispatcher;

	@Before
	public void setUp() {
		actionSyncServiceTestUtils = new ActionSyncServiceTestUtils();
		requestDispatchServiceMock = new RequestDispatchServiceTestImplementation();
		actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchServiceMock,
				actionSyncServiceTestUtils.getClientIdentificationProviderMock(),
				actionSyncServiceTestUtils.getProjectRepresentationProviderMock(), actionSyncServiceTestUtils.getErrorTreatmentServiceMock());
	}

	@Test
	public void testQueueingWhileDispatchServiceDoesNotReturn() {
		final ValueHolder<DispatchCallback<Void>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<Void>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> callback) {
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
		final ValueHolder<DispatchCallback<Void>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<Void>>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
			}
		});

		Assert.assertNull("It should not exist any callback registered.", callbackHolder.getValue());

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertNotNull("It should exist a callback registered.", callbackHolder.getValue());

		final DispatchCallback<Void> callback = callbackHolder.getValue();
		callbackHolder.setValue(null);

		Assert.assertNull("It should not exist any callback registered.", callbackHolder.getValue());

		callback.onRequestCompletition(null);

		Assert.assertNotNull("It should exist a callback registered.", callbackHolder.getValue());
	}

	@Test
	public void shouldDispatchAllQueuedActionsWhenDispatchServiceCallbackIsCalled() {
		final ValueHolder<DispatchCallback<Void>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<Void>>(null);
		final ValueHolder<ModelActionSyncRequest> request = actionSyncServiceTestUtils.new ValueHolder<ModelActionSyncRequest>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> callback) {
				if (callbackHolder.getValue() != null) Assert.fail(ActionQueuedDispatcher.class.getSimpleName()
						+ " should only make one call at a time to the request dispatch service.");
				callbackHolder.setValue(callback);
				request.setValue(modelActionSyncRequest);
			}
		});

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertEquals("The first action sync request should have only one action.", 1, request.getValue().getActionList().size());

		final DispatchCallback<Void> callback = callbackHolder.getValue();
		callbackHolder.setValue(null);
		callback.onRequestCompletition(null);

		Assert.assertEquals("The second action sync request should have all the remaining actions.", 9, request.getValue().getActionList().size());
	}

	@Test
	public void shouldQueueActionsWhileDispatchServiceCallbackIsNotCalled() {
		final ValueHolder<DispatchCallback<Void>> callbackHolder = actionSyncServiceTestUtils.new ValueHolder<DispatchCallback<Void>>(null);
		final ValueHolder<ModelActionSyncRequest> request = actionSyncServiceTestUtils.new ValueHolder<ModelActionSyncRequest>(null);
		requestDispatchServiceMock.registerDispatchListener(new DispatchListener() {

			@Override
			public void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> callback) {
				callbackHolder.setValue(callback);
				request.setValue(modelActionSyncRequest);
			}
		});

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertEquals("The first action sync request should have only one action.", 1, request.getValue().getActionList().size());

		callbackHolder.getValue().onRequestCompletition(null);

		for (int i = 0; i < 10; i++)
			actionQueuedDispatcher.dispatch(new ScopeUpdateAction(new UUID(), ""));

		Assert.assertEquals("The second action sync request should have 9 actions.", 9, request.getValue().getActionList().size());

		callbackHolder.getValue().onRequestCompletition(null);

		Assert.assertEquals("The second action sync request should have all the remaining actions.", 10, request.getValue().getActionList().size());
	}
}
