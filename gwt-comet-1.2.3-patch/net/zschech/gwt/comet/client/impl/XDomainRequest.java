package net.zschech.gwt.comet.client.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Icky from https://groups.google.com/forum/?fromgroups#!topic/gwt-comet/NkfGh6-nREM
 * Workaround for IE 9 Security rule 
 */
public class XDomainRequest extends JavaScriptObject {

	public static native XDomainRequest create() /*-{ 
		// XDomainRequest object does not play well with GWT JavaScriptObject so store in local variable 
		var me = new Object(); 
		
		me.request = new XDomainRequest(); 
		return me; 
	}-*/;

	public native static boolean isSupported() /*-{ 
		if ($wnd.XDomainRequest) { 
			return true; 
		} else { 
			return false; 
		} 
	}-*/;

	public final native void setListener(XDomainRequestListener listener) /*-{ 
	    var self = this; 
	    
	    this.request.onload = function() { 
			listener.@net.zschech.gwt.comet.client.impl.XDomainRequestListener::onLoad(Lnet/zschech/gwt/comet/client/impl/XDomainRequest;Ljava/lang/String;) 
			(self,self.request.responseText); 
	    }; 
	    
	    this.request.onprogress = function() {
			listener.@net.zschech.gwt.comet.client.impl.XDomainRequestListener::onProgress(Lnet/zschech/gwt/comet/client/impl/XDomainRequest;Ljava/lang/String;) 
			(self,self.request.responseText); 
		}; 
	    
	    this.request.ontimeout = function() { 
			listener.@net.zschech.gwt.comet.client.impl.XDomainRequestListener::onTimeout(Lnet/zschech/gwt/comet/client/impl/XDomainRequest;)
			(self); 
	    }; 
	    this.request.onerror = function() { 
			listener.@net.zschech.gwt.comet.client.impl.XDomainRequestListener::onError(Lnet/zschech/gwt/comet/client/impl/XDomainRequest;)
			(self); 
	    }; 
	}-*/;

	public final native void clearListener() /*-{ 
		 var self = this; 
		 
		 // Using a function literal here leaks memory on ie6 
		 // Using the same function object kills HtmlUnit 
		 $wnd.setTimeout(function() { 
			 self.request.onload = new Function(); 
			 self.request.onprogress = new Function(); 
			 self.request.ontimeout = new Function(); 
			 self.request.onerror = new Function(); 
		 }, 0); 
	 }-*/;

	public final native String getContentType() /*-{ 
		return this.request.contentType; 
	}-*/;

	/**
	 * 
	 * @return the body of the response returned by the server.
	 */
	public final native String getResponseText() /*-{ 
		return this.request.responseText; 
	}-*/;

	/**
	 * set the timeout in milliseconds
	 * 
	 * @param timeout
	 */
	public final native void setTimeout(int timeout) /*-{ 
		this.request.timeout = timeout; 
	}-*/;

	public final native int getTimeout() /*-{ 
		return this.request.timeout; 
	}-*/;

	/**
	 * The abort method terminates a pending send.
	 */
	public final native void abort() /*-{ 
		this.request.abort(); 
	}-*/;

	/**
	 * Creates a connection with a domain's server.
	 * 
	 * @param url
	 */
	public final native void openGET(String url) /*-{ 
		this.request.open("GET", url); 
	}-*/;

	/**
	 * Creates a connection with a domain's server.
	 * 
	 * @param url
	 */
	public final native void openPOST(String url) /*-{ 
		this.request.open("POST", url); 
	}-*/;

	/**
	 * Transmits a empty string to the server for processing.
	 */
	public final native void send() /*-{ 
		this.request.send(); 
	}-*/;

	/**
	 * Transmits a data string to the server for processing.
	 * 
	 * @param data
	 */
	public final native void send(String data) /*-{ 
		this.request.send(data); 
	}-*/;

	protected XDomainRequest() {}
}