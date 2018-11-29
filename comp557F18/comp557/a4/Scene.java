package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
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
        
        FastPoissonDisk disk = new FastPoissonDisk(0.05);
        FastPoissonDisk lightDisk = new FastPoissonDisk(0.05);
        
        for ( int i = 0; i < h && !render.isDone(); i++ ) {
            for ( int j = 0; j < w && !render.isDone(); j++ ) {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)       	
            	double[] offset = new double[] {-0.5, 0.5};
            	int finalr = 0;
            	int finalg = 0;
            	int finalb = 0;
            	int finala = 255;
            	//System.out.println(render.samples);
            	//render.samples = 1;            	
            	for(int k = 0; k<render.samples;k++) {
            		for(int m = 0;m<render.samples;m++) {
            			//ANTI-Aliasing;
        				//Depth Of field
            			double x = 0;
            			double y = 0;
            			if(cam.isDepthOfField) {
            				Point2d p = new Point2d();
            				disk.get(p, k*render.samples+m, render.samples*render.samples);
            				x = p.x*cam.lensRadius*2;
            				y = p.y*cam.lensRadius*2;
            			}
        				
        				Ray ray = new Ray();
		            	Scene.generateRay(i, j, k, m, x, y, render.samples, offset, cam, ray);
		            	
		                // TODO: Objective 2: test for intersection with scene surfaces
		            	Color3f c = new Color3f(render.bgcolor);
		            	int r = (int)(255*c.x);
		            	int g = (int)(255*c.y);
		                int b = (int)(255*c.z);
		            	IntersectResult fir = new IntersectResult();
		                //System.out.println(surfaceList.size());
		            	for(Intersectable intersectable : surfaceList) {
		            		IntersectResult ir = new IntersectResult();
		            		intersectable.intersect(ray, ir);
		            		if(ir.t<=fir.t) {
		            			fir = ir;
		            		}
		            	}
		            	
	            		// TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
	            		double lx = 0;
	            		double ly = 0;
	            		double lz = 0;
	            		if(fir.material !=null) {
	            			for(Light theLight : lights.values()) {
	            				//Area Light
	            				Light light = new Light(theLight);
	            				if(theLight.type.equals("area")) {
	            					Point2d p = new Point2d();
		            				lightDisk.get(p, k*render.samples+m, render.samples*render.samples);
		            				double lightx = p.x*light.lightRadius*2;
		            				double lightz = p.y*light.lightRadius*2;
		            				light.from.x = light.from.x+lightx;
		            				light.from.z = light.from.z+lightz;
		            				//System.out.println(light.from);
	            				}
	            				
		                		Vector3d newV = new Vector3d();
		                		newV.x = fir.p.x-light.from.x;
		                		newV.y = fir.p.y-light.from.y;
		                		newV.z = fir.p.y-light.from.z;
		                		Ray lightRay = new Ray(light.from, newV,0);
		                		Color4f l = computeShading(light, lightRay, ray, fir);
			            		lx += l.x;
			                	ly += l.y;
			                	lz += l.z;
			                	r = (int)(Math.min(1, lx)*255);
				              	g = (int)(Math.min(1, ly)*255);
				              	b = (int)(Math.min(1, lz)*255);
		                	}
	            		}
		            	finalr = finalr + r;
		            	finalg = finalg + g;
		            	finalb = finalb + b;
            		}
            	}
	            		
            	
                
            	// TODO: Objective 8: do antialiasing by sampling more than one ray per pixel
            	finalr = finalr/(render.samples*render.samples);
            	finalg = finalg/(render.samples*render.samples);
            	finalb = finalb/(render.samples*render.samples);
            	
                int argb = (finala<<24 | finalr<<16 | finalg<<8 | finalb);    
                
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
	public static void generateRay(final int i, final int j, final int k, final int l, final double x, final double y, final int total, final double[] offset, final Camera cam, Ray ray) {
		
		double offsets = -offset[0]+offset[1];
		double pixel = offsets/total;
		double halfPixel = pixel/2;
		//System.out.println(offsets);
		double offseti = halfPixel+k*pixel;
		double offsetj = halfPixel+l*pixel;
		//System.out.println(offseti + ", "+ offsetj);
		Point3d e = new Point3d(cam.from.x+x, cam.from.y+y, cam.from.z);
		//System.out.println(e);
		double d = e.z;
		double angle = cam.fovy*Math.PI/180;
		double t = d*Math.tan(angle/2);
		double b = -t;
		double aspectRatio = (double)cam.imageSize.width/(double)cam.imageSize.height;
		//System.out.println(aspectRatio);
		double ll = b*aspectRatio;
		double r = t*aspectRatio;
		//System.out.println(l +", " + r + ", " +  ", " + b + ", " + t);
		double nx = cam.imageSize.width;
		double ny = cam.imageSize.height;
		double u = ll+((r-ll)*(j+offseti)/nx);
		double v = t+((b-t)*(i+offsetj)/ny);
			
		Vector3d cw = new Vector3d(e.x-cam.to.x, e.y-cam.to.y, e.z-cam.to.z);
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
	
	public Color4f computeShading(Light light, Ray lightRay, Ray ray, IntersectResult fir) {
		Color4f la = ambientShading(fir);
		float lx = la.x;
    	float ly = la.y;
    	float lz = la.z;
        
    		
		IntersectResult shadResult = new IntersectResult();
		Ray shadowRay = new Ray();
		if(!inShadow(fir, light, surfaceList, shadResult, shadowRay)) {
			Color4f ld = lambertianShading(light,lightRay, fir);
			lx += ld.x;
			ly += ld.y;
			lz += ld.z;
			
			//System.out.println(fir.material.refractable);
			
			if(fir.material.reflectable) {
				Color4f lm = mirrorReflection(light, surfaceList, fir, ray);
				lx += lm.x;
				ly += lm.y;
				lz += lm.z;
			}
			else if(fir.material.refractable) {
				Color4f lm = refraction(light, surfaceList, fir, ray);
				lx += lm.x;
				ly += lm.y;
				lz += lm.z;
			}
			else {
				Color4f ls = specularShading(light, lightRay, ray, fir);
    			lx += ls.x;
    			ly += ls.y;
    			lz += ls.z;
			}
		}

    	Color4f result = new Color4f(lx, ly, lz, 1);
		return result;
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
		shadowRay = new Ray(result.p, new Vector3d(light.from.x-result.p.x, light.from.y-result.p.y, light.from.z-result.p.z), 0);
		Point3d p = shadowRay.eyePoint;
		Vector3d d = shadowRay.viewDirection;
		Point3d finalP = new Point3d(p.x+0.0001*d.x, p.y+0.0001*d.y, p.z+0.0001*d.z);
		shadowRay.eyePoint = finalP;
		for(Intersectable intersectable : sl) {
			intersectable.intersect(shadowRay, shadowResult);
			if(shadowResult.material!=null&&(shadowResult.material.refractable==false||result.material.refractable==false)) {
				//System.out.println(shadowResult.p + ", " + result.p);
				return true;
			}
		}
		
		return false;
	}
	
	
	public Color4f lambertianShading(Light light,Ray lightRay, IntersectResult ir) {
    	Vector3d n = ir.n;
    	//System.out.println(n);
		Vector3d l = new Vector3d();
		l.x = -lightRay.viewDirection.x;
		l.y = -lightRay.viewDirection.y;
		l.z = -lightRay.viewDirection.z;
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
	
	public Color4f specularShading(Light light, Ray lightRay, Ray ray, IntersectResult ir) {
		Vector3d n = ir.n;
		n.normalize();
		Vector3d l = new Vector3d();
		l.x = -lightRay.viewDirection.x;
		l.y = -lightRay.viewDirection.y;
		l.z = -lightRay.viewDirection.z;
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
	
	public Color4f mirrorReflection(Light light, List<Intersectable> surfaceList, IntersectResult ir, Ray ray) {
		Color4f ans = new Color4f();
		if(ir.material.numOfReflect>5) {
			ans.x = ir.material.diffuse.x;
			ans.y = ir.material.diffuse.y;
			ans.z = ir.material.diffuse.z;
		}
		else {
			Ray reflectRay = generateBouncedRay(ray, ir);
			IntersectResult fir = new IntersectResult();
			for(Intersectable intersectable : surfaceList) {
				IntersectResult rir = new IntersectResult();
				intersectable.intersect(reflectRay, rir);
				if(rir.material!=null) {
					if(rir.t<fir.t) {
						fir = rir;
					//if(rir.material.numOfReflect<=4) {
					
					}
					
				}
				
			}
			if(fir.material!=null) {
				fir.material.numOfReflect = ir.material.numOfReflect+1;
				Vector3d newV = new Vector3d();
				newV.x = fir.p.x-light.from.x;
				newV.y = fir.p.y-light.from.y;
				newV.z = fir.p.y-light.from.z;
				Ray lightRay = new Ray(light.from, newV, 0);
				//Ray newRay = new Ray(ray.eyePoint, new Vector3d(fir.p.x-ray.eyePoint.x, fir.p.y-ray.eyePoint.y, fir.p.z-ray.eyePoint.z));
				ans = computeShading(light, lightRay, reflectRay, fir);
				//ans = fir.material.diffuse;
				ans.x = ans.x*ir.material.specular.x;
				ans.y = ans.y*ir.material.specular.y;
				ans.z = ans.z*ir.material.specular.z;
			}
		}		
		return ans;
	}
	
	public Color4f refraction(Light light, List<Intersectable> surfaceList, IntersectResult ir, Ray ray) {
		Color4f ans = new Color4f();
		//System.out.println(ir.p);
		double n1 = ray.n1;
		double n2 = 1;
		Vector3d n = new Vector3d(ir.n.x, ir.n.y, ir.n.z);
		Vector3d negN = new Vector3d(-ir.n.x, -ir.n.y, -ir.n.z);
		Vector3d nn;
		if(n1==1) {
			//ray outside of object;
			n2 = ir.material.refractableN;
			nn = new Vector3d(negN.x, negN.y, negN.z);
		}
		else {
			//ray inside of object;
			nn = new Vector3d(n.x, n.y, n.z);
		}
		//System.out.println(ir.n);
		
		Vector3d I = ray.viewDirection;
		//System.out.println(I +", "  + n);
		double cos1 = nn.dot(I);
//		if(cos1<0) {
//			cos1 = -cos1;
//		}
		double angle = Math.acos(cos1);
		double r = n1/n2;
		double k = 1-r*r*(1-cos1*cos1);
		if(k<=0) {
			ans = mirrorReflection(light, surfaceList, ir, ray);
		}
		else {
			double c2 = Math.sqrt(k);
			Vector3d T = new Vector3d(r*I.x+(r*cos1-c2)*-nn.x, r*I.y+(r*cos1-c2)*-nn.y, r*I.z+(r*cos1-c2)*-nn.z);
			Point3d finalP = new Point3d(ir.p.x+0.0001*T.x, ir.p.y+0.0001*T.y, ir.p.z+0.0001*T.z);
			//nn.negate();
			double cos2 = nn.dot(T);
			double newAngle = Math.acos(cos2);
			double FR = fresnelEquation(n1, n2, angle, newAngle);
			double FT = 1-FR;
//			Color4f ans1 = mirrorReflection(light, surfaceList, ir, ray);
//			ans.x += (float) (ans1.x*ir.material.specular.x*FR);
//			ans.y += (float) (ans1.y*ir.material.specular.y*FR);
//			ans.z += (float) (ans1.z*ir.material.specular.z*FR);
			Ray refractRay = new Ray(finalP, T, n2);
			IntersectResult firT = new IntersectResult();
			for(Intersectable intersectable : surfaceList) {
				IntersectResult rirT = new IntersectResult();
				intersectable.intersect(refractRay, rirT);
				if(rirT.material!=null) {
					if(rirT.t<firT.t) {
						firT = rirT;
					}
				}
			}
			if(firT.material!=null) {
				Vector3d newV = new Vector3d();
				newV.x = firT.p.x-light.from.x;
				newV.y = firT.p.y-light.from.y;
				newV.z = firT.p.y-light.from.z;
				Ray lightRay = new Ray(light.from, newV, 0);
				//Ray newRay = 
				Color4f tmpans = computeShading(light, lightRay, refractRay, firT);
				//ans = fir.material.diffuse;
				ans.x += (float) (tmpans.x*ir.material.specular.x);
				ans.y += (float) (tmpans.y*ir.material.specular.y);
				ans.z += (float) (tmpans.z*ir.material.specular.z);
			}
		}
		return ans;
	}
	
	public Ray generateBouncedRay(Ray ray, IntersectResult ir) {
		Vector3d n = ir.n;
		Vector3d v = new Vector3d(-ray.viewDirection.x, -ray.viewDirection.y, -ray.viewDirection.z);
		Matrix3d nn2 = new Matrix3d(new double[] {
				2*n.x*n.x, 2*n.x*n.y, 2*n.x*n.z,
				2*n.y*n.x, 2*n.y*n.y, 2*n.y*n.z,
				2*n.z*n.x, 2*n.z*n.y, 2*n.z*n.z
		});
		Vector3d nn2v = new Vector3d(nn2.m00*v.x+nn2.m01*v.y+nn2.m02*v.z, nn2.m10*v.x+nn2.m11*v.y+nn2.m12*v.z, nn2.m20*v.x+nn2.m21*v.y+nn2.m22*v.z);
		Vector3d r = new Vector3d(nn2v.x-v.x, nn2v.y-v.y, nn2v.z-v.z);
		Point3d finalP = new Point3d(ir.p.x+0.00000001*r.x, ir.p.y+0.00000001*r.y, ir.p.z+0.00000001*r.z);
		Ray reflectRay = new Ray(finalP, r, ray.n1);
		return reflectRay;
	}
	
	public double fresnelEquation(double n1, double n2, double angle1, double angle2) {
		double ans = 0;
		double Fr1 = Math.pow((n2*Math.cos(angle1)-n1*Math.cos(angle2))/(n2*Math.cos(angle1)+n1*Math.cos(angle2)), 2);
		double Fr2 = Math.pow((n1*Math.cos(angle2)-n2*Math.cos(angle1))/(n1*Math.cos(angle2)+n2*Math.cos(angle1)), 2);
		ans = (Fr1+Fr2)/2;
		if(ans>1) {
			System.out.println(ans);
			System.out.println(angle1 +", " + angle2);
			ans = 1;
		}
		//System.out.println(ans);
		return ans;
	}
}
