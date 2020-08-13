package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class PoiBasedOpportunityLocationSelectorTest {

	@Mock
	private SynthesisContext context;
	@Mock
	private File inputFile;
	@Mock
	private PoiParser parser;

	@Test
	void createLocationsFromOpportunities() throws Exception {
		Location location = new Location(new Point2D.Double(0.0, 1.0), 0, 0);
		ZoneId zone = new ZoneId("1", 0);
		ActivityType activityType = ActivityType.WORK;
		Integer total_opportunities = 1;

		when(parser.parse(any()))
				.thenReturn(new Opportunity(zone, activityType, location, total_opportunities));

		OpportunityLocationSelector selector = createSelector();
		Map<Location, Integer> locations = selector
				.createLocations(zone, activityType, total_opportunities);

		assertThat(locations).containsEntry(location, total_opportunities);
	}

	private PoiBasedOpportunityLocationSelector createSelector() {
		return new PoiBasedOpportunityLocationSelector(inputFile, parser) {

			@Override
			Stream<Row> pois() {
				return Stream.of(Row.createRow(List.of(), List.of()));
			}
		};
	}
}
