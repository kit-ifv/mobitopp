package edu.kit.ifv.mobitopp.populationsynthesis.opportunities;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.data.ZoneId;

public interface RoadLocator {

	/**
	 * Find the nearest edges id and position within the given {@link ZoneId}.
	 * 
	 * @param zone
	 *          to search edges in
	 * @param coordinate
	 *          x and y coordinate to search nearest edge for
	 * @return position on nearest edge
	 */
	RoadPosition getRoadPosition(ZoneId zone, Point2D coordinate);

}
