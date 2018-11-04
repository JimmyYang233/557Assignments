package comp557.a3;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
        	newFace.recomputeNormal();
        	faces.add(newFace);
        	
        	hf1.leftFace = newFace;
        	hf2.leftFace = newFace;
        	hf3.leftFace = newFace;
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
    public void collapse(HalfEdge he, Vertex vt) {
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
    	Face f2 = B.leftFace;
    	faces.remove(f1);
    	faces.remove(f2);
//    	halfEdges.remove(he);
//    	halfEdges.remove(twin);
//    	halfEdges.remove(A);
//    	halfEdges.remove(B);
//    	halfEdges.remove(C);
//    	halfEdges.remove(D);
    	
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