package bmap.geo.map;

import java.util.List;

/**
 * 多边形
 */
public class Polygon extends Graph {
	protected List<Point> _userPoints;
	protected Polygon() {
		
	}
	public Polygon(List<Point> pts) {
		//cw.call(this, cD);
		this.setPath(Graph.formatPointGroup(pts));
	}
	public Polygon(String sPointList) {
		//cw.call(this, cD);
		this.setPath(sPointList);
	}
	//public void setPath(String sPointList, T) {
//	public void setPath(List<Point> pts) {
//		this._userPoints = pts.subList(0, pts.size());		
//	}
	public void setPath(String sPointList) {
		this._userPoints = super.getGraphPoints(sPointList);
		List<Point> list = super.getGraphPoints(sPointList);
		if (list.size() > 1 && !list.get(0).equals(list.get(list.size() - 1))) {
			list.add(new Point(list.get(0).lng, list.get(0).lat));
		}
		
		//cw.prototype.setPath.call(this, list, T);
		super.setPath(sPointList);
	}
	public void setPositionAt(int pos, Point p) {
		if (this._userPoints != null && pos >= this._userPoints.size()) {
			return;
		}
		this._userPoints.add(pos, new Point(p.lng, p.lat));
		this.points.add(pos, new Point(p.lng, p.lat));
		if (pos == 0 && !this.points.get(0).equals(this.points.get(this.points.size() - 1))) {
			this.points.add(this.points.size() - 1, new Point(p.lng, p.lat));
		}
		this._calcBounds();
	}
	public Point[] getPath() {
		if (this._userPoints != null && this._userPoints.size() != 0) {
			return this._userPoints.toArray(new Point[0]);
		} else {
			return this.points.toArray(new Point[0]);
		}
	}


}
