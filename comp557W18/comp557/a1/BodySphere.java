package comp557.a1;

import javax.vecmath.Tuple3d;

import org.w3c.dom.Element;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BodySphere extends DAGNode{

	Double tx;
	Double ty;
	Double tz;
	Double sx;
	Double sy;
	Double sz;
	Double cr;
	Double cg;
	Double cb;
		
	public BodySphere( String name ) {
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
		glut.glutSolidSphere(1, 300, 300);
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
	
	public void setElement(Element element) {
		element.setAttribute("type", "bodysphere");
		element.setAttribute("name", name);
		element.setAttribute("position", tx + " " + ty + " " + tz);
		element.setAttribute("scale",  sx + " " + sy + " " + sz);
		element.setAttribute("color", cr + " " + cg + " " + cb);
	}
}
