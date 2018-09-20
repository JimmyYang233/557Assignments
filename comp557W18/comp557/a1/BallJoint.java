package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJoint extends DAGNode {
	
	DoubleParameter firstAngle;
	DoubleParameter secondAngle;
	
	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public BallJoint( String name, double firstAngleMin, double firstAngleMax, double secondAngleMin, double secondAngleMax , double thirdAngleMin, double thirdAngleMax) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -4, 4 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -4, 4 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -4, 4 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, firstAngleMin, firstAngleMax ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, secondAngleMin, secondAngleMax ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, thirdAngleMin, thirdAngleMax ) );
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

}
