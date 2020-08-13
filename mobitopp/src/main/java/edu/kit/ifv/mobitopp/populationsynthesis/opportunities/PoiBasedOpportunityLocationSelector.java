package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class PoiBasedOpportunityLocationSelector implements OpportunityLocationSelector {

	private final File inputFile;
	private final PoiParser parser;

	public PoiBasedOpportunityLocationSelector(File inputFile, PoiParser parser) {
		super();
		this.inputFile = inputFile;
		this.parser = parser;
	}

	@Override
	public Map<Location, Integer> createLocations(
			ZoneId zone, ActivityType activityType, Integer total_opportunities) {
		return pois()
				.map(parser::parse)
				.filter(opportunity -> zone.equals(opportunity.zone()))
				.filter(opportunity -> activityType.equals(opportunity.activityType()))
				.collect(toMap(Opportunity::location, Opportunity::attractivity));
	}

	Stream<Row> pois() {
		return CsvFile.createFrom(inputFile).stream();
	}

}
