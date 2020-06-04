package edu.kit.ifv.mobitopp.data.local;

import java.util.SortedMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public interface DemandRegionMapping {

	SortedMap<RegionalLevel, RegionalLevel> getLevels();

	Stream<Row> contentFor(RegionalLevel level);

}
