package br.com.oncast.ontrack.client.services.user;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserDataServiceImpl implements UserDataService {

	private final Map<String, PortableContactJsonObject> cachedResults;

	public UserDataServiceImpl() {
		cachedResults = new HashMap<String, PortableContactJsonObject>();
	}

	@Override
	public SafeUri getAvatarUrl(final String email) {
		return new SafeUri() {
			@Override
			public String asString() {
				try {
					return new String("http://www.gravatar.com/avatar/" + getMd5Hex(email) + "?s=40&d=mm");
				}
				catch (final Exception e) {
					return null;
				}
			}

		};
	}

	@Override
	public void loadProfile(final String email, final LoadProfileCallback userNameCallback) {
		if (cachedResults.containsKey(email)) {
			userNameCallback.onProfileLoaded(cachedResults.get(email));
			return;
		}

		new JsonpRequestBuilder().requestObject(URL.encode("http://www.gravatar.com/" + getMd5Hex(email) + ".json"),
				new AsyncCallback<PortableContactJsonObject>() {

					@Override
					public void onFailure(final Throwable throwable) {
						userNameCallback.onProfileUnavailable(throwable);
					}

					@Override
					public void onSuccess(final PortableContactJsonObject result) {
						cachedResults.put(email, result);
						userNameCallback.onProfileLoaded(result);
					}

				});
	}

	private String getMd5Hex(final String email) {
		try {
			final BigInteger hash = new BigInteger(1, MessageDigest.getInstance("MD5").digest(email.trim().toLowerCase().getBytes()));
			final String md5 = hash.toString(16);
			return md5;
		}
		catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
