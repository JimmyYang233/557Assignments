package comp557.a4;

import javax.vecmath.Color4f;
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
    	double r = radius;
    	Point3d o = ray.eyePoint;
    	//System.out.println(ray.viewDirection);
    	Vector3d l = ray.viewDirection;
    	double a = l.x*l.x+l.y*l.y+l.z*l.z;
    	double b = 2*(l.x*(o.x-c.x)+l.y*(o.y-c.y)+l.z*(o.z-c.z));
    	double cc = (o.x-c.x)*(o.x-c.x)+(o.y-c.y)*(o.y-c.y)+(o.z-c.z)*(o.z-c.z)-r*r;
    	double triangle = b*b-4*a*cc;
    	//System.out.println(a+ ", " + b + ", " + cc);
    	if(triangle<0) {
    		//no intersection, material same as the bgcolor;
    		result.material = null;	
    	}
    	else if (triangle == 0) {
    		//1 intersection, just find it.
    		result.material = new Material(this.material);
    		//compute t
    		result.t = -b/(2*a);
    		if(result.t<0) {
    			result.material = null;
    		}
    		//compute p
    		result.p.x = o.x + result.t*l.x;
    		result.p.y = o.y + result.t*l.y;
    		result.p.z = o.z + result.t*l.z;
    		//compute n
    		result.n.x = result.p.x-center.x;
    		result.n.y = result.p.y-center.y;
    		result.n.z = result.p.z-center.z;
    		result.n.normalize();
    	}
    	
    	else if(triangle>0) {
    		// 2 intersections, find the closest one.
    		result.material = new Material(this.material);
    		//compute t
    		double t1= -(b+Math.sqrt(triangle))/(2*a);
    		double t2 = -(b-Math.sqrt(triangle))/(2*a);
    		if(t1>0&&t2>0) {
    			if(t1<t2) {
    				result.t = t1;
    			}
    			else {
    				result.t = t2;
    			}
    			//compute p
        		result.p.x = o.x + result.t*l.x;
        		result.p.y = o.y + result.t*l.y;
        		result.p.z = o.z + result.t*l.z;
        		//compute n
    			result.n.x = result.p.x-center.x;
        		result.n.y = result.p.y-center.y;
        		result.n.z = result.p.z-center.z;
        		result.n.normalize();
    		}
    		else if(t1<=0&&t2>0) {
    			result.t = t2;
    			//compute p
        		result.p.x = o.x + result.t*l.x;
        		result.p.y = o.y + result.t*l.y;
        		result.p.z = o.z + result.t*l.z;
        		//compute n
    			result.n.x = -result.p.x+center.x;
        		result.n.y = -result.p.y+center.y;
        		result.n.z = -result.p.z+center.z;
        		result.n.normalize();
    		}
    		else if(t1>0&&t2<=0) {
    			result.t = t1;
    			//compute p
        		result.p.x = o.x + result.t*l.x;
        		result.p.y = o.y + result.t*l.y;
        		result.p.z = o.z + result.t*l.z;
        		//compute n
    			result.n.x = -result.p.x+center.x;
        		result.n.y = -result.p.y+center.y;
        		result.n.z = -result.p.z+center.z;
        		result.n.normalize();
    		}
    		else {
    			result.material = null;
    		}
    		
    		
    		
    	}
    	
    }
    
}
