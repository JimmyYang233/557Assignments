package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a4.PolygonSoup.Vertex;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		//System.out.println(soup.faceList.size());
		//System.out.println(soup.vertexList.size());
		// TODO: Objective 9: ray triangle intersection for meshes
		for(int[] face : soup.faceList) {
			Vertex v1 = soup.vertexList.get(face[0]);
			Vertex v2 = soup.vertexList.get(face[1]);
			Vertex v3 = soup.vertexList.get(face[2]);
			v1.n = new Vector3d(v2.p.x-v1.p.x, v2.p.y-v1.p.y, v2.p.z-v1.p.z);
			v2.n = new Vector3d(v3.p.x-v2.p.x, v3.p.y-v2.p.y, v3.p.z-v2.p.z);
			v3.n = new Vector3d(v1.p.x-v3.p.x, v1.p.y-v3.p.y, v1.p.z-v3.p.z);
			Vector3d negatev3 = new Vector3d(-v3.n.x, -v3.n.y, -v3.n.z);
			Vector3d n1 = v1.n;
			//System.out.println(v1.n);
			Vector3d n = new Vector3d();
			n.cross(n1, negatev3);
			n.normalize();
			Point3d l0 = ray.eyePoint;
	    	Vector3d l = ray.viewDirection;
	    	Point3d p0 = v1.p;
	    	double ln = l.x*n.x+l.y*n.y+l.z*n.z;
	    	if(ln != 0) {
	    		double t = ((p0.x-l0.x)*n.x+(p0.y-l0.y)*n.y+(p0.z-l0.z)*n.z)/ln;
	    		//System.out.println(result.t);
	    		if(t>0&&(t<result.t)) {
	    			//intersect with plane;
	    			//System.out.println("Was here");
	    			Point3d p = new Point3d();
	    			p.x = l0.x+l.x*t;
	    			p.y = l0.y+l.y*t;
	    			p.z = l0.z+l.z*t;
	    			Point3d a = v1.p;
	    			Point3d b = v2.p;
	    			Point3d c = v3.p;
	    			Vector3d ba = new Vector3d(b.x-a.x, b.y-a.y, b.z-a.z);
	    			Vector3d xa = new Vector3d(p.x-a.x, p.y-a.y, p.z-a.z);
	    			Vector3d cb = new Vector3d(c.x-b.x, c.y-b.y, c.z-b.z);
	    			Vector3d xb = new Vector3d(p.x-b.x, p.y-b.y, p.z-b.z);
	    			Vector3d ac = new Vector3d(a.x-c.x, a.y-c.y, a.z-c.z);
	    			Vector3d xc = new Vector3d(p.x-c.x, p.y-c.y, p.z-c.z);
	    			Vector3d cross1 = new Vector3d();
	    			cross1.cross(ba, xa);
	    			Vector3d cross2 = new Vector3d();
	    			cross2.cross(cb, xb);
	    			Vector3d cross3 = new Vector3d();
	    			cross3.cross(ac, xc);
	    			double cross1N = n.x*cross1.x+n.y*cross1.y+n.z*cross1.z;
	    			double cross2N = n.x*cross2.x+n.y*cross2.y+n.z*cross2.z;
	    			double cross3N = n.x*cross3.x+n.y*cross3.y+n.z*cross3.z;
	    			//System.out.println(cross1N +", " + cross2N + ", " + cross3N);
	    			if(cross1N<0||cross2N<0||cross3N<0) {
	    				//do-nothing
	    			}
	    			else
	    			{
	    				result.t = t;
	    				//System.out.println("Was here");
	    				result.material = new Material(this.material);
	    	    		result.p = p;
	    	    		result.n = n;
	    			}
	    		}
	    	}
		}
		
		
	}

}
