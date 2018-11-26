package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
        
        for ( int i = 0; i < h && !render.isDone(); i++ ) {
            for ( int j = 0; j < w && !render.isDone(); j++ ) {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	Ray ray = new Ray();
            	double[] offset = new double[2];
            	Scene.generateRay(i, j, offset, cam, ray);
            	
                // TODO: Objective 2: test for intersection with scene surfaces
            	Color3f c = new Color3f(render.bgcolor);
            	int r = (int)(255*c.x);
            	int g = (int)(255*c.y);
                int b = (int)(255*c.z);
                int a = 255;
            	IntersectResult fir = new IntersectResult();
                //System.out.println(surfaceList.size());
            	for(Intersectable intersectable : surfaceList) {
            		IntersectResult ir = new IntersectResult();
            		intersectable.intersect(ray, ir);
            		if(ir.t<=fir.t) {
            			fir = ir;
            		}
            	}
            	
            	
            	if(fir.material!=null) {
            		Color4f la = ambientShading(fir);
            		double lx = la.x;
                	double ly = la.y;
                	double lz = la.z;
                    // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                	for(Light light : lights.values()) {
                		IntersectResult shadResult = new IntersectResult();
                		Ray shadowRay = new Ray();
                		if(!inShadow(fir, light, surfaceList, shadResult, shadowRay)) {
                			Color4f ld = lambertianShading(light, fir);
                			lx += ld.x;
                			ly += ld.y;
                			lz += ld.z;
                			Color4f ls = specularShading(light, ray, fir);
                			lx += ls.x;
                			ly += ls.y;
                			lz += ls.z;
                		}
            			
                	}
	              	r = (int)(Math.min(1, lx)*255);
	              	g = (int)(Math.min(1, ly)*255);
	              	b = (int)(Math.min(1, lz)*255);
	             }
            	
                
            	// TODO: Objective 8: do antialiasing by sampling more than one ray per pixel
            	
            	
            	
	           	
            	
                int argb = (a<<24 | r<<16 | g<<8 | b);    
                
                // update the render image
                render.setPixel(j, i, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		Point3d e = cam.from;
		double d = e.z;
		double angle = cam.fovy*Math.PI/180;
		double t = d*Math.tan(angle/2);
		double b = -t;
		double aspectRatio = (double)cam.imageSize.width/(double)cam.imageSize.height;
		//System.out.println(aspectRatio);
		double l = b*aspectRatio;
		double r = t*aspectRatio;
		//System.out.println(l +", " + r + ", " +  ", " + b + ", " + t);
		double nx = cam.imageSize.width;
		double ny = cam.imageSize.height;
		double u = l+((r-l)*(j+0.5)/nx);
		double v = t+((b-t)*(i+0.5)/ny);
			
		Vector3d cw = new Vector3d(cam.from.x-cam.to.x, cam.from.y-cam.to.y, cam.from.z-cam.to.z);
		cw.normalize();
		Vector3d cu = new Vector3d();
		cu.cross(cam.up, cw);
		cu.normalize();
		Vector3d cv = new Vector3d();
		cv.cross(cw, cu);
		Vector3d cd = new Vector3d(cu.x*u+cv.x*v-cw.x*d, cu.y*u+cv.y*v-cw.y*d, cu.z*u+cv.z*v-cw.z*d);
		cd.normalize();
		ray.eyePoint = e;
		ray.viewDirection = cd;
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final Light light, final List<Intersectable> sl, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: check for shdows and use it in your lighting computation
		shadowRay = new Ray(result.p, new Vector3d(light.from.x-result.p.x, light.from.y-result.p.y, light.from.z-result.p.z));
		Point3d p = shadowRay.eyePoint;
		Vector3d d = shadowRay.viewDirection;
		Point3d finalP = new Point3d(p.x+0.001*d.x, p.y+0.001*d.y, p.z+0.001*d.z);
		shadowRay.eyePoint = finalP;
		for(Intersectable intersectable : sl) {
			intersectable.intersect(shadowRay, shadowResult);
			if(shadowResult.material!=null) {
				//System.out.println(shadowResult.p + ", " + result.p);
				return true;
			}
		}
		
		return false;
	}
	
	
	public Color4f lambertianShading(Light light, IntersectResult ir) {
		Point3d p = ir.p;
    	Vector3d n = ir.n;
    	//System.out.println(n);
		Vector3d l = new Vector3d();
		l.x = light.from.x-p.x;
		l.y = light.from.y-p.y;
		l.z = light.from.z-p.z;
		l.normalize();
		
		//I
		Color4f I = new Color4f();
		I.x = (float) (light.color.x*light.power);
		I.y = (float) (light.color.y*light.power);
		I.z = (float) (light.color.z*light.power);
		
		//kd
		Color4f kd = ir.material.diffuse;
		
		//computation
		float nl = (float) (n.x*l.x+n.y*l.y+n.z*l.z);
		Color4f result = new Color4f();
		result.x = (float) (kd.x*I.x*Math.max(0, nl));
		result.y = (float) (kd.y*I.y*Math.max(0, nl));
		result.z = (float) (kd.z*I.z*Math.max(0, nl));
		return result;
		//System.out.println(ir.material.diffuse);
	}
	
	public Color4f specularShading(Light light, Ray ray, IntersectResult ir) {
		Point3d p = ir.p;
		Vector3d n = ir.n;
		n.normalize();
		Vector3d l = new Vector3d();
		l.x = light.from.x-p.x;
		l.y = light.from.y-p.y;
		l.z = light.from.z-p.z;
		l.normalize();
		Vector3d v = new Vector3d();
		v.x = -ray.viewDirection.x;
		v.y = -ray.viewDirection.y;
		v.z = -ray.viewDirection.z;
		v.normalize();
		Vector3d h = new Vector3d();
		h.x = v.x+l.x;
		h.y = v.y+l.y;
		h.z = v.z+l.z;
		h.normalize();
		double shine = ir.material.shinyness;
		Color4f ks = ir.material.specular;
		Color4f I = new Color4f();
		I.x = (float) (light.color.x*light.power);
		I.y = (float) (light.color.y*light.power);
		I.z = (float) (light.color.z*light.power);
		//System.out.println(ir.material.specular);
		double value = Math.pow(Math.max(0, n.x*h.x+n.y*h.y+n.z*h.z), shine);
		Color4f result = new Color4f();
		result.x = (float) (ks.x*I.x*value);
		result.y = (float) (ks.y*I.y*value);
		result.z = (float) (ks.z*I.z*value);
		return result;
	}
	
	public Color4f ambientShading(IntersectResult ir) {
		Color4f result = new Color4f();
		result.x = (float) (ir.material.diffuse.x*ambient.x*0.05);
		result.y = (float) (ir.material.diffuse.y*ambient.y*0.05);
		result.z = (float) (ir.material.diffuse.z*ambient.z*0.05);
		return result;
	}
}
