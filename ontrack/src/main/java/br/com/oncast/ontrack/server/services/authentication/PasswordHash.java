package br.com.oncast.ontrack.server.services.authentication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import com.google.gwt.user.server.Base64Utils;

public class PasswordHash {
	private final byte[] bytesHash;
	private final byte[] bytesSalt;

	/**
	 * Constructor for a new password hash. A password salt is randomly generated to better protect the password.
	 * @param password the raw password.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public PasswordHash(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this(password, generateSalt());
	}

	/**
	 * Constructor for a password hash with a known salt. Intended to be used to reconstruct a hash for authentication.
	 * @param password the raw password.
	 * @param passwordSalt the known salt.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public PasswordHash(final String password, final String passwordSalt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this(password, Base64Utils.fromBase64(passwordSalt));
	}

	private PasswordHash(final String password, final byte[] bSalt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.bytesSalt = bSalt;
		bytesHash = getHash(password, bSalt);
	}

	private static byte[] generateSalt() throws NoSuchAlgorithmException {
		final byte[] bSalt = new byte[8];
		final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.nextBytes(bSalt);
		return bSalt;
	}

	private byte[] getHash(final String password, final byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		final MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		digest.reset();
		input = digest.digest(input);
		return input;
	}

	public String getPasswordHash() {
		return Base64Utils.toBase64(bytesHash);
	}

	public String getPasswordSalt() {
		return Base64Utils.toBase64(bytesSalt);
	}

	public boolean compareAgainst(final String passwordHash) {
		return Arrays.equals(bytesHash, Base64Utils.fromBase64(passwordHash));
	}

}
