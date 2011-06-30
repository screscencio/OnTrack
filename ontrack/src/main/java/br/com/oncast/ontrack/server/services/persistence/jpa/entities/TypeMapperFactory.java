package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

public class TypeMapperFactory {

	public static TypeMapper getInstance() {
		return new ListMapper(new UUIDMapper(new SimpleMapper(null)));
	}

}
