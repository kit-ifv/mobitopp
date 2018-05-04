package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.CommutationTicketModelStuttgart.Landkreis;

public class CommutationTicketModelStuttgartTest {

	@Test
	public void verifyLandkreisLB() {
		String forExampleId = "Z11111";
		verify(Landkreis.LB, forExampleId);
	}

	@Test
	public void verifyLandkreisS() {
		String forExampleId = "Z21111";
		verify(Landkreis.S, forExampleId);
	}

	@Test
	public void verifyLandkreisWN() {
		String forExampleId = "Z31111";
		verify(Landkreis.WN, forExampleId);
	}

	@Test
	public void verifyLandkreisBB() {
		String forExampleId = "Z41111";
		verify(Landkreis.BB, forExampleId);
	}

	@Test
	public void verifyLandkreisES() {
		String forExampleId = "Z51111";
		verify(Landkreis.ES, forExampleId);
	}

	@Test
	public void verifyLandkreisGP() {
		String forExampleId = "Z61111";
		verify(Landkreis.GP, forExampleId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifyZoneWithoutLandkreis() {
		String outerZoneId = "Z111111";
		Zone zone = zoneFor(outerZoneId);
		
		CommutationTicketModelStuttgart.getLandkreis(zone);
	}

	private void verify(Landkreis expected, String id) {
		Zone zone = zoneFor(id);

		Landkreis landkreis = CommutationTicketModelStuttgart.getLandkreis(zone);

		assertThat(landkreis, is(expected));
	}

	private Zone zoneFor(String id) {
		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn(id);
		return zone;
	}

}
