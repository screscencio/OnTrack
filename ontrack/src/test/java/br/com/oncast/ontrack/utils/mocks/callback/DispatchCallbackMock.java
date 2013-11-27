package br.com.oncast.ontrack.utils.mocks.callback;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

@SuppressWarnings("rawtypes")
public class DispatchCallbackMock {

	/**
	 * Use this with caution. It appears that you can only mock one callback at a time.
	 */
	public static <T extends DispatchResponse> Stubber callOnSuccessWith(final T t) {
		return Mockito.doAnswer(new Answer<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final Object[] args = invocationOnMock.getArguments();
				((DispatchCallback) args[args.length - 1]).onSuccess(t);
				return null;
			}
		});
	}

	public static Stubber callOnFailureWith(final Throwable data) {
		return Mockito.doAnswer(new Answer<Throwable>() {
			@Override
			public Throwable answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final Object[] args = invocationOnMock.getArguments();
				((DispatchCallback) args[args.length - 1]).onTreatedFailure(data);
				return null;
			}
		});
	}
}