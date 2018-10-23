package comp557.a1;

import javax.vecmath.Tuple3d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class FreeJoint extends DAGNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public FreeJoint( String name ) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -20, 20 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -20, 20 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -20, 20 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glTranslated(tx.getValue(), ty.getValue(), tz.getValue());
		gl.glRotated(rx.getValue(), 1, 0, 0);
		gl.glRotated(ry.getValue(), 0, 1, 0);
		gl.glRotated(rz.getValue(), 0, 0, 1);
		super.display(drawable);
		gl.glPopMatrix();
        
	
	}

	public void setPosition(Tuple3d tuple3dAttr) {
		tx.setDefaultValue(tuple3dAttr.x);
		ty.setDefaultValue(tuple3dAttr.y);
		tz.setDefaultValue(tuple3dAttr.z);
		
	}

	public void setAxis(Tuple3d tuple3dAttr) {
		rx.setDefaultValue(tuple3dAttr.x);
		ry.setDefaultValue(tuple3dAttr.y);
		rz.setDefaultValue(tuple3dAttr.z);
		
	}
	
	public Element setElement(Document doc, Element parent) {
		Element element = doc.createElement("node");
		element.setAttribute("type", "freejoint");
		element.setAttribute("name", name);
		element.setAttribute("position", tx.getDefaultValue() + " " + ty.getDefaultValue()+ " " + tz.getDefaultValue());
		parent.appendChild(element);
		return element;
	}
	
}
