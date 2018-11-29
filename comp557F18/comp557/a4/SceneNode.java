package comp557.a4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a4.IntersectResult;
import comp557.a4.Intersectable;
import comp557.a4.Ray;

/**
 * The scene is constructed from a hierarchy of nodes, where each node
 * contains a transform, a material definition, some amount of geometry, 
 * and some number of children nodes.  Each node has a unique name so that
 * it can be instanced elsewhere in the hierarchy (provided it does not 
 * make loops. 
 * 
 * Note that if the material (inherited from Intersectable) for a scene 
 * node is non-null, it should override the material of any child.
 * 
 */
public class SceneNode extends Intersectable {
	
	/** Static map for accessing scene nodes by name, to perform instancing */
	public static Map<String,SceneNode> nodeMap = new HashMap<String,SceneNode>();
	
    public String name;
   
    /** Matrix transform for this node */
    public Matrix4d M;
    
    /** Inverse matrix transform for this node */
    public Matrix4d Minv;
    
    /** Child nodes */
    public List<Intersectable> children;
    
    /**
     * Default constructor.
     * Note that all nodes must have a unique name, so that they can used as an instance later on.
     */
    public SceneNode() {
    	super();
    	this.name = "";
    	this.M = new Matrix4d();
    	this.Minv = new Matrix4d();
    	this.children = new LinkedList<Intersectable>();
    }
           
    @Override
    public void intersect(Ray ray, IntersectResult result) {

    	// TODO: Objective 7: implement hierarchy with instances

    	//System.out.println(M);
    	Minv.invert(M);
    	double px = ray.eyePoint.x;
    	double py = ray.eyePoint.y;
    	double pz = ray.eyePoint.z;
    	double dx = ray.viewDirection.x;
    	double dy = ray.viewDirection.y;
    	double dz = ray.viewDirection.z;
    	double a = Minv.m00;
    	double b = Minv.m01;
    	double c = Minv.m02;
    	double d = Minv.m03;
    	double e = Minv.m10;
    	double f = Minv.m11;
    	double g = Minv.m12;
    	double h = Minv.m13;
    	double i = Minv.m20;
    	double j = Minv.m21;
    	double k = Minv.m22;
    	double l = Minv.m23;
    	Point3d newP = new Point3d(a*px+b*py+c*pz+d, e*px+f*py+g*pz+h, i*px+j*py+k*pz+l);
    	Vector3d newd = new Vector3d(a*dx+b*dy+c*dz, e*dx+f*dy+g*dz, i*dx+j*dy+k*dz);
    	ray.eyePoint = newP;
    	ray.viewDirection = newd;
    	for(Intersectable intersectable : children) {
    		IntersectResult ir = new IntersectResult(); 
    		Ray newRay = new Ray(ray.eyePoint, ray.viewDirection, 1);
    		intersectable.intersect(newRay, ir);
    		if(ir.material!=null) {
    			if(ir.t<result.t) {
        			result.t = ir.t;
        	    	result.p = ir.p;
        			result.material = new Material(ir.material);
        	    	result.n = ir.n;
        		}
    		}
    	}
    	Point3d p = result.p;
		px=p.x;
		py=p.y;
		pz=p.z;
		a=M.m00;
		b=M.m01;
		c=M.m02;
		d=M.m03;
		e=M.m10;
		f=M.m11;
		g=M.m12;
		h=M.m13;
		i=M.m20;
		j=M.m21;
		k=M.m22;
		l=M.m23;
		newP = new Point3d(a*px+b*py+c*pz+d, e*px+f*py+g*pz+h, i*px+j*py+k*pz+l);
		result.p = newP; 
		Vector3d n = result.n;
		double nx = n.x;
		double ny = n.y;
		double nz = n.z;
		Vector3d newN = new Vector3d(a*nx+b*ny+c*nz, e*nx+f*ny+g*nz, i*nx+j*ny+k*nz);
		newN.normalize();
		result.n = newN;
//    	if(result.material==null) {
//    		result.material = new Material();
//    		result.material.diffuse = new Color4f(this.material.diffuse);
//    		result.material.specular = new Color4f(this.material.specular);
//    		result.material.shinyness = this.material.shinyness;
//    	}
    }
    
}
