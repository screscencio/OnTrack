package br.com.oncast.ontrack.shared.model.release;

public class ReleaseDescriptionParser {

	private static final int NOT_FOUND_INDEX = -1;
	private static final String SEPARATOR = Release.SEPARATOR;

	private final String description;
	// TODO Remove fullDescripionOfTheHead and simplify code if ReleaseCreator stop using it
	private String fullDescriptionOfTheHead;

	private String head;
	private int lastSeparatorIndex = 0;
	private boolean dirty;

	public ReleaseDescriptionParser(final String releaseDescription) {
		description = releaseDescription;
		fullDescriptionOfTheHead = "";
		dirty = true;
	}

	public String getFullDescriptionOfHeadRelease() {
		if (lastSeparatorIndex == NOT_FOUND_INDEX) return "";
		return fullDescriptionOfTheHead;
	}

	public String getHeadRelease() {
		if (lastSeparatorIndex == NOT_FOUND_INDEX) return "";
		return dirty ? head = extractHead() : head;
	}

	public String getTailReleases() {
		if (lastSeparatorIndex == NOT_FOUND_INDEX) return "";
		final String tail = containsSeparator() ? description.substring(getNextSeparatorIndex() + SEPARATOR.length()) : "";
		return tail.trim();
	}

	public boolean hasHeadRelease() {
		return getHeadRelease() != "";
	}

	public boolean next() {
		if (lastSeparatorIndex == NOT_FOUND_INDEX) return false;
		dirty = true;
		lastSeparatorIndex = getNextSeparatorIndex();
		if (lastSeparatorIndex != NOT_FOUND_INDEX) lastSeparatorIndex++;
		return lastSeparatorIndex != NOT_FOUND_INDEX || lastSeparatorIndex >= description.length();
	}

	private String extractHead() {
		String head = getFirstHead();
		while (head.isEmpty() && next()) {
			head = getFirstHead();
		}
		dirty = false;
		fullDescriptionOfTheHead += getNeededSeparator() + head;
		return head;
	}

	private String getFirstHead() {
		final int separatorIndex = getNextSeparatorIndex();
		final String head = separatorIndex == NOT_FOUND_INDEX ? description.substring(lastSeparatorIndex) : description.substring(lastSeparatorIndex,
				separatorIndex);
		return head.trim();
	}

	private int getNextSeparatorIndex() {
		return description.indexOf(SEPARATOR, lastSeparatorIndex);
	}

	private boolean containsSeparator() {
		return description.contains(SEPARATOR);
	}

	private String getNeededSeparator() {
		return !fullDescriptionOfTheHead.isEmpty() ? SEPARATOR : "";
	}

}
