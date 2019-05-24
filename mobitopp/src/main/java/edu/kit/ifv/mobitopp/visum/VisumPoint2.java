package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;

import java.io.Serializable;
import java.util.Objects;

public class VisumPoint2 implements Serializable {

  private static final long serialVersionUID = 1L;

  public final float x;
  public final float y;

  public VisumPoint2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Point2D asPoint2D() {
    return new Point2D.Float(x, y);
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
    VisumPoint2 other = (VisumPoint2) obj;
    return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
        && Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
  }

  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
