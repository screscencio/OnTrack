package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class FileRepresentationTestUtils {

	private static final Long DEFAULT_PROJECT = 1L;
	private static int fileRepresentationCounter = 0;

	public static FileRepresentation create() {
		final int counter = fileRepresentationCounter++;
		return new FileRepresentation("file_" + counter + ".extension", "path/to/file/" + counter, new UUID(String.valueOf(DEFAULT_PROJECT)));
	}

}
