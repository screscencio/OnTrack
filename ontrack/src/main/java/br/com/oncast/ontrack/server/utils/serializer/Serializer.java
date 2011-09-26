package br.com.oncast.ontrack.server.utils.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serializer {

	public static byte[] serialize(final Serializable serializable) throws IOException {
		final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		final ObjectOutputStream outputObject = new ObjectOutputStream(byteArray);
		outputObject.writeObject(serializable);
		outputObject.close();

		return byteArray.toByteArray();
	}

	public static Object deserialize(final byte[] byteBuffer) throws IOException, ClassNotFoundException {
		if (byteBuffer == null) throw new NullPointerException("An empty buffer cannot be deserialized.");

		final ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(byteBuffer));
		return objectIn.readObject();
	}

}
