package comp557.a3;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector4d;

/**
 * A class to store information concerning mesh simplificaiton
 * that is common to a pair of half edges.  Speicifically, 
 * the error metric, optimal vertex location on collapse, 
 * and the error.
 * @author kry
 */
public class Edge implements Comparable<Edge> {
	
	/** One of the two half edges */
	HalfEdge he;
	
	/** Optimal vertex location on collapse */
	Vector4d v = new Vector4d();
	
	/** Error metric for this edge */
	Matrix4d Q = new Matrix4d();
	
	/** The error involved in performing the collapse of this edge */
	double error;
	
	@Override
	public int compareTo(Edge o) {
		if (error < o.error ) return -1;
		if (error > o.error ) return 1;
		return 0;
	}
	
	public void recompute() {
		Vertex vi = he.head;
		Vertex vj = he.twin.head;
		
		/** recompute Q*/
		Vertex m = HEDS.getMiddlePoint(vi, vj);
    	double Qreg44 = m.p.x*m.p.x+m.p.y*m.p.y + m.p.z*m.p.z;
    	Matrix4d Qreg = new Matrix4d(new double[]{
    		1,0,0,-m.p.x,
    		0,1,0,-m.p.y,
    		0,0,1,-m.p.z,
    		-m.p.x,-m.p.y,-m.p.z, Qreg44
    	});
    	Qreg.mul(0.01);
    	Matrix4d totalQ = new Matrix4d();
    	totalQ.add(vi.Q);
    	totalQ.add(vj.Q);
    	totalQ.add(Qreg);
    	Q = totalQ;
    	
    	/** recompute v */
    	Matrix3d A = new Matrix3d(new double[]{
    		totalQ.m00, totalQ.m01, totalQ.m02,
    		totalQ.m10, totalQ.m11, totalQ.m12,
    		totalQ.m20, totalQ.m21, totalQ.m22
    	});
    	
    	Point3d b = new Point3d(new double[] {
    		-totalQ.m03, -totalQ.m13, -totalQ.m23	
    	});
    	A.invert();
    	Vertex result = new Vertex();
    	result.p.x = A.m00*b.x+A.m01*b.y+A.m02*b.z;
    	result.p.y = A.m10*b.x+A.m11*b.y+A.m12*b.z;
    	result.p.z = A.m20*b.x+A.m21*b.y+A.m22*b.z;
    	v = new Vector4d(result.p.x, result.p.y, result.p.z, 1); 
    	
    	/** recompute r*/
    	Point4d tmp = new Point4d();
    	tmp.x = v.x*Q.m00+v.y*Q.m10+v.z*Q.m20+ v.w*Q.m30;
    	tmp.y = v.x*Q.m01+v.y*Q.m11+v.z*Q.m21+ v.w*Q.m31;
    	tmp.z = v.x*Q.m02+v.y*Q.m12+v.z*Q.m22+ v.w*Q.m32;
    	tmp.w = v.x*Q.m03+v.y*Q.m13+v.z*Q.m23+ v.w*Q.m33;
    	error = tmp.x*v.x+tmp.y*v.y+tmp.z*v.z+tmp.w*v.w;
	}
	
}
