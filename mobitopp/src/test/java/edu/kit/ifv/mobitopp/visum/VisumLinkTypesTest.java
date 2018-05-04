package edu.kit.ifv.mobitopp.visum;

import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper;

public class VisumLinkTypesTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private int id;
	private VisumLinkType linkType;

	@Before
	public void initialise() {
		id = 0;
		String name = "name";
		VisumTransportSystemSet systemSet = TransportSystemHelper.dummySet();
		int numberOfLanes = 1;
		int capacityCar = 2;
		int freeFlowSpeedCar = 3;
		int walkSpeed = 4;
		linkType = new VisumLinkType(id, name, systemSet, numberOfLanes, capacityCar, freeFlowSpeedCar,
				walkSpeed);
	}

	@Test
	public void getBy() {
		VisumLinkTypes types = new VisumLinkTypes(types());

		VisumLinkType type = types.getById(id);

		assertThat(type, is(equalTo(linkType)));
	}

	private Map<Integer, VisumLinkType> types() {
		Map<Integer, VisumLinkType> types = new HashMap<>();
		types.put(linkType.id, linkType);
		return types;
	}

	@Test
	public void getMissing() {
		VisumLinkTypes types = new VisumLinkTypes(emptyMap());

		thrown.expect(IllegalArgumentException.class);
		types.getById(id);
	}
}
