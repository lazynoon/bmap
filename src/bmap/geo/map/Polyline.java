package bmap.geo.map;

public class Polyline extends Graph {
	public Polyline(String sPointList) {
		//Graph.call(this, cD);
		this.setPath(sPointList);
	}
}
