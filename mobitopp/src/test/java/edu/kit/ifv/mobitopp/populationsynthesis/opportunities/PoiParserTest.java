package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class PoiParserTest {

	@Mock
	private ZoneRepository zoneRepository;
	private RoadLocator roadLocator;

	@Test
	void attachesPoiToRoadNetwork() throws Exception {
		ZoneId zone = new ZoneId("1", 0);
		ActivityType activityType = ActivityType.WORK;
		Location location = new Location(new Point2D.Double(0.0, 1.0), 0, 0.0);
		int attractivity = 1;
		when(zoneRepository.getId(zone.getExternalId())).thenReturn(zone);
		roadLocator = (x, y) -> new RoadPosition(location.roadAccessEdgeId(), location.roadPosition());
		PoiParser parser = new PoiParser(zoneRepository, roadLocator);
		Row row = Row
				.createRow(
						List
								.of(zone.getExternalId(), String.valueOf(activityType.getTypeAsInt()),
										String.valueOf(location.coordinatesP().getX()),
										String.valueOf(location.coordinatesP().getY()), String.valueOf(attractivity)),
						List.of("zoneId", "activityType", "locationX", "locationY", "attractivity"));

		Opportunity opportunity = parser.parse(row);

		assertThat(opportunity.zone()).isEqualTo(zone);
		assertThat(opportunity.activityType()).isEqualTo(activityType);
		assertThat(opportunity.location()).isEqualTo(location);
		assertThat(opportunity.attractivity()).isEqualTo(attractivity);
	}
}
