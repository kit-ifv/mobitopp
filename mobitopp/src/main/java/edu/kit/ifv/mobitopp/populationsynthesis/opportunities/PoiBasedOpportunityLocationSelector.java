package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class PoiBasedOpportunityLocationSelector implements OpportunityLocationSelector {

	private final List<Opportunity> opportunities;

	public PoiBasedOpportunityLocationSelector(List<Opportunity> opportunities) {
		super();
		this.opportunities = opportunities;
	}

	public static OpportunityLocationSelector create(File inputFile, PoiParser parser) {
		List<Opportunity> opportunities = CsvFile
				.createFrom(inputFile)
				.stream()
				.map(parser::parse)
				.collect(toList());
		return new PoiBasedOpportunityLocationSelector(opportunities);
	}

	@Override
	public Map<Location, Integer> createLocations(
			ZoneId zone, ActivityType activityType, Integer total_opportunities) {
		return opportunities
				.stream()
				.filter(opportunity -> zone.equals(opportunity.zone()))
				.filter(opportunity -> activityType.equals(opportunity.activityType()))
				.collect(toMap(Opportunity::location, Opportunity::attractivity));
	}

}
