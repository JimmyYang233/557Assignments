package comp557.a1;

import javax.vecmath.Tuple3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BodyBox extends DAGNode{
	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter sx;
	DoubleParameter sy;
	DoubleParameter sz;
	DoubleParameter cr;
	DoubleParameter cg;
	DoubleParameter cb;
		
	public BodyBox( String name,double x, double y,double z, double x2, double y2, double z2 ) {
		super(name);
		dofs.add(tx = new DoubleParameter( name+" tx", 0, -4, 4 )) ;		
		tx.setDefaultValue(x);
		dofs.add(ty = new DoubleParameter( name+" ty", 0, -4, 4 ) );
		ty.setDefaultValue(y);
		dofs.add(tz = new DoubleParameter( name+" tz", 0, -4, 4 ) );
		tz.setDefaultValue(z);
		dofs.add(sx = new DoubleParameter( name+" sx", 0, 0, 4 ) );	
		sx.setDefaultValue(x2);
		dofs.add(sy = new DoubleParameter( name+" sy", 0, 0, 4 ));
		sy.setDefaultValue(y2);
		dofs.add(sz = new DoubleParameter( name+" sz", 0, 0, 4 ) );
		sz.setDefaultValue(z2);
		dofs.add(cr = new DoubleParameter( name+" cr", 0, 0, 255 )) ;
		cr.setDefaultValue((double) 255);
		dofs.add(cg = new DoubleParameter( name+" cg", 0, 0, 255 ) );
		dofs.add(cb = new DoubleParameter( name+" cb", 0, 0, 255 ) );
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glScaled(sx.getValue(), sy.getValue(), sz.getValue());
		gl.glTranslated(tx.getValue(), ty.getValue(), tz.getValue());
		gl.glColor3d(cr.getValue(), cg.getValue(), cb.getValue());
		glut.glutSolidCube(1);
		gl.glPopMatrix();
		super.display(drawable);
	}
	

	public void setCentre(Tuple3d t) {
		System.out.println(t.x +" " + t.y + " " + t.z);
		tx.setDefaultValue(t.x);
		ty.setDefaultValue(t.y);
		tz.setDefaultValue(t.z);
		System.out.println(tx.getValue() +" " + ty.getValue() + " " + tz.getValue());
		
	}

	public void setScale(Tuple3d t) {
		sx.setDefaultValue(t.x);
		sy.setDefaultValue(t.y);
		sz.setDefaultValue(t.z);
		
	}

	public void setColor(Tuple3d t) {
		cr.setDefaultValue(t.x);
		cg.setDefaultValue(t.y);
		cb.setDefaultValue(t.z);
	}
}
