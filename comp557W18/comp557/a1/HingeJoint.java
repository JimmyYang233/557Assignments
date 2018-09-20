package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class HingeJoint extends DAGNode{


	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
		
	public HingeJoint( String name , double angleMin, double angleMax) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -4, 4 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -4, 4 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -4, 4 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, angleMin, angleMax) );		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glTranslated(tx.getValue(), ty.getValue(), tz.getValue());
		gl.glRotated(rx.getValue(), 1, 0, 0);
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
		
	}
}
