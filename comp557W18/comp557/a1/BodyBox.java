package comp557.a1;

import javax.vecmath.Tuple3d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BodyBox extends DAGNode{
	Double tx;
	Double ty;
	Double tz;
	Double sx;
	Double sy;
	Double sz;
	Double cr;
	Double cg;
	Double cb;
		
	public BodyBox( String name,double x, double y,double z, double x2, double y2, double z2 ) {
		super(name);
		tx = x;
		ty = y;
		tz = z;
		sx = x2;
		sy = y2;
		sz = z2;
		cr = 255.0;
		cg = 0.0;
		cb = 0.0;
	}
	
	public BodyBox( String name) {
		super(name);
		tx = 0.0;
		ty = 0.0;
		tz = 0.0;
		sx = 0.0;
		sy = 0.0;
		sz = 0.0;
		cr = 255.0;
		cg = 0.0;
		cb = 0.0;
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glScaled(sx, sy, sz);
		gl.glTranslated(tx, ty, tz);
		gl.glColor3d(cr, cg, cb);
		glut.glutSolidCube(1);
		gl.glPopMatrix();
		super.display(drawable);
	}
	

	public void setCentre(Tuple3d t) {
		System.out.println(t.x +" " + t.y + " " + t.z);
		tx = t.x;
		ty = t.y;
		tz = t.z;
		//System.out.println(tx.getValue() +" " + ty.getValue() + " " + tz.getValue());
		
	}

	public void setScale(Tuple3d t) {
		sx = t.x;
		sy = t.y;
		sz = t.z;
		
	}

	public void setColor(Tuple3d t) {
		cr = t.x;
		cg = t.y;
		cb = t.z;
	}
	
	public Element setElement(Document doc, Element parent) {
		Element element = doc.createElement("geom");
		element.setAttribute("type", "bodybox");
		element.setAttribute("name", name);
		element.setAttribute("position", tx + " " + ty + " " + tz);
		element.setAttribute("scale",  sx + " " + sy + " " + sz);
		element.setAttribute("color", cr + " " + cg + " " + cb);
	}
}
