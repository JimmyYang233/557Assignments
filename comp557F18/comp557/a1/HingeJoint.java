package comp557.a1;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class HingeJoint extends DAGNode{


	Double tx;
	Double ty;
	Double tz;
	DoubleParameter rx;
		
	public HingeJoint( String name , double angleMin, double angleMax) {
		super(name);
		tx = 0.0;
		ty = 0.0;
		tz = 0.0;
		dofs.add( rx = new DoubleParameter( name+" rx", 0, angleMin, angleMax) );		
	}
	
	public HingeJoint( String name , Tuple3d t) {
		super(name);
		tx = 0.0;
		ty = 0.0;
		tz = 0.0;
		dofs.add( rx = new DoubleParameter( name+" rx", 0, t.x, t.y) );		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glTranslated(tx, ty, tz);
		gl.glRotated(rx.getValue(), 1, 0, 0);
		super.display(drawable);
		gl.glPopMatrix();
        
	
	}

	public void setPosition(Tuple3d tuple3dAttr) {
		tx = tuple3dAttr.x;
		ty = tuple3dAttr.y;
		tz = tuple3dAttr.z;
		
	}

	public void setAxis(Tuple3d tuple3dAttr) {
		rx.setDefaultValue(tuple3dAttr.x);
		
	}
	
	public Element setElement(Document doc, Element parent) {
		Element element = doc.createElement("node");
		element.setAttribute("type", "hingejoint");
		element.setAttribute("name", name);
		element.setAttribute("position", tx + " " + ty + " " + tz);
		element.setAttribute("limitx", rx.getMinimum() + " " + rx.getMaximum() + " 0");
		element.setAttribute("axis", rx.getDefaultValue() + " 0 0");
		parent.appendChild(element);
		return element;
	}
}
