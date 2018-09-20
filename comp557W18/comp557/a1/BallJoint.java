package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJoint extends DAGNode {
	
	DoubleParameter firstAngle;
	DoubleParameter secondAngle;
	
	Double tx;
	Double ty;
	Double tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public BallJoint( String name, double firstAngleMin, double firstAngleMax, double secondAngleMin, double secondAngleMax , double thirdAngleMin, double thirdAngleMax) {
		super(name);
		tx = 0.0;
		ty = 0.0;
		tz = 0.0;
		dofs.add( rx = new DoubleParameter( name+" rx", 0, firstAngleMin, firstAngleMax ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, secondAngleMin, secondAngleMax ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, thirdAngleMin, thirdAngleMax ) );
	}
	
	public BallJoint( String name, Tuple3d xx, Tuple3d yy, Tuple3d zz) {
		super(name);
		tx = 0.0;
		ty = 0.0;
		tz = 0.0;
		dofs.add( rx = new DoubleParameter( name+" rx", 0, xx.x, xx.y ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, yy.x, yy.y ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, zz.x, zz.y ) );
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glTranslated(tx, ty, tz);
		gl.glRotated(rx.getValue(), 1, 0, 0);
		gl.glRotated(ry.getValue(), 0, 1, 0);
		gl.glRotated(rz.getValue(), 0, 0, 1);
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
		ry.setDefaultValue(tuple3dAttr.y);
		rz.setDefaultValue(tuple3dAttr.z);
		
	}

}
