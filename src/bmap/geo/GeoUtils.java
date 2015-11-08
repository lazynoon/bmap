package bmap.geo;

import bmap.geo.map.Bounds;
import bmap.geo.map.Circle;
import bmap.geo.map.Point;
import bmap.geo.map.Polygon;
import bmap.geo.map.Polyline;

public class GeoUtils {
    /**
     * 地球半径
     */
    public static final double EARTHRADIUS = 6370996.81; 

    /**
     * 判断点是否在矩形内
     * @param {Point} point 点对象
     * @param {Bounds} bounds 矩形边界对象
     * @returns {Boolean} 点在矩形内返回true,否则返回false
     */
    public static boolean isPointInRect(Point point, Bounds bounds){
        //检查类型是否正确
        if (!(point instanceof Point) || 
            !(bounds instanceof Bounds)) {
            return false;
        }
        Point sw = bounds.getSouthWest(); //西南脚点
        Point ne = bounds.getNorthEast(); //东北脚点
        return (point.lng >= sw.lng && point.lng <= ne.lng && point.lat >= sw.lat && point.lat <= ne.lat);
    }
    
    /**
     * 判断点是否在圆形内
     * @param {Point} point 点对象
     * @param {Circle} circle 圆形对象
     * @returns {Boolean} 点在圆形内返回true,否则返回false
     */
    public static boolean isPointInCircle(Point point, Circle circle){
        //检查类型是否正确
        if (!(point instanceof Point) || 
            !(circle instanceof Circle)) {
            return false;
        }

        //point与圆心距离小于圆形半径，则点在圆内，否则在圆外
        Point c = circle.getCenter();
        double r = circle.getRadius();

        double dis = GeoUtils.getDistance(point, c);
        if(dis <= r){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 判断点是否在折线上
     * @param {Point} point 点对象
     * @param {Polyline} polyline 折线对象
     * @returns {Boolean} 点在折线上返回true,否则返回false
     */
    public static boolean isPointOnPolyline(Point point, Polyline polyline){
        //检查类型
        if(!(point instanceof Point) ||
            !(polyline instanceof Polyline)){
            return false;
        }

        //首先判断点是否在线的外包矩形内，如果在，则进一步判断，否则返回false
        Bounds lineBounds = polyline.getBounds();
        if(!isPointInRect(point, lineBounds)){
            return false;
        }

        //判断点是否在线段上，设点为Q，线段为P1P2 ，
        //判断点Q在该线段上的依据是：( Q - P1 ) × ( P2 - P1 ) = 0，且 Q 在以 P1，P2为对角顶点的矩形内
        Point[] pts = polyline.getPath();
        for(int i = 0; i < pts.length - 1; i++){
            Point curPt = pts[i];
            Point nextPt = pts[i+1];
            //首先判断point是否在curPt和nextPt之间，即：此判断该点是否在该线段的外包矩形内
            if (point.lng >= Math.min(curPt.lng, nextPt.lng) && point.lng <= Math.max(curPt.lng, nextPt.lng) &&
                point.lat >= Math.min(curPt.lat, nextPt.lat) && point.lat <= Math.max(curPt.lat, nextPt.lat)){
                //判断点是否在直线上公式
                double precision = (curPt.lng - point.lng) * (nextPt.lat - point.lat) - 
                    (nextPt.lng - point.lng) * (curPt.lat - point.lat);                
                if(precision < 2e-10 && precision > -2e-10){//实质判断是否接近0
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 判断点是否多边形内
     * @param {Point} point 点对象
     * @param {Polyline} polygon 多边形对象
     * @returns {Boolean} 点在多边形内返回true,否则返回false
     */
    public static boolean isPointInPolygon(Point point, Polygon polygon){

        //首先判断点是否在多边形的外包矩形内，如果在，则进一步判断，否则返回false
        Bounds polygonBounds = polygon.getBounds();
        if(!isPointInRect(point, polygonBounds)){
            return false;
        }

        Point[] pts = polygon.getPath();//获取多边形点
        
        //下述代码来源：http://paulbourke.net/geometry/insidepoly/，进行了部分修改
        //基本思想是利用射线法，计算射线与多边形各边的交点，如果是偶数，则点在多边形外，否则
        //在多边形内。还会考虑一些特殊情况，如点在多边形顶点上，点在多边形边上等特殊情况。
        
        int N = pts.length;
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x 
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point p1, p2;//neighbour bound vertices
        Point p = point; //测试点
        
        p1 = pts[0];//left vertex        
        for(int i = 1; i <= N; ++i){//check all rays            
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }
            
            p2 = pts[i % N];//right vertex            
            if(p.lat < Math.min(p1.lat, p2.lat) || p.lat > Math.max(p1.lat, p2.lat)){//ray is outside of our interests                
                p1 = p2; 
                continue;//next ray left point
            }
            
            if(p.lat > Math.min(p1.lat, p2.lat) && p.lat < Math.max(p1.lat, p2.lat)){//ray is crossing over by the algorithm (common part of)
                if(p.lng <= Math.max(p1.lng, p2.lng)){//x is before of ray                    
                    if(p1.lat == p2.lat && p.lng >= Math.min(p1.lng, p2.lng)){//overlies on a horizontal ray
                        return boundOrVertex;
                    }
                    
                    if(p1.lng == p2.lng){//ray is vertical                        
                        if(p1.lng == p.lng){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        } 
                    }else{//cross point on the left side                        
                        double xinters = (p.lat - p1.lat) * (p2.lng - p1.lng) / (p2.lat - p1.lat) + p1.lng;//cross point of lng                        
                        if(Math.abs(p.lng - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }
                        
                        if(p.lng < xinters){//before ray
                            ++intersectCount;
                        } 
                    }
                }
            }else{//special case when ray is crossing through the vertex                
                if(p.lat == p2.lat && p.lng <= p2.lng){//p crossing over p2                    
                    Point p3 = pts[(i+1) % N]; //next vertex                    
                    if(p.lat >= Math.min(p1.lat, p3.lat) && p.lat <= Math.max(p1.lat, p3.lat)){//p.lat lies between p1.lat & p3.lat
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }            
            p1 = p2;//next ray left point
        }
        
        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }            
    }

    /**
     * 将度转化为弧度
     * @param {degree} Number 度     
     * @returns {Number} 弧度
     */
    public static double degreeToRad(double degree){
        return Math.PI * degree/180;    
    }
    
    /**
     * 将弧度转化为度
     * @param {radian} Number 弧度     
     * @returns {Number} 度
     */
    public static double radToDegree(double rad){
        return (180 * rad) / Math.PI;       
    }
    
    /**
     * 将v值限定在a,b之间，纬度使用
     */
    private static double  _getRange(double v, double a, double b){
        v = Math.max(v, a);
        v = Math.min(v, b);
        return v;
    }
    
    /**
     * 将v值限定在a,b之间，经度使用
     */
    private static double  _getLoop(double v, double a, double b){
        while( v > b){
          v -= b - a;
        }
        while(v < a){
          v += b - a;
        }
        return v;
    }

    /**
     * 计算两点之间的距离,两点坐标必须为经纬度
     * @param {point1} Point 点对象
     * @param {point2} Point 点对象
     * @returns {Number} 两点之间距离，单位为米
     */
    public static double getDistance(Point point1, Point point2){
        //判断类型
        if(!(point1 instanceof Point) ||
            !(point2 instanceof Point)){
            return 0;
        }

        point1.lng = _getLoop(point1.lng, -180, 180);
        point1.lat = _getRange(point1.lat, -74, 74);
        point2.lng = _getLoop(point2.lng, -180, 180);
        point2.lat = _getRange(point2.lat, -74, 74);
        
        double x1, x2, y1, y2;
        x1 = GeoUtils.degreeToRad(point1.lng);
        y1 = GeoUtils.degreeToRad(point1.lat);
        x2 = GeoUtils.degreeToRad(point2.lng);
        y2 = GeoUtils.degreeToRad(point2.lat);

        return EARTHRADIUS * Math.acos((Math.sin(y1) * Math.sin(y2) + Math.cos(y1) * Math.cos(y2) * Math.cos(x2 - x1)));    
    }
    
    /**
     * 计算折线或者点数组的长度
     * @param {Polyline|Array<Point>} polyline 折线对象或者点数组
     * @returns {Number} 折线或点数组对应的长度
     */
    public static double getPolylineDistance(Polyline polyline){
    	return getPolylineDistance(polyline.getPath());
    }
    public static double getPolylineDistance(Point[] pts){
        if(pts.length < 2){//小于2个点，返回0
            return 0;
        }

        //遍历所有线段将其相加，计算整条线段的长度
        double totalDis = 0;
        for(int i =0; i < pts.length - 1; i++){
            Point curPt = pts[i];
            Point nextPt = pts[i + 1];
            double dis = GeoUtils.getDistance(curPt, nextPt);
            totalDis += dis;
        }

        return totalDis;
            
    }
    
    /**
     * 计算多边形面或点数组构建图形的面积,注意：坐标类型只能是经纬度，且不适合计算自相交多边形的面积
     * @param {Polygon|Array<Point>} polygon 多边形面对象或者点数组
     * @returns {Number} 多边形面或点数组构成图形的面积
     */
    public static double getPolygonArea(Polygon polygon){
    	return getPolygonArea(polygon.getPath());
    }
    public static double getPolygonArea(Point[] pts){
        if(pts.length < 3){//小于3个顶点，不能构建面
            return 0;
        }
        
        double totalArea = 0;//初始化总面积
        double LowX = 0.0;
        double LowY = 0.0;
        double MiddleX = 0.0;
        double MiddleY = 0.0;
        double HighX = 0.0;
        double HighY = 0.0;
        double AM = 0.0;
        double BM = 0.0;
        double CM = 0.0;
        double AL = 0.0;
        double BL = 0.0;
        double CL = 0.0;
        double AH = 0.0;
        double BH = 0.0;
        double CH = 0.0;
        double CoefficientL = 0.0;
        double CoefficientH = 0.0;
        double ALtangent = 0.0;
        double BLtangent = 0.0;
        double CLtangent = 0.0;
        double AHtangent = 0.0;
        double BHtangent = 0.0;
        double CHtangent = 0.0;
        double ANormalLine = 0.0;
        double BNormalLine = 0.0;
        double CNormalLine = 0.0;
        double OrientationValue = 0.0;
        double AngleCos = 0.0;
        double Sum1 = 0.0;
        double Sum2 = 0.0;
        double Count2 = 0;
        double Count1 = 0;
        double Sum = 0.0;
        double Radius = EARTHRADIUS; //6378137.0,WGS84椭球半径 
        int Count = pts.length;        
        for (int i = 0; i < Count; i++) {
            if (i == 0) {
                LowX = pts[Count - 1].lng * Math.PI / 180;
                LowY = pts[Count - 1].lat * Math.PI / 180;
                MiddleX = pts[0].lng * Math.PI / 180;
                MiddleY = pts[0].lat * Math.PI / 180;
                HighX = pts[1].lng * Math.PI / 180;
                HighY = pts[1].lat * Math.PI / 180;
            }
            else if (i == Count - 1) {
                LowX = pts[Count - 2].lng * Math.PI / 180;
                LowY = pts[Count - 2].lat * Math.PI / 180;
                MiddleX = pts[Count - 1].lng * Math.PI / 180;
                MiddleY = pts[Count - 1].lat * Math.PI / 180;
                HighX = pts[0].lng * Math.PI / 180;
                HighY = pts[0].lat * Math.PI / 180;
            }
            else {
                LowX = pts[i - 1].lng * Math.PI / 180;
                LowY = pts[i - 1].lat * Math.PI / 180;
                MiddleX = pts[i].lng * Math.PI / 180;
                MiddleY = pts[i].lat * Math.PI / 180;
                HighX = pts[i + 1].lng * Math.PI / 180;
                HighY = pts[i + 1].lat * Math.PI / 180;
            }
            AM = Math.cos(MiddleY) * Math.cos(MiddleX);
            BM = Math.cos(MiddleY) * Math.sin(MiddleX);
            CM = Math.sin(MiddleY);
            AL = Math.cos(LowY) * Math.cos(LowX);
            BL = Math.cos(LowY) * Math.sin(LowX);
            CL = Math.sin(LowY);
            AH = Math.cos(HighY) * Math.cos(HighX);
            BH = Math.cos(HighY) * Math.sin(HighX);
            CH = Math.sin(HighY);
            CoefficientL = (AM * AM + BM * BM + CM * CM) / (AM * AL + BM * BL + CM * CL);
            CoefficientH = (AM * AM + BM * BM + CM * CM) / (AM * AH + BM * BH + CM * CH);
            ALtangent = CoefficientL * AL - AM;
            BLtangent = CoefficientL * BL - BM;
            CLtangent = CoefficientL * CL - CM;
            AHtangent = CoefficientH * AH - AM;
            BHtangent = CoefficientH * BH - BM;
            CHtangent = CoefficientH * CH - CM;
            AngleCos = (AHtangent * ALtangent + BHtangent * BLtangent + CHtangent * CLtangent) / (Math.sqrt(AHtangent * AHtangent + BHtangent * BHtangent + CHtangent * CHtangent) * Math.sqrt(ALtangent * ALtangent + BLtangent * BLtangent + CLtangent * CLtangent));
            AngleCos = Math.acos(AngleCos);            
            ANormalLine = BHtangent * CLtangent - CHtangent * BLtangent;
            BNormalLine = 0 - (AHtangent * CLtangent - CHtangent * ALtangent);
            CNormalLine = AHtangent * BLtangent - BHtangent * ALtangent;
            if (AM != 0)
                OrientationValue = ANormalLine / AM;
            else if (BM != 0)
                OrientationValue = BNormalLine / BM;
            else
                OrientationValue = CNormalLine / CM;
            if (OrientationValue > 0) {
                Sum1 += AngleCos;
                Count1++;
            }
            else {
                Sum2 += AngleCos;
                Count2++;
            }
        }        
        double tempSum1, tempSum2;
        tempSum1 = Sum1 + (2 * Math.PI * Count2 - Sum2);
        tempSum2 = (2 * Math.PI * Count1 - Sum1) + Sum2;
        if (Sum1 > Sum2) {
            if ((tempSum1 - (Count - 2) * Math.PI) < 1)
                Sum = tempSum1;
            else
                Sum = tempSum2;
        }
        else {
            if ((tempSum2 - (Count - 2) * Math.PI) < 1)
                Sum = tempSum2;
            else
                Sum = tempSum1;
        }
        totalArea = (Sum - (Count - 2) * Math.PI) * Radius * Radius;

        return totalArea; //返回总面积
    }
   

}