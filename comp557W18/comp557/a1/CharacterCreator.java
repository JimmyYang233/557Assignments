package comp557.a1;

import javax.swing.JTextField;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import comp557.a1.DAGNode;
import comp557.a1.Parser;
import mintools.parameters.BooleanParameter;

public class CharacterCreator {

	static public String name = "CHARACTER NAME - YOUR NAME AND STUDENT NUMBER";
	
	// TODO: Objective 6: change default of load from file to true once you start working with xml
	static BooleanParameter loadFromFile = new BooleanParameter( "Load from file (otherwise by procedure)", false );
	static JTextField baseFileName = new JTextField("a1data/character");
	static { baseFileName.setName("what is this?"); }
	
	/**
	 * Creates a character, either procedurally, or by loading from an xml file
	 * @return root node
	 */
	static public DAGNode create() {
		
		if ( loadFromFile.getValue() ) {
			// TODO: Objectives 6: create your character in the character.xml file 
			return Parser.load( baseFileName.getText() + ".xml");
		} else {
			DAGNode myCharacter = new FreeJoint("Character");
			BodyBox upperBody = new BodyBox("UpperBody");
			upperBody.setScale(new Point3d(3,3,2));
			upperBody.setColor(new Point3d(255,0,0));
			BodyBox lowerBody = new BodyBox("LowerBody");
			lowerBody.setScale(new Point3d(1,0.5,1));
			lowerBody.setCentre(new Point3d(0,-0.25,0));
			lowerBody.setColor(new Point3d(255,0,0));
			HingeJoint bodyJoint = new HingeJoint("BodyJoint");
			bodyJoint.setPosition(new Point3d(0,-0.5,0));
			DAGNode neck = new BallJoint("neck");
			DAGNode head = new BodySphere("Head");
			neck.add(head);
			bodyJoint.add(lowerBody);
			myCharacter.add(upperBody);
			myCharacter.add(bodyJoint);
			myCharacter.add(neck);
			return myCharacter;
		}
	}
}
