package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class HingeJoint extends FreeJoint{

	DoubleParameter angle;
	
	public HingeJoint(String name) {
		super(name);
		dofs.add( angle = new DoubleParameter( name+" angle", 0, -180, 0 ) );
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glRotated(angle.getValue(), 1, 0, 0);
		super.display(drawable);
	}


}
