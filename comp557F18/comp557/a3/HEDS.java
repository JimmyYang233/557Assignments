package comp557.a3;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Half edge data structure.
 * Maintains a list of faces (i.e., one half edge of each) to allow
 * for easy display of geometry.
 */
public class HEDS {

    /** List of faces */
    Set<Face> faces = new HashSet<Face>();
    
    /**
     * Constructs an empty mesh (used when building a mesh with subdivision)
     */
    public HEDS() {
        // do nothing
    }
        
    /**
     * Builds a half edge data structure from the polygon soup   
     * @param soup
     */
    public HEDS( PolygonSoup soup ) {
        halfEdges.clear();
        faces.clear();
        
        // TODO: Objective 1: create the half edge data structure from the polygon soup  
        for(int[] face : soup.faceList) {
        	HalfEdge hf1 = new HalfEdge();
        	HalfEdge hf2 = new HalfEdge();
        	HalfEdge hf3 = new HalfEdge();
        	
        	hf1.head = soup.vertexList.get(face[0]);
        	hf2.head = soup.vertexList.get(face[1]);
        	hf3.head = soup.vertexList.get(face[2]);
        	
        	halfEdges.put(face[2] + "," + face[0], hf1);
        	halfEdges.put(face[0] + "," + face[1], hf2);
        	halfEdges.put(face[1] + "," + face[2], hf3);
        	
        	hf1.next = hf2;
        	hf2.next = hf3;
        	hf3.next = hf1;
        	
        	Face newFace = new Face(hf1);
        	faces.add(newFace);
        }
        
    	for(String ij : halfEdges.keySet()) {
    		String[] ijs = ij.split(",");
    		String i = ijs[0];
    		String j = ijs[1];
    		int ii = Integer.parseInt(i);
    		int jj = Integer.parseInt(j);
    		halfEdges.get(ii + "," + jj).twin = halfEdges.get(jj +"," + ii);
    		//System.out.println(ii + ", " + jj);
        }
		//halfEdges.clear();
        
        // TODO: Objective 5: fill your priority queue on load
        
    }

    /**
     * You might want to use this to match up half edges... 
     */
    Map<String,HalfEdge> halfEdges = new TreeMap<String,HalfEdge>();
    
    
    // TODO: Objective 2, 3, 4, 5: write methods to help with collapse, and for checking topological problems
    /**
     * get the middlePoint of 2 vertices.
     * @param v1
     * @param v2
     * @return
     */
    public Vertex getMiddlePoint(Vertex v1, Vertex v2) {
    	Vertex middlePoint = new Vertex();
		middlePoint.p = new Point3d((v1.p.x+v2.p.x)/2, (v1.p.y+v2.p.y)/2, (v1.p.z+v2.p.z)/2);
		return middlePoint;
    }
    
    /**
     * get the middlePoint of an HalfEdge.
     * @param he
     * @return
     */
    public Vertex getMiddlePoint(HalfEdge he) {
		Vertex v1 = he.head;
		Vertex v2 = he.twin.head;
    	Vertex middlePoint = new Vertex();
		middlePoint.p = new Point3d((v1.p.x+v2.p.x)/2, (v1.p.y+v2.p.y)/2, (v1.p.z+v2.p.z)/2);
		return middlePoint;
    }
    
    /**
     * callapse the halfedge he to the vertex vt;
     * @param he
     * @param vt
     */
    public void collapse(HalfEdge he, Vertex vt) {
    	if(isTetrahedron()) {
    		return;
    	}
    	else {
    		HalfEdge twin = he.twin;
        	HalfEdge A = he.next;
        	HalfEdge B  = he.next.next;
        	HalfEdge C = twin.next;
        	HalfEdge D = twin.next.next;
        	HalfEdge loop = C.twin;
        	do {
        		loop.head = vt;
        		loop = loop.next.twin;
        	}while(loop!=C.twin);
        	
        	loop = he;
        	do {
        		loop.head = vt;
        		loop = loop.next.twin;
        	}while(loop!=he);
        	
        	B.head = vt;
        	D.head = vt;
        	A.twin.twin = B.twin;
        	B.twin.twin = A.twin;
        	C.twin.twin = D.twin;
        	D.twin.twin = C.twin;
        	Face f1 = A.leftFace;
        	Face f2 = C.leftFace;
        	faces.remove(f1);
        	faces.remove(f2);
    	}
    	if(causeTopologicalProblem(he.next.twin, vt)) {
    		System.out.println("Topology Problem!!");
    		//TO-DO undo collapse. 
    	}
    }
    
    /**
     * Check if the current hed has only 4 faces or less.
     * @return true if it has 4 faces left, false if it has more than 4 faces
     */
    public boolean isTetrahedron() {
    	if(faces.size()<=4) {
    		System.out.println("There are only 4 faces, can not collapse anymore");
    		return true;
    	}
    	return false;
    }
    
