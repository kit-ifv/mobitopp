package edu.kit.ifv.mobitopp.publictransport.model;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FootJourneyTest {

	@Test
	public void doesNotHaveAnyConnections() throws Exception {
		assertThat(FootJourney.footJourney.connections().asCollection(), is(empty()));
	}

	@Test
	public void capacityIsUnlimited() throws Exception {
		assertThat(FootJourney.footJourney.capacity(), is(Integer.MAX_VALUE));
	}

}
