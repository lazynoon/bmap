package bmap.geo;

import bmap.geo.map.Point;

public class ConvertUtils {

	private static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	// / <summary>
	// / 中国正常坐标系GCJ02协议的坐标，转到 百度地图对应的 BD09 协议坐标
	// / </summary>
	// / <param name="lat">维度</param>
	// / <param name="lng">经度</param>
	public static Point toBaidu(Point pt) {
		double lat = pt.lat; 
		double lng = pt.lng;
		double x = lng, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		lng = z * Math.cos(theta) + 0.0065;
		lat = z * Math.sin(theta) + 0.006;
		return new Point(lng, lat);
	}

	// / <summary>
	// / 百度地图对应的 BD09 协议坐标，转到 中国正常坐标系GCJ02协议的坐标
	// / </summary>
	// / <param name="lat">维度</param>
	// / <param name="lng">经度</param>
	public static Point fromBaidu(Point pt) {
		double lat = pt.lat; 
		double lng = pt.lng;
		double x = lng - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		lng = z * Math.cos(theta);
		lat = z * Math.sin(theta);
		return new Point(lng, lat);
	}

}
