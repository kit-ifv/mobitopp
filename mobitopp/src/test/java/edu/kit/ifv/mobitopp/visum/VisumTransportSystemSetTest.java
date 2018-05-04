package edu.kit.ifv.mobitopp.visum;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class VisumTransportSystemSetTest {

	private static final String firstCode = "first";
	private static final String secondCode = "second";
	private VisumTransportSystem first;
	private VisumTransportSystem second;
	private VisumTransportSystems systems;
	private String setCode;

	@Before
	public void initialise() {
		first = first();
		second = second();
		systems = new VisumTransportSystems(systems());
		setCode = firstCode + VisumTransportSystemSet.delimiter + secondCode;
	}

	@Test
	public void createSet() {
		VisumTransportSystemSet set = VisumTransportSystemSet.getByCode(setCode, systems);

		assertThat(set.transportSystems, contains(first, second));
	}

	@Test
	public void serialiseSet() {
		VisumTransportSystemSet set = VisumTransportSystemSet.getByCode(setCode, systems);

		String serialisedSet = set.serialise();

		assertThat(serialisedSet, is(equalTo(setCode)));
	}

	private Map<String, VisumTransportSystem> systems() {
		HashMap<String, VisumTransportSystem> systems = new HashMap<>();
		systems.put(first.code, first);
		systems.put(second.code, second);
		return systems;
	}

	private VisumTransportSystem second() {
		return newSystem(secondCode);
	}

	private VisumTransportSystem first() {
		return newSystem(firstCode);
	}

	private VisumTransportSystem newSystem(String firstt) {
		return new VisumTransportSystem(firstt, firstt, firstt);
	}
}
