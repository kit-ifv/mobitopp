package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class PoiBasedOpportunityLocationSelector extends DefaultOpportunityLocationSelector {

	private final File inputFile;
	private final PoiParser parser;

	public PoiBasedOpportunityLocationSelector(
			SynthesisContext context, File inputFile, PoiParser parser) {
		super(context);
		this.inputFile = inputFile;
		this.parser = parser;
	}

	public static OpportunityLocationSelector createRoadBased(SynthesisContext context) {
		File inputFile = context.experimentalParameters().valueAsFile("pois");
		ZoneRepository zoneRepository = context.zoneRepository().zoneRepository();
		RoadLocator roadLocator = new NetworkBasedRoadLocator(context.roadNetwork());
		PoiParser parser = new PoiParser(zoneRepository, roadLocator);
		return new PoiBasedOpportunityLocationSelector(context, inputFile, parser);
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
