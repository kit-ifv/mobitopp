package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Line2D;
import java.util.List;

import edu.kit.ifv.mobitopp.util.geom.AreaUtil;

public class Border {

	private final List<List<Line2D>> lines;

	public Border(List<List<Line2D>> lines) {
		this.lines = lines;
	}

	public boolean intersects(Line2D line) {
		return AreaUtil.lineIntersectsLines(line, lines);
	}

}
