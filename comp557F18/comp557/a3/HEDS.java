package comp557.a3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
    
    PriorityQueue<Edge> edges = new PriorityQueue<Edge>();
    
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
        	hf1.head.halfEdges.add(hf1);
        	hf2.head.halfEdges.add(hf2);
        	hf3.head.halfEdges.add(hf3);
        	
        	halfEdges.put(face[2] + "," + face[0], hf1);
        	halfEdges.put(face[0] + "," + face[1], hf2);
        	halfEdges.put(face[1] + "," + face[2], hf3);
        	
        	hf1.next = hf2;
        	hf2.next = hf3;
        	hf3.next = hf1;
        	
        	Face newFace = new Face(hf1);
        	faces.add(newFace);
        }
        
        
        /** get the twin of each halfEdge and gives them Edge.*/
    	for(String ij : halfEdges.keySet()) {
    		String[] ijs = ij.split(",");
    		String i = ijs[0];
    		String j = ijs[1];
    		int ii = Integer.parseInt(i);
    		int jj = Integer.parseInt(j);
    		HalfEdge he1 = halfEdges.get(ii + "," + jj);
    		HalfEdge he2 = halfEdges.get(jj +"," + ii);
    		he1.twin = he2;
			Edge edge = new Edge();
			edge.he = he1;
			he1.e = edge;
			he2.e = edge;
        }
    	
    	
        /**get Q of all Vertices */
    	for(Vertex vertex : soup.vertexList) {
    		Matrix4d QQ = new Matrix4d();
    		for(HalfEdge he : vertex.halfEdges) {
    			QQ.add(he.leftFace.K);
    		}
    		vertex.Q = QQ;
    	}
    	// TODO: Objective 5: fill your priority queue on load
    	Collection<HalfEdge> theHalfEdges = halfEdges.values(); 
    	for(HalfEdge he : theHalfEdges) {
    		he.e.recompute();
    		if(!edges.contains(he.e)&&!causeTopologicalProblem(he)) {
    			edges.add(he.e);
    		}
    	}
        System.out.println("Total of " + edges.size() + " edges. " );
        int size = edges.size();
        for(int i = 0; i<size; i++) {
        	//System.out.println(i + ", " + edges.remove().error);
        }
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
    public static Vertex getMiddlePoint(Vertex v1, Vertex v2) {
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
    		edges.clear();
    		return;
    	}
    	else if(causeTopologicalProblem(he)) {
    		System.out.println("Topology Problem!!");
    	}
    	else {
    		doCollapse(he, vt);
    	}    	
    	edges.remove(he.e);
    	for(Edge edge : edges) {
			if(causeTopologicalProblem(edge.he)) {
				edges.remove(edge);
			}
		}
    	System.out.println("Total of " + edges.size() + " edges. " );
    }
    
    public void doCollapse(HalfEdge he, Vertex vt) {
    	undoList.add(he);
    	vt.Q.add(he.head.Q);
    	vt.Q.add(he.twin.head.Q);
		HalfEdge twin = he.twin;
    	HalfEdge A = he.next;
    	HalfEdge B  = he.next.next;
    	HalfEdge C = twin.next;
    	HalfEdge D = twin.next.next;
    	HalfEdge loop = C.twin;
    	do {
    		loop.head = vt;
    		Edge edge = loop.e;
    		if(edges.contains(edge)) {
    			edges.remove(edge);
    		}    		
    		loop = loop.next.twin;
    	}while(loop!=he.twin);	
    	loop = A.twin;
    	do {
    		loop.head = vt;
    		Edge edge = loop.e;
    		if(edges.contains(edge)) {
    			edges.remove(edge);
    		}
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
    	loop = A.twin;
    	do {
    		Edge edge = new Edge();
    		edge.he = loop;
    		loop.e = edge;
    		loop.twin.e = edge;
    		edge.recompute();
    		if(!causeTopologicalProblem(loop)) {
    			edges.add(edge);
    		}
    		loop = loop.next.twin;
    	}while(loop!=A.twin);
    	//System.out.println("Removed " + count + " edges, add " + addCount + " edges" );
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
    public boolean causeTopologicalProblem(HalfEdge he) {
    		int count = numberOfCommonAdjacantVertices(he);
    		if(count!=2) {
    			System.out.println("Huston we got a problem, there are 1-rings edges vertices with " + count + " number of vertices in common. ");
    			return true;
    		}
    	
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
    	System.out.println(v1Adjacants.size());
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

    	HalfEdge twin = he.twin;
    	HalfEdge A = he.next;
    	HalfEdge B  = he.next.next;
    	HalfEdge C = twin.next;
    	HalfEdge D = twin.next.next;
    	Vertex vi = he.head;
    	Vertex vj = he.twin.head;
    	A.twin.twin = A;
    	B.twin.twin = B;
    	C.twin.twin = C;
    	D.twin.twin = D;
    	HalfEdge loop = he.twin;
    	do {
    		loop.head = vj;
    		loop = loop.next.twin;
    	}while(loop!=he.twin);
    	
    	loop = he;
    	do {
    		loop.head = vi;
    		loop = loop.next.twin;
    	}while(loop!=he);
    	
    	Face f1 = A.leftFace;
    	Face f2 = C.leftFace;
    	faces.add(f1);
    	faces.add(f2);
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
        
        for ( Face face : faces ) {
            HalfEdge he = face.he;
            HalfEdge e = he;
            do {
            	gl.glPointSize(5);
                gl.glBegin(GL2.GL_POINTS);
                //System.out.println(e.e.v.x + ", " + e.e.v.y + ", " + e.e.v.z);
                gl.glVertex3d(e.e.v.x, e.e.v.y, e.e.v.z);
                gl.glEnd();
            } while ( e != he );
        }
    }

	public boolean noMoreCollapse() {
		if(edges.size()<=0) {
			return true;
		}
		else {
			return false;
		}	
	}
    
    
    

}