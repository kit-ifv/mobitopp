package edu.kit.ifv.mobitopp.populationsynthesis.region;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.Zone;
import lombok.Value;

@Value
public class OdPair {

	private final Zone homeZone;
	private final Zone possibleDestination;

	public static OdPair from(DemandZone homeZone, DemandZone possibleDestination) {
		return new OdPair(homeZone.zone(), possibleDestination.zone());
	}

}
