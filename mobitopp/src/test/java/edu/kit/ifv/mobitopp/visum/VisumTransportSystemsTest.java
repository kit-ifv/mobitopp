package edu.kit.ifv.mobitopp.visum;

import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VisumTransportSystemsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getByCode() {
		Map<String, VisumTransportSystem> transportSystems = new HashMap<>();
		String code = "code-1";
		VisumTransportSystem system = new VisumTransportSystem(code, "name-1", "type-1");
		transportSystems.put(code, system);
		VisumTransportSystems systems = new VisumTransportSystems(transportSystems);

		boolean systemExists = systems.containsFor(code);
		VisumTransportSystem foundSystem = systems.getBy(code);

		assertTrue(systemExists);
		assertThat(foundSystem, is(equalTo(system)));
	}

	@Test
	public void getMissing() {
		String code = "code-1";
		VisumTransportSystems systems = new VisumTransportSystems(emptyMap());

		assertFalse(systems.containsFor(code));
		thrown.expect(IllegalArgumentException.class);
		systems.getBy(code);
	}
}
