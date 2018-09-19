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
			upperBody.setScale(new Point3d(4,4,2));
			upperBody.setColor(new Point3d(255,0,0));
			myCharacter.add(upperBody);
			
			HingeJoint bodyJoint = new HingeJoint("BodyJoint", -50, 50);
			bodyJoint.setPosition(new Point3d(0,-2,0));
			upperBody.add(bodyJoint);
			
			BodyBox lowerBody = new BodyBox("LowerBody");
			lowerBody.setScale(new Point3d(4,2,2));
			lowerBody.setCentre(new Point3d(0,-0.5,0));
			lowerBody.setColor(new Point3d(255,0,0));
			bodyJoint.add(lowerBody);
			
			BallJoint lowerNeckJoint = new BallJoint("lowerNeckJoint", -80, 70, -120, 120, -80, 80);
			lowerNeckJoint.setPosition(new Point3d(0,2,0));
			upperBody.add(lowerNeckJoint);
			
			BodyBox neck = new BodyBox("Neck") {{
				setCentre(new Point3d(0,0.5,0));
				setScale(new Point3d(1,1,1));
				setColor(new Point3d(255,0,0));
			}};
			lowerNeckJoint.add(neck);
			
			BodySphere head = new BodySphere("Head"){{
				setScale(new Point3d(1.5,1.5,1.5));
				setCentre(new Point3d(0,1.5,0));
				setColor(new Point3d(255,0,0));
			}};
			neck.add(head);
			
			BallJoint leftShoulderJoint = new BallJoint("LeftShouldJoint", -180, 100,-90,90,-180,0);
			leftShoulderJoint.setPosition(new Point3d(-2,1.65,0));
			upperBody.add(leftShoulderJoint);
			
			BodyBox leftUpperArm = new BodyBox("leftUpperArm");
			leftUpperArm.setCentre(new Point3d(-0.5,-0.5,0));
			leftUpperArm.setScale(new Point3d(1,3.5,1));
			leftUpperArm.setColor(new Point3d(255,0,0));
			leftShoulderJoint.add(leftUpperArm);
			
			return myCharacter;
		}
	}
}
