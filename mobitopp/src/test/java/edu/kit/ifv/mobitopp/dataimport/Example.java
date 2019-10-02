package edu.kit.ifv.mobitopp.dataimport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.UncheckedIOException;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class Example {

  static final String someZone = "1";
  static final String anotherZone = "2";
  static final String otherZone = "3";
  static final int pointX = 1;
  static final int pointY = 2;
  static final int dummyEdge = 3;
  static final double positionOnEdge = 0.0d;

	public static StructuralData demographyData() {
		return create("Demography.csv");
	}
  
	public static StructuralData notStartingAtZeroAge() {
		return create("NotStartingAtZeroAge.csv");
	}

	private static StructuralData create(String fileName) {
		try {
		CsvFile csv = CsvFile.createFrom(Example.class.getResourceAsStream(fileName));
		return new StructuralData(csv);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	public static StructuralData missingAgeGroup() {
		return create("MissingAgeGroup.csv");
	}

  public static BicRepository areaTypeRepository() {
    return new BicRepository();
  }

	public static StructuralData attractivityData() {
		return create("Attractivities.csv");
	}

	public static StructuralData attractivityDataByCode() {
		return create("Attractivities-Code.csv");
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
