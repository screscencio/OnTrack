package br.com.oncast.ontrack.shared.model.timesheet;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UuidAssociation implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id1;
	private UUID id2;

	protected UuidAssociation() {}

	public UuidAssociation(final UUID id1, final UUID id2) {
		this.id1 = id1;
		this.id2 = id2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id1 == null) ? 0 : id1.hashCode());
		result = prime * result + ((id2 == null) ? 0 : id2.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final UuidAssociation other = (UuidAssociation) obj;
		if (id1 == null) {
			if (other.id1 != null) return false;
		}
		else if (!id1.equals(other.id1)) return false;
		if (id2 == null) {
			if (other.id2 != null) return false;
		}
		else if (!id2.equals(other.id2)) return false;
		return true;
	}
}
