package edu.kit.ifv.mobitopp.network;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectStreamException;

import edu.kit.ifv.mobitopp.util.geom.AreaUtil;

public class ZoneArea
	implements Serializable
{

	private static final long serialVersionUID = 1L;
	private static long SEED = 123;

	private final Area area;
	private final Border border;
	private final double estimatedSize;

	public ZoneArea(Area area) {
		this.area = area;
		border = new Border(AreaUtil.asLines(area));
		this.estimatedSize = calculateEstimateSize();
	}

	public Point2D randomPoint(long rnd) {

		Random random = new Random(rnd);

		Point2D point = randomPointWithinEnvelope(random.nextDouble(), random.nextDouble());

		int i=0;

		while (!this.area.contains(point)){

			if (i>0 && i%100000 == 0	) { 
					System.out.println("try " + i); 
			}

			point = randomPointWithinEnvelope(random.nextDouble(), random.nextDouble());

			i++;

			if (i>1000000){System.exit(-1); }
		}

		return point;
	}


	public boolean isEmpty() {
		return area.isEmpty();
	}

	public Rectangle2D getBounds2D() {
		return area.getBounds2D();
	}

	public boolean contains(Point2D point) {
		return area.contains(point);
	}

	public double estimateSize() {

		return estimatedSize;
	}

	private double calculateEstimateSize() {

		if (area.isEmpty()) {
			return 0.0;
		}

		final int SAMPLES = 100;

		Random random = new Random(SEED);

		Rectangle2D bbox = area.getBounds2D();

		double size = bbox.getWidth() * bbox.getHeight();

		int inside = 0;

		for (int i=0; i<SAMPLES; i++) {

			Point2D point = randomPointWithinEnvelope(random.nextDouble(), random.nextDouble());

			if (area.contains(point)) {
				inside++;
			}
		}

		size = (Math.max(inside, 0.01d) / SAMPLES) * size;

		return size;	
	}

	private Point2D randomPointWithinEnvelope(double rnd1, double rnd2) {

		Rectangle2D bbox = area.getBounds2D();

		double x_min = bbox.getMinX();
		double y_min = bbox.getMinY();
		double x_range = bbox.getWidth();
		double y_range = bbox.getHeight();

		double x = x_min + rnd1*x_range;
		double y = y_min +  rnd2*y_range;
		
		return new Point2D.Double(x,y);

	}

	public  List<Edge> filterIntersectingEdges(Collection<Edge> lines) {
		return filterIntersectingEdges(lines, 2.0);
	}

	private  List<Edge> filterIntersectingEdges(Collection<Edge> lines, double surrounding) {

		Rectangle2D bbox = scale(area.getBounds2D(),surrounding);

		List<Edge> matchingLines = new ArrayList<Edge>();

		for (Edge e : lines) {

			Line2D line = e.line();

			Point2D from = line.getP1();
			Point2D to = line.getP2();


			if (bbox.contains(from) || bbox.contains(to)) {
				if (area.contains(from) || area.contains(to)) {

					matchingLines.add(e);
				} else if (border.intersects(line)) {
					matchingLines.add(e);
				}
			}
		}

		return matchingLines;
	}

	private Rectangle2D scale(Rectangle2D rect, double scaling) {

		double cx = rect.getCenterX();
		double cy = rect.getCenterY();

		double width = rect.getWidth();
		double height = rect.getHeight();


		return new Rectangle2D.Double(
									cx-0.5*width*scaling,
									cy-0.5*height*scaling,
									scaling*width, scaling*height);
	}


	// Serialization
	private static Shape tmp_area;
	private static double tmp_estimated_size;

	private ZoneArea(Shape area, double estimatedSize) {

		this.area = new Area(area);
		border = new Border(AreaUtil.asLines(this.area));
		this.estimatedSize = estimatedSize;
	}

	private void writeObject(java.io.ObjectOutputStream out)
     throws IOException 
	{
		Shape shp = AffineTransform.getTranslateInstance(0.0,0.0).createTransformedShape(area);

		out.writeObject(shp);
		out.writeDouble(estimatedSize);
	}

 private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
	{
		tmp_area = (Shape) in.readObject();
		tmp_estimated_size = in.readDouble();
	}

 private Object readResolve()
     throws ObjectStreamException
	{
		return new ZoneArea(tmp_area, tmp_estimated_size);
	}
}
