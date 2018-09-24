package edu.kit.ifv.mobitopp.data.areatype;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class BicRepositoryTest {

	@Test
	public void fromValue() {
		for (AreaType expectedType : ZoneAreaType.values()) {
			AreaType typeFromCode = typeFor(expectedType.getTypeAsInt());

			assertThat(typeFromCode, is(expectedType));
		}
	}

	private AreaType typeFor(int code) {
		return new BicRepository().getTypeForCode(code);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForUnknownType() {
		typeFor(-1);
	}

	@Test
	public void fromName() {
		for (ZoneAreaType expectedType : ZoneAreaType.values()) {
			AreaType typeFromName = typeFor(expectedType.getTypeAsString());

			assertThat(typeFromName, is(expectedType));
		}
	}

	private AreaType typeFor(String name) {
		return new BicRepository().getTypeForName(name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForUnknownName() {
		typeFor("unknown");
	}
}
