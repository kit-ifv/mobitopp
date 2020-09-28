package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

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

		OpportunityLocationSelector selector = createSelector(new Opportunity(zone, activityType, location, total_opportunities));
		Map<Location, Integer> locations = selector
				.createLocations(zone, activityType, total_opportunities);

		assertThat(locations).containsEntry(location, total_opportunities);
	}

	private PoiBasedOpportunityLocationSelector createSelector(Opportunity opportunity) {
		return new PoiBasedOpportunityLocationSelector(List.of(opportunity));
	}
}
