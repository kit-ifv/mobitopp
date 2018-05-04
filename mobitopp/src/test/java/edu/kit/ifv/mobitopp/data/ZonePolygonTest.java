package edu.kit.ifv.mobitopp.data;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class ZonePolygonTest {

	private ZonePolygon zonePolygon;
	private Point2D centerPoint;
	private Location location;

	@Before
	public void initialise() {
		VisumSurface surface = visumSurface().build();
		centerPoint = Data.coordinate(0, 0);
		location = new Example().location(centerPoint);
		zonePolygon = new ZonePolygon(surface, location);
	}
	
	@Test
	public void centroid() {
		assertThat(zonePolygon.centroid(), is(equalTo(centerPoint)));
	}
	
	@Test
	public void centroidLocation() {
		assertThat(zonePolygon.centroidLocation(), is(equalTo(location)));
	}
}
