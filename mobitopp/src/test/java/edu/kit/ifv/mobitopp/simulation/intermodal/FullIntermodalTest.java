package edu.kit.ifv.mobitopp.simulation.intermodal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class FullIntermodalTest {

	@Test
	void doesNotSupportLegMode() throws Exception {
		FullIntermodal mode = new FullIntermodal(StandardMode.PEDELEC, StandardMode.PUBLICTRANSPORT,
			StandardMode.PEDESTRIAN);

		assertThrows(UnsupportedOperationException.class, mode::legMode);
	}

}
