package edu.kit.ifv.mobitopp.dataimport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.io.File;
import java.net.URISyntaxException;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class Example {
	
	static final int pointX = 1;
	static final int pointY = 2;
	static final int dummyEdge = 3;
	static final double positionOnEdge = 0.0d;

	public static StructuralData demographyData() {
		try {
		String path = new File(Example.class.getResource("Demography.csv").toURI())
				.getAbsolutePath();
		CsvFile csv = new CsvFile(path);
		return new StructuralData(csv, areaTypeRepository());
		} catch (URISyntaxException cause) {
			throw new RuntimeException(cause);
		}
	}

	private static BicRepository areaTypeRepository() {
		return new BicRepository();
	}
	
	public static StructuralData attractivityData() {
		try {
			String path = new File(Example.class.getResource("Attractivities.csv").toURI())
					.getAbsolutePath();
			CsvFile csv = new CsvFile(path);
			return new StructuralData(csv, areaTypeRepository());
		} catch (URISyntaxException cause) {
			throw new RuntimeException(cause);
		}
	}

	public Location location() {
		Point2D coordinate = Data.coordinate(pointX, pointY);
		return location(coordinate);
	}

	public Location location(Point2D coordinate) {
		return new Location(coordinate, dummyEdge, positionOnEdge);
	}

	public ZonePolygon polygonAcceptingPoints() {
		VisumSurface surface = mock(VisumSurface.class);
		ZonePolygon polygon = mock(ZonePolygon.class);
		when(polygon.polygon()).thenReturn(surface);
		when(surface.isPointInside(any())).thenReturn(true);
		return polygon;
	}
	
	public ZonePolygon emptyPolygon() {
		VisumSurface surface = mock(VisumSurface.class);
		ZonePolygon polygon = mock(ZonePolygon.class);
		when(polygon.polygon()).thenReturn(surface);
		when(surface.isPointInside(any())).thenReturn(false);
		return polygon;
	}

}
