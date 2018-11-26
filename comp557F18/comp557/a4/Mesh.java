package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import comp557.a4.PolygonSoup.Vertex;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		//System.out.println(soup.faceList.size());
		//System.out.println(soup.vertexList.size());
		// TODO: Objective 9: ray triangle intersection for meshes
		for(int[] face : soup.faceList) {
			Vertex v1 = soup.vertexList.get(face[0]);
			Vertex v2 = soup.vertexList.get(face[1]);
			Vertex v3 = soup.vertexList.get(face[2]);
		}
		
		
	}

}
