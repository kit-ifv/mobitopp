package edu.kit.ifv.mobitopp.data;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.simulation.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ZoneProperties {

	@NonNull private final String name;
	@NonNull private final AreaType areaType;
	@NonNull private final RegionType regionType;
	@NonNull private final ZoneClassificationType classification;
	private final int parkingPlaces;
	private final boolean isDestination;
	@NonNull private final Location centroidLocation;
	private double relief;
	@NonNull private final Map<String, Value> zoneProperties;

}
