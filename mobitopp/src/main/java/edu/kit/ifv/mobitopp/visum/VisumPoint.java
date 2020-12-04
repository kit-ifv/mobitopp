package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Objects;

public class VisumPoint implements Serializable {

  private static final long serialVersionUID = 1L;
  private final double x;
  private final double y;

  public VisumPoint(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public VisumPoint(Point2D p) {
    this(p.getX(), p.getY());
  }
  
  public double getX() {
    return x;
  }
  
  public double getY() {
    return y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumPoint other = (VisumPoint) obj;
    return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
        && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
  }

  public String toString() {
    return "(" + getX() + "," + getY() + ")";
  }
}
