package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;
import edu.kit.ifv.mobitopp.visum.VisumZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Zone implements Serializable {

	private static final long serialVersionUID = 6726608239268453865L;
	
	/** Industrial, commercial, public, military and private unit **/
	private static final String industrialAreaType = "12100";
	private static final List<String> residentialAreaTypes = new ArrayList<String>();

	static {
			residentialAreaTypes.add("11100"); // Continuous Urban Fabric (S.L. > 80%)
			residentialAreaTypes.add("11210"); // Discontinuous Dense Urban Fabric (S.L. : 50% -  80%)
			residentialAreaTypes.add("11220"); // Discontinuous Medium Density Urban Fabric (S.L. : 30% - 50%)
			residentialAreaTypes.add("11230"); // Discontinuous Low Density Urban Fabric (S.L. : 10% - 30%)
			residentialAreaTypes.add("11240"); // Discontinuous Very Low Density Urban Fabric (S.L. < 10%)
			residentialAreaTypes.add("11300"); // Isolated Structures
	}

	private final SimpleRoadNetwork network;
	private final VisumZone vZone; 
	private	final VisumSurface surface;

	private final	Point2D center;
	private final ZoneArea residentialArea;
	private final ZoneArea totalArea;
	private final Map<String,ZoneArea> zoneAreasByLanduse;
	private final List<Edge> intersectingEdges;

	public Zone(SimpleRoadNetwork network, VisumRoadNetwork visumNetwork, VisumZone vZone) {
		this.network = network;
		this.vZone = vZone;
		this.surface = visumNetwork.areas.get(vZone.areaId);
		this.totalArea = new ZoneArea(surface.area());

		final Map<String,Area> areasByLanduse = calculateAreasByLanduse(visumNetwork.territories);

		this.zoneAreasByLanduse = asZoneAreas(areasByLanduse);
		this.residentialArea = calculateResidentialArea(this.surface, areasByLanduse);

		this.intersectingEdges = this.totalArea.filterIntersectingEdges(this.network.edges());
		this.center = vZone.coord.asPoint2D();
	}


	public Integer id() {
		return vZone.id;
	}

	public ZoneArea residentialArea() {

		return residentialArea;
	}

	public ZoneArea totalArea() {

		return totalArea;
	}
	
	public boolean contains(Point2D point) {
		return totalArea.contains(point);
	}

	public boolean isExternal() {

		String id = id().toString();

		return id.length() > 5 && Integer.valueOf(id.substring(0,1)) < 7;
	}

	public boolean isOuter() {
	
		String id = id().toString();

		return id.length() > 5 && Integer.valueOf(id.substring(0,1)) >= 7;
	}

	public Point2D center() {

		return new Point2D.Double(this.center.getX(), this.center.getY());
	}


	private ZoneArea calculateResidentialArea(VisumSurface surface, Map<String,Area> areasByLanduse) {

		Area area = new Area();


		for (String type : residentialAreaTypes) {
			if (!areasByLanduse.containsKey(type)) {
				continue;
			}
			Area a = areasByLanduse.get(type);
			area.add(a);
			log.info("Used residential area type: " + type);
		}

		if (area.isEmpty() && !areasByLanduse.containsKey(industrialAreaType)) {
			log.warn("Zone - No residential area types are available and industrial area type is also not available: "
							+ industrialAreaType);
		}
		if (area.isEmpty() && areasByLanduse.containsKey(industrialAreaType)) {
			Area a = areasByLanduse.get(industrialAreaType);
			area.add(a);
		}

		if (area.isEmpty()) {
			area = surface.area();
		}

		return  new ZoneArea(area);
	}

	public Map<String,ZoneArea> areasByLanduse(Collection<String> landTypes) {

		Map<String,ZoneArea> areas = new LinkedHashMap<String,ZoneArea>();

		for (String type : landTypes) {
			if ( this.zoneAreasByLanduse.containsKey(type)) {
				
				ZoneArea area = this.zoneAreasByLanduse.get(type);
				
				assert area != null;
				
				areas.put(type, area);
				
			}
			
		}

		return areas;
	}

	private Map<String,Area>  calculateAreasByLanduse(Map<Integer,VisumTerritory> territories) {

		Map<String,Area> areas = new LinkedHashMap<String,Area>();
		
		int i = 0;
		int totalSize = territories.size();

		for(VisumTerritory territory : territories.values()) {
			
			
			if ( territory.isRelevantForZoneId(vZone.id)) {

				Area a = territory.intersect(this.surface.area());
				areas.put(territory.code, a);
				
			}
			
			i = i + 1;
			if ( i % 10000 == 0) {
				System.out.println("territory " + i + " of " + totalSize + " id=" + territory.id);
			}

		}

		return areas;
	}

	private static Map<String,ZoneArea>  asZoneAreas(Map<String,Area> areas) {

		Map<String,ZoneArea> zoneAreas = new LinkedHashMap<String,ZoneArea>();

		for (String key : areas.keySet()) {

				zoneAreas.put(key, new ZoneArea(areas.get(key)));
		}

		return zoneAreas;
	}

	public Map<Integer,Edge> containedEdges(SimpleRoadNetwork network) {

		List<Line2D> lines = new ArrayList<Line2D>();
		Map<Line2D,Edge> mappedEdges = new LinkedHashMap<Line2D,Edge>();

		for(Integer key : network.edges.keySet()) {

			Edge edge = network.edges.get(key);

			Line2D line = edge.line();

			lines.add(line);
			mappedEdges.put(line, edge);
		}

		List<Line2D> innerLines = filterInnerEdges(lines);

		if (innerLines.isEmpty()) {
			innerLines = filterMeetingEdges(lines);
		}

		Map<Integer,Edge> result = new LinkedHashMap<Integer,Edge>();

		for (Line2D l : innerLines) {

			Edge e = mappedEdges.get(l);

			assert result != null;
			assert e != null : l;

			result.put(e.id(), e);
		}

		return result;
	}

	private  List<Line2D> filterInnerEdges(List<Line2D> lines) {

		Area area = this.surface.area();
		Rectangle2D bbox = area.getBounds2D();

		List<Line2D> innerLines = new ArrayList<Line2D>();

		for (Line2D l : lines) {
			Point2D from = l.getP1();
			Point2D to = l.getP2();


			if (bbox.contains(from) && bbox.contains(to)) {
				if (area.contains(from) && area.contains(to)) {

					innerLines.add(l);
				}
			}
		}

		return innerLines;
	}

	private  List<Line2D> filterMeetingEdges(List<Line2D> lines) {

		Area area = this.surface.area();
		Rectangle2D bbox = area.getBounds2D();

		List<Line2D> innerLines = new ArrayList<Line2D>();

		for (Line2D l : lines) {
			Point2D from = l.getP1();
			Point2D to = l.getP2();


			if (bbox.contains(from) || bbox.contains(to)) {
				if (area.contains(from) || area.contains(to)) {

					innerLines.add(l);
				}
			}
		}

		return innerLines;
	}


	
	public List<Line2D> edgesAsLines(Collection<SimpleEdge> edges) {
		List<Line2D> lines = new ArrayList<Line2D>();

		for (SimpleEdge edge : edges) {
			lines.add(edge.line());
		}

		return lines;
	}


	public SimpleEdge nearestEdge(Point2D point) {


		if (this.intersectingEdges.isEmpty()) {

			return (SimpleEdge) nearestEdgeAtConnector(point);
		}

		return (SimpleEdge) nearestEdge(point, this.intersectingEdges);
	}

	private Edge nearestEdgeAtConnector(Point2D point) {

		List<Edge> edges = edgesAtConnector();

		assert !edges.isEmpty();

		return nearestEdge(point, edges);
	}


	private Edge nearestEdge(Point2D point, List<Edge> edges) {

		assert !edges.isEmpty();

		double min_dist = Double.MAX_VALUE;
		Edge nearest_edge = edges.get(0);

		for (Edge edge : edges) {

			double dist = edge.line().ptSegDist(point);
			if (dist < min_dist && !((SimpleEdge)edge).isDegenerated()) {
				nearest_edge = edge;
				min_dist = dist;
			}
		}

		return nearest_edge;
	}


	private	List<Edge> edgesAtConnector() {

		List<Edge> edges = new ArrayList<Edge>();

		SimpleNode center = this.network.zoneCenterNodes.get(this.vZone.id);

		List<Edge> connectors = this.network.connectorEdges.get(this.vZone.id);

		assert !connectors.isEmpty();

		Set<Node> connectorNodes = new LinkedHashSet<Node>();

		for (Edge e :connectors) {
			connectorNodes.add(e.from());
			connectorNodes.add(e.to());
		}

		connectorNodes.remove(center);

		assert !connectorNodes.isEmpty();

		for (Node node : connectorNodes) {

			Collection<Edge> outgoingEdges = this.network.out(node);

			assert !outgoingEdges.isEmpty();

			edges.addAll(outgoingEdges);
		}

		return edges;
	}

	public Location randomLocation(
		int randomValue
	) {

		Point2D point = residentialArea.randomPoint(randomValue);

		SimpleEdge road = nearestEdge(point);

		double pos = road.nearestPositionOnEdge(point);

		return new Location(point, road.id(), pos);
	}

}
