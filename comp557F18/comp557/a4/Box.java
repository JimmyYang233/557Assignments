package comp557.a4;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
		double xMin = min.x;
		double xMax = max.x;
		double yMin = min.y;
		double yMax = max.y;
		double zMin = min.z;
		double zMax = max.z;
		double px = ray.eyePoint.x;
		double py = ray.eyePoint.y;
		double pz = ray.eyePoint.z;
		double dx = ray.viewDirection.x;
		double dy = ray.viewDirection.y;
		double dz = ray.viewDirection.z;
		double txmin = Double.MIN_VALUE;
		double txmax = Double.MAX_VALUE;
		if(dx!=0) {
			txmin = (xMin-px)/dx;
			txmax = (xMax-px)/dx;
		}		
		double tymin = Double.MIN_VALUE;
		double tymax = Double.MAX_VALUE;
		if(dy!=0) {
			tymin = (yMin-py)/dy;
			tymax = (yMax-py)/dy;
		}		
		double tzmin = Double.MIN_VALUE;
		double tzmax = Double.MAX_VALUE;
		if(dz!=0) {
			tzmin = (zMin-pz)/dz;
			tzmax = (zMax-pz)/dz;
		}		
		double txlow = Math.min(txmin,txmax);
		double tylow = Math.min(tymin, tymax);
		double tzlow = Math.min(tzmin, tzmax);
		double txhigh = Math.max(txmin, txmax);
		double tyhigh = Math.max(tymin, tymax);
		double tzhigh = Math.max(tzmin, tzmax);
		double tmin = Math.max(txlow, tylow);
		tmin = Math.max(tmin, tzlow);
		
		double tmax = Math.min(txhigh, tyhigh);
		tmax = Math.min(tmax, tzhigh);
		if(tmax<tmin) {
			result.material = null;
		}
		else {
			result.material = new Material(this.material);
    		
    		if(tmin<0) {
    			if(tmax<0) {
    				result.material = null;
    			}
    			else {
    				result.t = tmax;
    			}
    		}
    		else {
    			result.t = tmin;
    		}
    		result.p.x = ray.eyePoint.x + result.t*ray.viewDirection.x;
    		result.p.y = ray.eyePoint.y + result.t*ray.viewDirection.y;
    		result.p.z = ray.eyePoint.z + result.t*ray.viewDirection.z;
    		Point3d p = result.p;
    		if(dx==0&&(p.x<xMin||p.x>xMax)) {
    			result.material = null;
    		}
    		if(dy==0&&(p.y<yMin||p.y>yMax)) {
    			result.material = null;
    		}
    		if(dz==0&&(p.z<zMin||p.z>zMax)) {
    			result.material = null;
    		}
    		//System.out.println(tmin+ ", " + tmax);
    		//System.out.println(p);
    		if(p.x<xMin+0.00001&&p.x>xMin-0.00001) {
    			result.n = new Vector3d(-1,0,0);
    		}
    		else if(p.x<xMax+0.00001&&p.x>xMax-0.00001) {
    			result.n = new Vector3d(1,0,0);
    		}
    		else if(p.y<yMin+0.00001&&p.y>yMin-0.00001) {
    			result.n = new Vector3d(0,-1,0);
    		}
    		else if(p.y < yMax+0.00001&&p.y>yMax-0.00001) {
    			result.n = new Vector3d(0,1,0);
    		}
    		else if(p.z < zMin+0.00001&&p.z>yMin-0.00001) {
    			result.n = new Vector3d(0,0,-1);
    		}
    		else if(p.z < zMax+0.00001&&p.z>zMax-0.00001) {
    			result.n = new Vector3d(0,0,1);
    		}
    		//System.out.println(result.n);
    		//System.out.println(material.diffuse);
    		if(result.n.x==0&&result.n.y==0&&result.n.z==0&&result.material!=null) {
    			//Stop here;
    			//System.out.println("Was here");
    		}
		}
		
		
	}	

}