    /**
     * Check if the vertex have topological problem with the other vertices.
     * @param he halfedge which one end is vertex vt.
     * @param vt the vertex to check
     * @return true if it has topological problem, false otherwise
     */
    public boolean causeTopologicalProblem(HalfEdge he, Vertex vt) {
    	if(he.head!=vt) {
    		he = he.twin;
    	}
    	HalfEdge loop = he;
    	do {
    		int count = numberOfCommonAdjacantVertices(loop);
    		if(count!=2) {
    			System.out.println("Huston we got a problem, there are 1-rings edges vertices with " + count + " number of vertices in common. ");
    			return true;
    		}
    		loop = loop.next.twin;
    	}while(loop!=he);
    	
    	return false;	
    }
    
    /**
     * To check how many adjacant vertices are in common between the two end verticese of edge he.
     * @param he
     * @return
     */
    public int numberOfCommonAdjacantVertices(HalfEdge he) {
    	HalfEdge loop = he;
    	Set<Vertex> v1Adjacants= new HashSet<Vertex>();
    	do {
    		v1Adjacants.add(loop.twin.head);
    		loop = loop.next.twin;
    	}while(loop!=he);
    	int count = 0;
    	loop = he.twin;
    	do {
    		Vertex current = loop.twin.head;
    		if(v1Adjacants.contains(current)) {
    			count++;
    		}
    		loop = loop.next.twin;
    	}while(loop!=he.twin);
    	return count;
    }
    
    /**
     * find the optimal regularized vertex position of edge he. 
     * @param he
     * @return regularized vertex position that can be used to collapse. 
     */
    public Vertex quadricErrorMetric(HalfEdge he) {
    	Vertex vi = he.head;
    	Vertex vj = he.twin.head;
    	
    	Set<Face> viFaces = new HashSet<Face>();
    	HalfEdge loop = he;
    	do {
    		viFaces.add(loop.leftFace);
    		loop = loop.next.twin;
    	}while(loop!=he);
    	
    	Set<Face> vjFaces = new HashSet<Face>();
    	loop = he.twin;
    	do {
    		vjFaces.add(loop.leftFace);
    		loop = loop.next.twin;
    	}while(loop!=he.twin);
    	
    	Matrix4d Qi = new Matrix4d();
    	for(Face facei : viFaces) {
    		Qi.add(facei.K);
    	}
    	vi.Q = Qi;
    	
    	Matrix4d Qj = new Matrix4d();
    	for(Face facej: vjFaces) {
    		Qj.add(facej.K);
    	}
    	vj.Q =  Qj;
    	
    	Vertex m = getMiddlePoint(vi, vj);
    	double Qreg44 = m.p.x*m.p.x+m.p.y*m.p.y + m.p.z*m.p.z;
    	Matrix4d Qreg = new Matrix4d(new double[]{
    		1,0,0,-m.p.x,
    		0,1,0,-m.p.y,
    		0,0,1,-m.p.z,
    		-m.p.x,-m.p.y,-m.p.z, Qreg44
    	});
    	Qreg.mul(0.01);
    	Matrix4d totalQ = new Matrix4d();
    	totalQ.add(Qi);
    	totalQ.add(Qj);
    	totalQ.add(Qreg);
    	
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
    	return result;
    }
    
    /**
	 * Need to know both verts before the collapse, but this information is actually 
	 * already stored within the excized portion of the half edge data structure.
	 * Thus, we only need to have a half edge (the collapsed half edge) to undo
	 */
	LinkedList<HalfEdge> undoList = new LinkedList<>();
	/**
	 * To redo an undone collapse, we must know which edge to collapse.  We should
	 * likewise reuse the Vertex that was created for the collapse.
	 */
	LinkedList<HalfEdge> redoListHalfEdge = new LinkedList<>();
	LinkedList<Vertex> redoListVertex = new LinkedList<>();

    void undoCollapse() {
    	if ( undoList.isEmpty() ) return; // ignore the request
   
    	HalfEdge he = undoList.removeLast();

    	// TODO: Objective 6: undo the last collapse
    	// be sure to put the information on the redo list so you can redo the collapse too!
    }
    
    void redoCollapse() {
    	if ( redoListHalfEdge.isEmpty() ) return; // ignore the request
    	
    	HalfEdge he = redoListHalfEdge.removeLast();
    	Vertex v = redoListVertex.removeLast();
    	
    	undoList.add( he );  // put this on the undo list so we can undo this collapse again

    	// TODO: Objective 7: undo the edge collapse!
    	
    }
      
    /**
     * Draws the half edge data structure by drawing each of its faces.
     * Per vertex normals are used to draw the smooth surface when available,
     * otherwise a face normal is computed. 
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // we do not assume triangular faces here        
        Point3d p;
        Vector3d n;        
        for ( Face face : faces ) {
            HalfEdge he = face.he;
            gl.glBegin( GL2.GL_POLYGON );
            n = he.leftFace.n;
            gl.glNormal3d( n.x, n.y, n.z );
            HalfEdge e = he;
            do {
                p = e.head.p;
                gl.glVertex3d( p.x, p.y, p.z );
                e = e.next;
            } while ( e != he );
            gl.glEnd();
        }
    }

}