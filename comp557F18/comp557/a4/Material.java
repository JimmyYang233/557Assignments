package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;

/**
 * A class defining the material properties of a surface, 
 * such as colour and specularity. 
 */
public class Material {
	
	public int numOfReflect = 0;
	
	public boolean reflectable = false;
	
	public boolean refractable = false;
	
	public double refractableN = 1;
	
	/** Static member to access all the materials */
	public static Map<String,Material> materialMap = new HashMap<String,Material>();
	
	/** Material name */
    public String name = "";
    
    /** Diffuse colour, defaults to white */
    public Color4f diffuse = new Color4f(1,1,1,1);
    
    /** Specular colour, default to black (no specular highlight) */
    public Color4f specular = new Color4f(0,0,0,0);
    
    /** Specular hardness, or exponent, default to a reasonable value */ 
    public float shinyness = 64;
 
    /**
     * Default constructor
     */
    public Material() {
    	// do nothing
    }
    
    /**
     * Copy constructor
     * @param material
     */
    public Material(Material material) {
    	
    	this.name = material.name;
		this.diffuse = new Color4f(material.diffuse);
		this.specular = new Color4f(material.specular);
		this.shinyness = material.shinyness;
		this.reflectable = material.reflectable;
		this.refractable = material.refractable;
		this.refractableN = material.refractableN;
    }
    
}
