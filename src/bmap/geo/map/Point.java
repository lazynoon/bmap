package bmap.geo.map;

public class Point {
	public double lng;
	public double lat;
	
	public Point(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}
	public boolean isInRange(Point p) {
		return p.lng <= 180 && p.lng >= -180 && p.lat <= 74 && p.lat >= -74;
	};
	public boolean equals(Point p) {
		return this.lat == p.lat && this.lng == p.lng;
	};

}
