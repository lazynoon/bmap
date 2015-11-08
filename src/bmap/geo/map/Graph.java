package bmap.geo.map;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	public static final double[] parseTolerance = {0.09f, 0.005f, 0.0001f, 0.00001f};

	protected List<Point> points;
	Bounds _bounds;
	/*
	function cw(T) {
		U.call(this);
		this._config = {
			strokeColor: "#3a6bdb",
			strokeWeight: 5,
			strokeOpacity: 0.65,
			strokeStyle: "solid",
			enableMassClear: true,
			getParseTolerance: null,
			getParseCacheIndex: null,
			enableEditing: false,
			mouseOverTolerance: 15,
			use3DCoords: false,
			clickable: true
		};
		T = T || {};
		this.setConfig(T);
		if (this._config.strokeWeight <= 0) {
			this._config.strokeWeight = 5
		}
		if (this._config.strokeOpacity < 0 || this._config.strokeOpacity > 1) {
			this._config.strokeOpacity = 0.65
		}
		if (this._config.fillOpacity < 0 || this._config.fillOpacity > 1) {
			this._config.fillOpacity = 0.65
		}
		if (this._config.strokeStyle != "solid" && this._config.strokeStyle != "dashed") {
			this._config.strokeStyle = "solid"
		}
		if (b8(T.enableClicking)) {
			this._config.clickable = T.enableClicking
		}
		this.domElement = null;
		this._bounds = new BMap.Bounds(0, 0, 0, 0);
		this._parseCache = [];
		this.vertexMarkers = [];
		this._temp = {}
	}
	a1.lang.inherits(cw, U, "Graph");
	*/
	public List<Point> getGraphPoints(String str) {
		List<Point> list = new ArrayList<Point>();
		if (str == null) {
			return list;
		}
		for(String str2 : str.split(";")) {
			String[] arr = str2.split(",", 2);
			double lng = Float.parseFloat(arr[0]);
			double lat = Float.parseFloat(arr[1]);
			list.add(new Point(lng, lat));
		}
		return list;
	}
	public void setPath(String sPointList) {
		//this._parseCache.length = 0;
		this.points = this.getGraphPoints(sPointList);
		this._calcBounds();
	}
	protected void _calcBounds() {
		if (this.points == null) {
			return;
		}
		Bounds bounds = new Bounds();
		for(Point p : this.points) {
			bounds.extend(p);
		}
		//TODO:
		this._bounds = bounds;
	}
	public Point[] getPath() {
		return this.points.toArray(new Point[0]);
	}
	public void setPositionAt(int pos, Point p) {
		if (p == null || pos >= this.points.size()) {
			return;
		}
		//this._parseCache.length = 0;
		this.points.add(pos, new Point(p.lng, p.lat));
		this._calcBounds();
	}
	public Bounds getBounds() {
		return this._bounds;
	}
	
	public static String formatPointGroup(List<Point> pts) {
		StringBuffer sb = new StringBuffer();
		for(Point p : pts) {
			if(sb.length() > 0) {
				sb.append(";");
			}
			sb.append(p.lng);
			sb.append(",");
			sb.append(p.lat);
		}
		return sb.toString();
	}

}
