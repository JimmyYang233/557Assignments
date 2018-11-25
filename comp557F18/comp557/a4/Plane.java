package comp557.a4;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import jogamp.nativewindow.Debug;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 4: intersection of ray with plane
    	Point3d l0 = ray.eyePoint;
    	Vector3d l = ray.viewDirection;
    	Point3d p0 = new Point3d(0,0,0);
    	double ln = l.x*n.x+l.y*n.y+l.z*n.z;
    	if(ln != 0) {
    		result.t = ((p0.x-l0.x)*n.x+(p0.y-l0.y)*n.y+(p0.z*l0.z)*n.z)/ln;
    		//System.out.println(result.t);
    		if(result.t>=0) {
    			//compute p
        		result.p.x = l0.x + result.t*l.x;
        		result.p.y = l0.y + result.t*l.y;
        		result.p.z = l0.z + result.t*l.z;
        		//compute n
        		result.n = new Vector3d(0,1,0);
        		int x = (int) result.p.x;
        		if(result.p.x<0) {
        			x--;
        		}
        		int z = (int) result.p.z;
        		if(result.p.z<0) {
        			z--;
        		}
        		if((x%2==0&&z%2==0)||(x%2!=0&&z%2!=0)) {
        			result.material = new Material();
            		result.material.diffuse = new Color4f(this.material.diffuse);
            		result.material.specular = new Color4f(this.material.specular);
            		result.material.shinyness = this.material.shinyness;
        		}
        		else {
        			result.material = new Material();
            		result.material.diffuse = new Color4f(this.material2.diffuse);
            		result.material.specular = new Color4f(this.material2.specular);
            		result.material.shinyness = this.material2.shinyness;
        		}
    		}
    	}
    }
    
}
