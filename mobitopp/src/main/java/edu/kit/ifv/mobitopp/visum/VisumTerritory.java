package edu.kit.ifv.mobitopp.visum;


import java.util.List;
import java.awt.geom.Area;

import java.io.Serializable;

public class VisumTerritory 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final int id;
	public final String code;
	public final String name;

	public final int surfaceId;

	protected	VisumSurface surface;

	public VisumTerritory(
		int id,
		String code,
		String name,
		int surfaceId,
		VisumSurface surface
	) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.surfaceId = surfaceId;
		this.surface = surface;
	}
	

	public List<Area> areas() {
		return surface.areas();
	}

	public String toString() {

		return "VisumTerritory(" + id + "," + code + "," + name + "," + surfaceId + "," + surface + ")";
	}

	public Area intersect(Area area) {

		Area result = new Area();

		for(Area current : surface.areas()) {

			if (current.intersects(area.getBounds2D())) {

				Area tmp = new Area(area);
				tmp.intersect(current);

				result.add(tmp);
			}
		}

		return result;
	}

}
