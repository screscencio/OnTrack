package br.com.oncast.ontrack.shared.model.release;

public class ReleaseDescriptionParser {

	private static final int INDEX_NOT_FOUND = -1;
	public static final String SEPARATOR = Release.SEPARATOR;

	private final String description;
	// TODO Remove fullDescripionOfTheHead and simplify code if ReleaseCreator stop using it
	private String fullDescriptionOfTheHead;

	private String head;
	private int lastSeparatorIndex = 0;

	public ReleaseDescriptionParser(final String releaseDescription) {
		description = releaseDescription;
		fullDescriptionOfTheHead = "";
		head = extractHead();
	}

	public String getFullDescriptionOfHeadRelease() {
		if (lastSeparatorIndex == INDEX_NOT_FOUND) return "";
		return fullDescriptionOfTheHead;
	}

	public String getHeadRelease() {
		if (lastSeparatorIndex == INDEX_NOT_FOUND) return "";
		return head;
	}

	public String getTailReleases() {
		if (lastSeparatorIndex == INDEX_NOT_FOUND) return "";
		final String tail = containsSeparator() ? description.substring(getNextSeparatorIndex() + SEPARATOR.length()) : "";
		return tail.trim();
	}

	public boolean hasNext() {
		final int nextSeparatorIndex = getNextSeparatorIndex();
		return (nextSeparatorIndex != INDEX_NOT_FOUND && hasMeaningfulTextAfter(nextSeparatorIndex));
	}

	// Meaningful text is anything other than separators.
	private boolean hasMeaningfulTextAfter(final int separatorIndex) {
		final int indexAfterSeparator = separatorIndex + 1;
		if (indexAfterSeparator >= description.length()) return false;

		final String textAfter = description.substring(indexAfterSeparator).trim();
		if (removeSeparatorsFrom(textAfter).isEmpty()) return false;

		return true;
	}

	private String removeSeparatorsFrom(final String text) {
		if (text == null || text.trim().isEmpty()) return text;
		return text.replace(SEPARATOR, "").trim();
	}

	public boolean next() {
		if (!hasNext()) {
			lastSeparatorIndex = INDEX_NOT_FOUND;
			return false;
		}
		lastSeparatorIndex = getNextSeparatorIndex();
		if (lastSeparatorIndex != INDEX_NOT_FOUND) increaseLastSeparatorIndex();
		head = extractHead();
		return lastSeparatorIndex != INDEX_NOT_FOUND || lastSeparatorIndex >= description.length();
	}

	private void increaseLastSeparatorIndex() {
		lastSeparatorIndex++;
		if (description.substring(lastSeparatorIndex).trim().startsWith(SEPARATOR)) lastSeparatorIndex = getNextSeparatorIndex() + 1;
	}

	private String extractHead() {
		String extractedHead = getFirstHead();
		while (extractedHead.isEmpty() && next()) {
			extractedHead = getFirstHead();
		}
		fullDescriptionOfTheHead += getNeededSeparator() + extractedHead;
		return extractedHead;
	}

	private String getFirstHead() {
		final int separatorIndex = getNextSeparatorIndex();
		final String firstHead = separatorIndex == INDEX_NOT_FOUND ? description.substring(lastSeparatorIndex) : description.substring(lastSeparatorIndex,
				separatorIndex);
		return firstHead.trim();
	}

	private int getNextSeparatorIndex() {
		if (lastSeparatorIndex == INDEX_NOT_FOUND) return INDEX_NOT_FOUND;
		return description.indexOf(SEPARATOR, lastSeparatorIndex);
	}

	private boolean containsSeparator() {
		return description.contains(SEPARATOR);
	}

	private String getNeededSeparator() {
		return !fullDescriptionOfTheHead.isEmpty() ? SEPARATOR : "";
	}

}
