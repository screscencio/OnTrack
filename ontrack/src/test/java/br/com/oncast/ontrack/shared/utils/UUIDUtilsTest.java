package br.com.oncast.ontrack.shared.utils;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UUIDUtilsTest {

	private UUID uuid;

	@Before
	public void setup() throws Exception {
		uuid = new UUID();
	}

	@Test
	public void sameInstancesAreEqual() {
		final Object obj = new String();
		assertTrue(UUIDUtils.equals(obj, obj));
	}

	@Test
	public void equivalentUUIDsAreEqual() throws Exception {
		assertTrue(UUIDUtils.equals(uuid, createEquivalent(uuid)));
	}

	@Test
	public void anObjectThatHasSameUUIDIsEquailToTheUUID() throws Exception {
		assertTrue(UUIDUtils.equals(uuid, new HasUUID() {
			@Override
			public UUID getId() {
				return uuid;
			}
		}));
	}

	@Test
	public void anObjectThatHasEquivalentUUIDIsEquailToTheUUID() throws Exception {
		assertTrue(UUIDUtils.equals(uuid, new HasUUID() {
			@Override
			public UUID getId() {
				return createEquivalent(uuid);
			}
		}));
	}

	@Test
	public void anyNullArgmentIsDifferent() throws Exception {
		assertFalse(UUIDUtils.equals(null, null));
		assertFalse(UUIDUtils.equals(null, uuid));
		assertFalse(UUIDUtils.equals(new Object(), null));
	}

	@Test
	public void anyObjectThatHasNullUUIDIsDifferent() throws Exception {
		assertFalse(UUIDUtils.equals(uuid, new HasUUID() {
			@Override
			public UUID getId() {
				return null;
			}
		}));

		assertFalse(UUIDUtils.equals(new HasUUID() {
			@Override
			public UUID getId() {
				return null;
			}
		}, uuid));

		assertFalse(UUIDUtils.equals(new HasUUID() {

			@Override
			public UUID getId() {
				return null;
			}
		}, new HasUUID() {
			@Override
			public UUID getId() {
				return null;
			}
		}));
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotBeAbleToGenerateHashCodeFromNull() throws Exception {
		UUIDUtils.hashCode(null);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotBeAbleToGenerateHashCodeFromAnObjectThasHasNullUUID() throws Exception {
		UUIDUtils.hashCode(new HasUUID() {
			@Override
			public UUID getId() {
				return null;
			}
		});
	}

	@Test
	public void hasCodeOfAUUIDShouldBeTheUUIDsHashCodeItself() throws Exception {
		assertEquals(uuid.hashCode(), UUIDUtils.hashCode(uuid));
	}

	@Test
	public void hasCodeOfAnObjectThatHasUUIDShouldBeTheObtainedUUIDsHashCode() throws Exception {
		assertEquals(uuid.hashCode(), UUIDUtils.hashCode(new HasUUID() {
			@Override
			public UUID getId() {
				return uuid;
			}
		}));
	}

	private UUID createEquivalent(final UUID id) {
		return new UUID(id.toString());
	}

}
