package br.com.oncast.ontrack.shared.model.checklist;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Checklist implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private UUID id;

	protected Checklist() {}

	public Checklist(final UUID id, final String title) {
		this.title = title;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public UUID getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Checklist other = (Checklist) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

}
