package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.CommutationTicketModelStuttgart.Landkreis;

public class CommutationTicketModelStuttgartTest {

	@Test
	public void verifyLandkreisLB() {
		int forExampleId = 11111;
		verify(Landkreis.LB, forExampleId);
	}

	@Test
	public void verifyLandkreisS() {
		int forExampleId = 21111;
		verify(Landkreis.S, forExampleId);
	}

	@Test
	public void verifyLandkreisWN() {
		int forExampleId = 31111;
		verify(Landkreis.WN, forExampleId);
	}

	@Test
	public void verifyLandkreisBB() {
		int forExampleId = 41111;
		verify(Landkreis.BB, forExampleId);
	}

	@Test
	public void verifyLandkreisES() {
		int forExampleId = 51111;
		verify(Landkreis.ES, forExampleId);
	}

	@Test
	public void verifyLandkreisGP() {
		int forExampleId = 61111;
		verify(Landkreis.GP, forExampleId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifyZoneWithoutLandkreis() {
		int outerZoneId = 111111;
		Zone zone = zoneFor(outerZoneId);
		
		CommutationTicketModelStuttgart.getLandkreis(zone);
	}

	private void verify(Landkreis expected, int id) {
		Zone zone = zoneFor(id);

		Landkreis landkreis = CommutationTicketModelStuttgart.getLandkreis(zone);

		assertThat(landkreis, is(expected));
	}

	private Zone zoneFor(int id) {
		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn(new ZoneId("Z" + id, id));
		return zone;
	}

}
