package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJoint extends FreeJoint {
	
	DoubleParameter firstAngle;
	DoubleParameter secondAngle;
	
	public BallJoint(String name) {
		super(name);
		dofs.add( firstAngle = new DoubleParameter( name+" firstAngle", 0, -90, 90 ) );
		dofs.add( secondAngle = new DoubleParameter( name+" secondAngle", 0, -180, 180 ) );
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glRotated(rx.getValue(), 1, 0, 0);
		gl.glRotated(ry.getValue(), 0, 1, 0);
		super.display(drawable);
	}

}
