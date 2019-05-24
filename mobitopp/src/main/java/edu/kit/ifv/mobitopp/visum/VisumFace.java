package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class VisumFace 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final Integer id;

	public final List<VisumEdge> edges;
	public final List<Integer> direction;

	transient private Area area;


	public VisumFace(Integer id, List<VisumEdge> edges,  List<Integer> direction) {

		assert edges.size() == direction.size();

		this.id = id;
		this.edges = Collections.unmodifiableList(edges);
		this.direction = Collections.unmodifiableList(direction);

	}

  public Area asArea() {
    if (null == this.area) {
      area = new Area(asPath());
    }
    return area;
  }

  private Path2D asPath() {

    Path2D shp = new Path2D.Double();

    for (int i=0; i< edges.size(); i++) {

      VisumEdge e = edges.get(i);
      Integer dir = direction.get(i);

      VisumPoint from;
      VisumPoint to;

      List<VisumPoint> intermediate = new ArrayList<>(e.intermediate);

      if (dir == 0) {

        from = e.from;
        to = e.to;

      } else {
        from = e.to;
        to = e.from;

        Collections.reverse(intermediate);        
      }

      if (i==0) {
        shp.moveTo(from.getX(),from.getY());
      }

      for (VisumPoint p : intermediate) {
        shp.lineTo(p.getX(),p.getY());
      }

      shp.lineTo(to.getX(),to.getY());

    }

    return shp;
  }

  @Override
  public int hashCode() {
    return Objects.hash(direction, edges, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumFace other = (VisumFace) obj;
    return Objects.equals(direction, other.direction) && Objects.equals(edges, other.edges)
        && Objects.equals(id, other.id);
  }

  public String toString() {
    String s = "VisumFace(edges=" + edges.size() + "; \n";
    for (int i = 0; i < edges.size(); i++) {
      VisumEdge e = edges.get(i);
      int dir = direction.get(i);
      s += e + ", dir=" + dir + "; \n";
    }
    s += ")";
    return s;
  }
}
