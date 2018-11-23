package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    	Point3d c = center;
    	//System.out.println(center);
    	double r = 1;
    	Point3d o = ray.eyePoint;
    	//System.out.println(ray.viewDirection);
    	Vector3d l = ray.viewDirection;
    	double a = l.x*l.x+l.y*l.y+l.z*l.z;
    	double b = (l.x*(o.x-c.x)+l.y*(o.y-c.y)+l.z*(o.z-c.z));
    	double cc = (o.x-c.x)*(o.x-c.x)+(o.y-c.y)*(o.y-c.y)+(o.z-c.z)*(o.z-c.z)-r*r;
    	double triangle = b*b-a*cc;
    	System.out.println(a+ ", " + b + ", " + cc);
    	if(triangle<0) {
    		result.p = null;
    	}
    }
    
}
