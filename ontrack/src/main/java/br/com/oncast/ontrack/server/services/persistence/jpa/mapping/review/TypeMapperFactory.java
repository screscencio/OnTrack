package br.com.oncast.ontrack.server.services.persistence.jpa.mapping.review;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.SimpleMapper;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.custom.ListMapper;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.custom.UUIDMapper;


public class TypeMapperFactory {

	public static TypeMapper getInstance() {
		return new ListMapper(new UUIDMapper(new SimpleMapper(null)));
	}

}
