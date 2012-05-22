package net.zschech.gwt.comet.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * @author Icky from https://groups.google.com/forum/?fromgroups#!topic/gwt-comet/NkfGh6-nREM
 * Workaround for IE 9 Security rule 
 */
public class IEXDRCometTransport extends RawDataCometTransport {
	private static final String SEPARATOR = "\n";

	private int read;
	private XDomainRequest transportRequest;
	private XDomainRequestListener xDomainRequestListener = new XDomainRequestListener() {

		@Override
		public void onError(XDomainRequest request) {
			if (isCurrent(request)) {
				expectingDisconnection = true;
				listener.onError(new StatusCodeException(Response.SC_INTERNAL_SERVER_ERROR, ""), true);
				transportRequest = null;
			}
		}

		@Override
		public void onLoad(XDomainRequest request, String responseText) {
			request.clearListener();
			if (isCurrent(request)) {
				transportRequest = null;
				if (!disconnecting) {
					onReceiving(Response.SC_OK, responseText, false);
				}
			}
		}

		@Override
		public void onProgress(XDomainRequest request, String responseText) {
			if (!disconnecting && isCurrent(request)) {
				onReceiving(Response.SC_OK, responseText, true);
			} else {
				request.clearListener();
				request.abort();
				if (isCurrent(request)) {
					transportRequest = null;
				}
			}
		}

		@Override
		public void onTimeout(XDomainRequest request) {
			if (isCurrent(request)) {
				if (!expectingDisconnection) {
					listener.onError(new RequestException( "Unexpected connection timeout " + request.getTimeout()), false);
				}
			}
		}

		public boolean isCurrent(XDomainRequest request) {
			return request == transportRequest;
		}
	};

	@Override
	public void connect(int connectionCount) {
		super.connect(connectionCount);
		read = 0;
		transportRequest = XDomainRequest.create();
		try {
			transportRequest.setListener(xDomainRequestListener);
			transportRequest.openGET(getUrl(connectionCount));
			transportRequest.send();

		} catch (JavaScriptException ex) {
			if (transportRequest != null) {
				transportRequest.abort();
				transportRequest = null;
			}
			listener.onError(new RequestException(ex.getMessage()), false);
		}
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if (transportRequest != null) {
			transportRequest.clearListener();
			transportRequest.abort();
			transportRequest = null;
		}
		listener.onDisconnected();
	}

	private void onReceiving(int statusCode, String responseText,
			boolean connected) {
		if (statusCode != Response.SC_OK) {
			if (!connected) {
				super.disconnect();
				listener.onError(new StatusCodeException(statusCode, responseText), connected);
			}
		} else {
			int index = responseText.lastIndexOf(SEPARATOR);
			if (index > read) {
				List<Serializable> messages = new ArrayList<Serializable>();

				JsArrayString data = split(responseText.substring(read, index), SEPARATOR);
				int length = data.length();
				for (int i = 0; i < length; i++) {
					if (disconnecting) {
						return;
					}

					String message = data.get(i);
					if (!message.isEmpty()) {
						parse(message, messages);
					}
				}
				read = index + 1;
				if (!messages.isEmpty()) {
					listener.onMessage(messages);
				}
			}

			if (!connected) {
				super.disconnected();
			}
		}
	}

	native static JsArrayString split(String string, String separator) /*-{ 
																		     return string.split(separator); 
																		}-*/;

	static String unescape(String string) {
		return string.replace("\\n", "\n").replace("\\\\", "\\");
	}
}
