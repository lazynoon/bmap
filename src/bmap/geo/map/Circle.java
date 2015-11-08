package bmap.geo.map;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆
 */
public class Circle extends Polygon {
	public static final double[] parseTolerance = {0.01, 0.0001, 0.00001, 0.000004};
	Point center;
	double radius;
	public Circle(Point center, double radius) {
		this.center = center;
		this.radius = Math.abs(radius); //半径。单位：米
		//Polygon.call(this, [], cD)
		initialize();
	}
	private void initialize() {
			this.points = this._getPerimeterPoints(this.center, this.radius);
			this._calcBounds();
		}
		public Point getCenter() {
			return this.center;
		}
		public void setCenter(Point center) {
			if (center == null) {
				return;
			}
			this.center = center;
		}
		public double getRadius() {
			return this.radius;
		}
		public void setRadius(double redius) {
			this.radius = Math.abs(redius);
		}
		public List<Point> _getPerimeterPoints(Point p, double radius) {
			List<Point> list = new ArrayList<Point>();
			if (p == null || radius == 0) {
				return list;
			}
			//var cC = this.map;
			double lng = p.lng,
				lat = p.lat;
			double cL = radius / 6378800; //地球直径
			double cI = (Math.PI / 180) * lat,
				cO = (Math.PI / 180) * lng;
			for (int i = 0; i < 360; i += 9) {
				double cF = (Math.PI / 180) * i,
					cM = Math.asin(Math.sin(cI) * Math.cos(cL) + Math.cos(cI) * Math.sin(cL) * Math.cos(cF)),
					cK = Math.atan2(Math.sin(cF) * Math.sin(cL) * Math.cos(cI), Math.cos(cL) - Math.sin(cI) * Math.sin(cM)),
					cN = ((cO - cK + Math.PI) % (2 * Math.PI)) - Math.PI;
				Point newPoint = new Point(cN * (180 / Math.PI), cM * (180 / Math.PI));
				list.add(newPoint);
			}
			Point first = list.get(0);
			list.add(new Point(first.lng, first.lat));
			return list;
		}

}
