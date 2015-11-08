package bmap.geo.map;

public class Bounds {
	Point _sw = null;
	Point _ne = null;
	double _swLng = 0;
	double _swLat = 0;
	double _neLng = 0;
	double _neLat = 0;
	public Bounds() {
		
	}
	public Bounds(Point sw, Point ne) {
		this._sw = new Point(sw.lng, sw.lat);
		this._ne = new Point(ne.lng, ne.lat);
		this._swLng = sw.lng;
		this._swLat = sw.lat;
		this._neLng = ne.lng;
		this._neLat = ne.lat;
 
	}
	public boolean isEmpty() {
		return this._sw == null || this._ne == null;
	}
	public boolean equals(Bounds p) {
		if (this.isEmpty()) {
			return false;
		}
		return this.getSouthWest().equals(p.getSouthWest()) && this.getNorthEast().equals(p.getNorthEast());
	}
	public Point getSouthWest() {
		return this._sw;
	}
	public Point getNorthEast() {
		return this._ne;
	}
	public boolean containsBounds(Bounds p) {
		if (this.isEmpty() || p.isEmpty()) {
			return false;
		}
		return (p._swLng > this._swLng && p._neLng < this._neLng && p._swLat > this._swLat && p._neLat < this._neLat);
	}
	public Point getCenter() {
		if (this.isEmpty()) {
			return null;
		}
		return new Point((this._swLng + this._neLng) / 2, (this._swLat + this._neLat) / 2);
	}
	public Bounds intersects(Bounds p) {
		if (Math.max(p._swLng, p._neLng) < Math.min(this._swLng, this._neLng)
				|| Math.min(p._swLng, p._neLng) > Math.max(this._swLng, this._neLng)
				|| Math.max(p._swLat, p._neLat) < Math.min(this._swLat, this._neLat)
				|| Math.min(p._swLat, p._neLat) > Math.max(this._swLat, this._neLat)) {
			return null;
		}
		double a = Math.max(this._swLng, p._swLng);
		double b = Math.min(this._neLng, p._neLng);
		double c = Math.max(this._swLat, p._swLat);
		double d = Math.min(this._neLat, p._neLat);
		return new Bounds(new Point(a, c), new Point(b, d));
	}
	public boolean containsPoint(Point p) {
		if (this.isEmpty()) {
			return false;
		}
		return (p.lng >= this._swLng && p.lng <= this._neLng && p.lat >= this._swLat && p.lat <= this._neLat);
	}
	public void extend(Point p) {
		double lng = p.lng,
			lat = p.lat;
		if (this._sw == null) {
			this._sw = new Point(0, 0);
		}
		if (this._ne == null) {
			this._ne = new Point(0, 0);
		}
		if (this._swLng == 0 || this._swLng > lng) {
			this._sw.lng = this._swLng = lng;
		}
		if (this._neLng == 0 || this._neLng < lng) {
			this._ne.lng = this._neLng = lng;
		}
		if (this._swLat == 0 || this._swLat > lat) {
			this._sw.lat = this._swLat = lat;
		}
		if (this._neLat == 0 || this._neLat < lat) {
			this._ne.lat = this._neLat = lat;
		}
	}
	public Point toSpan() {
		if (this.isEmpty()) {
			return new Point(0, 0);
		}
		return new Point(Math.abs(this._neLng - this._swLng), Math.abs(this._neLat - this._swLat));
	}

}
