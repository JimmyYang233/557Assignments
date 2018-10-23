package comp557.a1;

import javax.swing.JTextField;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import comp557.a1.DAGNode;
import comp557.a1.Parser;
import mintools.parameters.BooleanParameter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CharacterCreator {

	static public String name = "My Character - Dan Ning Yang 260743330";
	
	// TODO: Objective 6: change default of load from file to true once you start working with xml
	static BooleanParameter loadFromFile = new BooleanParameter( "Load from file (otherwise by procedure)", true );
	static JTextField baseFileName = new JTextField("comp557F18/a1data/character");
	static { baseFileName.setName("what is this?"); }
	
	/**
	 * Creates a character, either procedurally, or by loading from an xml file
	 * @return root node
	 */
	static public DAGNode create() {
		
		if ( loadFromFile.getValue() ) {
			
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				
				Element rootdoc = doc.createElement("doc");
				doc.appendChild(rootdoc);
				
				FreeJoint myCharacter = new FreeJoint("Character");
				myCharacter.setPosition(new Point3d(0,4,-4));
				Element character = myCharacter.setElement(doc, rootdoc);
				//
				BodyBox upperBody = new BodyBox("UpperBody",0,0,0,4,4,2);
				upperBody.setColor(new Point3d(255,0,0));
				myCharacter.add(upperBody);
				Element upperbody = upperBody.setElement(doc, character);
				//
				HingeJoint bodyJoint = new HingeJoint("BodyJoint", -50, 50);
				bodyJoint.setPosition(new Point3d(0,-2,0));
				upperBody.add(bodyJoint);
				Element bodyjoint = bodyJoint.setElement(doc, upperbody);
				//
				BodyBox lowerBody = new BodyBox("LowerBody",0,-0.5,0,4,2,2);
				lowerBody.setColor(new Point3d(255,0,0));
				bodyJoint.add(lowerBody);
				Element lowerbody = lowerBody.setElement(doc, bodyjoint);
				//
				BallJoint lowerNeckJoint = new BallJoint("lowerNeckJoint", -80, 70, -120, 120, -80, 80);
				lowerNeckJoint.setPosition(new Point3d(0,2,0));
				upperBody.add(lowerNeckJoint);
				Element lowerneckjoint = lowerNeckJoint.setElement(doc, upperbody);
				//
				BodyBox neck = new BodyBox("Neck",0,0.5,0,1,1,1) {{
					setColor(new Point3d(255,0,0));
				}};
				lowerNeckJoint.add(neck);
				Element neckxml = neck.setElement(doc, lowerneckjoint); 
				//
				BodySphere head = new BodySphere("Head"){{
					setScale(new Point3d(1.5,1.5,1.5));
					setCentre(new Point3d(0,1.5,0));
					setColor(new Point3d(255,0,0));
				}};
				neck.add(head);
				Element headxml = head.setElement(doc, neckxml);
				
				//
				BallJoint rightShoulderJoint = new BallJoint("rightShouldJoint", -180, 100,-90,90,-180,0);
				rightShoulderJoint.setPosition(new Point3d(-2,2,0));
				rightShoulderJoint.setAxis(new Point3d(0,0,-100));
				upperBody.add(rightShoulderJoint);
				Element rightshoulderjoint = rightShoulderJoint.setElement(doc, upperbody);
				
				//
				BodyBox rightUpperArm = new BodyBox("rightUpperArm",-0.5,-0.5,0,1,3.5,1);
				rightUpperArm.setColor(new Point3d(255,0,0));
				rightShoulderJoint.add(rightUpperArm);
				Element rightupperarm = rightUpperArm.setElement(doc, rightshoulderjoint);
				//
				HingeJoint rightElbow = new HingeJoint("rightElbow", -160, 0);
				rightElbow.setPosition(new Point3d(-0.5,-3.5,0));
				rightUpperArm.add(rightElbow);
				Element rightelbow = rightElbow.setElement(doc, rightupperarm);
				
				BodyBox rightLowerArm = new BodyBox("rightLowerArm",0,-0.5,0,1,3.5,1);
				rightLowerArm.setColor(new Point3d(255,0,0));
				rightElbow.add(rightLowerArm);
				Element rightlowerarm = rightLowerArm.setElement(doc, rightelbow);
				
				BallJoint rightWrist = new BallJoint("rightWrist",-90,90,-90,90,-90,90);
				rightWrist.setPosition(new Point3d(0,-3.5,0));
				rightLowerArm.add(rightWrist);
				Element rightwrist = rightWrist.setElement(doc, rightlowerarm);
				
				BodyBox rightHand = new BodyBox("rightHand",0,-0.25,0,1,1.5,1);
				rightHand.setColor(new Point3d(255,0,0));
				rightWrist.add(rightHand);
				Element righthand = rightHand.setElement(doc, rightwrist);
				
				BallJoint leftShoulderJoint = new BallJoint("leftShouldJoint", -180, 100,-90,90,0,180);
				leftShoulderJoint.setPosition(new Point3d(2,2,0));
				leftShoulderJoint.setAxis(new Point3d(0,0,100));
				upperBody.add(leftShoulderJoint);
				Element leftshoulderjoint = leftShoulderJoint.setElement(doc, upperbody);
				
				BodyBox leftUpperArm = new BodyBox("leftUpperArm",0.5,-0.5,0,1,3.5,1);
				leftUpperArm.setColor(new Point3d(255,0,0));
				leftShoulderJoint.add(leftUpperArm);
				Element leftupperarm = leftUpperArm.setElement(doc, leftshoulderjoint);
				
				HingeJoint leftElbow = new HingeJoint("leftElbow", -160, 0);
				leftElbow.setPosition(new Point3d(0.5,-3.5,0));
				leftUpperArm.add(leftElbow);
				Element leftelbow = leftElbow.setElement(doc,leftupperarm);
				
				BodyBox leftLowerArm = new BodyBox("leftLowerArm",0,-0.5,0,1,3.5,1);
				leftLowerArm.setColor(new Point3d(255,0,0));
				leftElbow.add(leftLowerArm);
				Element leftlowerarm = leftLowerArm.setElement(doc, leftelbow);
				
				BallJoint leftWrist = new BallJoint("leftWrist",-90,90,-90,90,-90,90);
				leftWrist.setPosition(new Point3d(0,-3.5,0));
				leftLowerArm.add(leftWrist);
				Element leftwrist = leftWrist.setElement(doc, leftlowerarm);
				
				BodyBox leftHand = new BodyBox("leftHand",0,-0.25,0,1,1.5,1);
				leftHand.setColor(new Point3d(255,0,0));
				leftWrist.add(leftHand);
				Element lefthand = leftHand.setElement(doc, leftwrist);
				
				BallJoint rightHipbone = new BallJoint("rightHipbone", -130,70,-90,90,-60,45);
				rightHipbone.setPosition(new Point3d(-1.1,-2,0));
				rightHipbone.setAxis(new Point3d(0,0,-30));
				lowerBody.add(rightHipbone);
				Element righthipbone = rightHipbone.setElement(doc, lowerbody);
				
				BodyBox rightUpperLeg = new BodyBox("rightUpperLeg",0,-0.5,0,1.8,3.7,1.8);
				rightUpperLeg.setColor(new Point3d(255,0,0));
				rightHipbone.add(rightUpperLeg);
				Element rightupperleg = rightUpperLeg.setElement(doc, righthipbone);
				
				HingeJoint rightKnee = new HingeJoint("rightKnee",0,150);
				rightKnee.setPosition(new Point3d(0,-3.7,0));
				rightUpperLeg.add(rightKnee);
				Element rightknee = rightKnee.setElement(doc, rightupperleg);
				
				BodyBox rightLowerLeg = new BodyBox("rightLowerLeg",0,-0.5,0,1.8,3.8,1.8);
				rightLowerLeg.setColor(new Point3d(255,0,0));
				rightKnee.add(rightLowerLeg);
				Element rightlowerleg = rightLowerLeg.setElement(doc, rightknee);
				
				
				BallJoint rightAnkle = new BallJoint("rightAnkle", -40,90,-45,20,-20,20);
				rightAnkle.setPosition(new Point3d(0,-3.8,0));
				rightLowerLeg.add(rightAnkle);
				Element rightankle = rightAnkle.setElement(doc, rightlowerleg);
				
				BodyBox rightFoot = new BodyBox("rightFoot",0,-0.5,0.2,1.8,1,3);
				rightFoot.setColor(new Point3d(255,0,0));
				rightAnkle.add(rightFoot);
				Element rightfoot = rightFoot.setElement(doc, rightankle);
				
				BallJoint leftHipbone = new BallJoint("leftHipbone", -130,70,-90,90,-45,60);
				leftHipbone.setPosition(new Point3d(1.1,-2,0));
				leftHipbone.setAxis(new Point3d(0,0,30));
				lowerBody.add(leftHipbone);
				Element lefthipbone = leftHipbone.setElement(doc, lowerbody);
				
				BodyBox leftUpperLeg = new BodyBox("leftUpperLeg",0,-0.5,0,1.8,3.7,1.8);
				leftUpperLeg.setColor(new Point3d(255,0,0));
				leftHipbone.add(leftUpperLeg);
				Element leftupperleg = leftUpperLeg.setElement(doc, lefthipbone);
				
				HingeJoint leftKnee = new HingeJoint("leftKnee",0,150);
				leftKnee.setPosition(new Point3d(0,-3.7,0));
				leftUpperLeg.add(leftKnee);
				Element leftknee = leftKnee.setElement(doc, leftupperleg);
				
				BodyBox leftLowerLeg = new BodyBox("leftLowerLeg",0,-0.5,0,1.8,3.8,1.8);
				leftLowerLeg.setColor(new Point3d(255,0,0));
				leftKnee.add(leftLowerLeg);
				Element leftlowerleg = leftLowerLeg.setElement(doc, leftknee);
				
				BallJoint leftAnkle = new BallJoint("leftAnkle", -40,90,-20,45,-20,20);
				leftAnkle.setPosition(new Point3d(0,-3.8,0));
				leftLowerLeg.add(leftAnkle);
				Element leftankle = leftAnkle.setElement(doc, leftlowerleg);
				
				BodyBox leftFoot = new BodyBox("leftFoot",0,-0.5,0.2,1.8,1,3);
				leftFoot.setColor(new Point3d(255,0,0));
				leftAnkle.add(leftFoot);
				Element leftfoot = leftFoot.setElement(doc, leftankle);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File( baseFileName.getText() + ".xml")); 

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);
				
				System.out.println("File saved!");
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Parser.load( baseFileName.getText() + ".xml");
		} else {
			FreeJoint myCharacter = new FreeJoint("Character");
			myCharacter.setPosition(new Point3d(0,4,-4));
			//
			BodyBox upperBody = new BodyBox("UpperBody",0,0,0,4,4,2);
			upperBody.setColor(new Point3d(255,0,0));
			myCharacter.add(upperBody);
			//
			HingeJoint bodyJoint = new HingeJoint("BodyJoint", -50, 50);
			bodyJoint.setPosition(new Point3d(0,-2,0));
			upperBody.add(bodyJoint);
			//
			BodyBox lowerBody = new BodyBox("LowerBody",0,-0.5,0,4,2,2);
			lowerBody.setColor(new Point3d(255,0,0));
			bodyJoint.add(lowerBody);
			//
			BallJoint lowerNeckJoint = new BallJoint("lowerNeckJoint", -80, 70, -120, 120, -80, 80);
			lowerNeckJoint.setPosition(new Point3d(0,2,0));
			upperBody.add(lowerNeckJoint);
			//
			BodyBox neck = new BodyBox("Neck",0,0.5,0,1,1,1) {{
				setColor(new Point3d(255,0,0));
			}};
			lowerNeckJoint.add(neck);
			//
			BodySphere head = new BodySphere("Head"){{
				setScale(new Point3d(1.5,1.5,1.5));
				setCentre(new Point3d(0,1.5,0));
				setColor(new Point3d(255,0,0));
			}};
			neck.add(head);
			//
			BallJoint rightShoulderJoint = new BallJoint("rightShouldJoint", -180, 100,-90,90,-180,0);
			rightShoulderJoint.setPosition(new Point3d(-2,2,0));
			rightShoulderJoint.setAxis(new Point3d(0,0,-100));
			upperBody.add(rightShoulderJoint);
			//
			BodyBox rightUpperArm = new BodyBox("rightUpperArm",-0.5,-0.5,0,1,3.5,1);
			rightUpperArm.setColor(new Point3d(255,0,0));
			rightShoulderJoint.add(rightUpperArm);
			//
			HingeJoint rightElbow = new HingeJoint("rightElbow", -160, 0);
			rightElbow.setPosition(new Point3d(-0.5,-3.5,0));
			rightUpperArm.add(rightElbow);
			
			BodyBox rightLowerArm = new BodyBox("rightLowerArm",0,-0.5,0,1,3.5,1);
			rightLowerArm.setColor(new Point3d(255,0,0));
			rightElbow.add(rightLowerArm);
			
			BallJoint rightWrist = new BallJoint("rightWrist",-90,90,-90,90,-90,90);
			rightWrist.setPosition(new Point3d(0,-3.5,0));
			rightLowerArm.add(rightWrist);
			
			BodyBox rightHand = new BodyBox("rightHand",0,-0.25,0,1,1.5,1);
			rightHand.setColor(new Point3d(255,0,0));
			rightWrist.add(rightHand);
			
			BallJoint leftShoulderJoint = new BallJoint("leftShouldJoint", -180, 100,-90,90,0,180);
			leftShoulderJoint.setPosition(new Point3d(2,2,0));
			leftShoulderJoint.setAxis(new Point3d(0,0,100));
			upperBody.add(leftShoulderJoint);
			
			BodyBox leftUpperArm = new BodyBox("leftUpperArm",0.5,-0.5,0,1,3.5,1);
			leftUpperArm.setColor(new Point3d(255,0,0));
			leftShoulderJoint.add(leftUpperArm);
			
			HingeJoint leftElbow = new HingeJoint("leftElbow", -160, 0);
			leftElbow.setPosition(new Point3d(0.5,-3.5,0));
			leftUpperArm.add(leftElbow);
			
			BodyBox leftLowerArm = new BodyBox("leftLowerArm",0,-0.5,0,1,3.5,1);
			leftLowerArm.setColor(new Point3d(255,0,0));
			leftElbow.add(leftLowerArm);
			
			BallJoint leftWrist = new BallJoint("leftWrist",-90,90,-90,90,-90,90);
			leftWrist.setPosition(new Point3d(0,-3.5,0));
			leftLowerArm.add(leftWrist);
			
			BodyBox leftHand = new BodyBox("leftHand",0,-0.25,0,1,1.5,1);
			leftHand.setColor(new Point3d(255,0,0));
			leftWrist.add(leftHand);
			
			BallJoint rightHipbone = new BallJoint("rightHipbone", -130,70,-90,90,-60,45);
			rightHipbone.setPosition(new Point3d(-1.1,-2,0));
			rightHipbone.setAxis(new Point3d(0,0,-30));
			lowerBody.add(rightHipbone);
			
			BodyBox rightUpperLeg = new BodyBox("rightUpperLeg",0,-0.5,0,1.8,3.7,1.8);
			rightUpperLeg.setColor(new Point3d(255,0,0));
			rightHipbone.add(rightUpperLeg);
			
			HingeJoint rightKnee = new HingeJoint("rightKnee",0,150);
			rightKnee.setPosition(new Point3d(0,-3.7,0));
			rightUpperLeg.add(rightKnee);
			
			BodyBox rightLowerLeg = new BodyBox("rightLowerLeg",0,-0.5,0,1.8,3.8,1.8);
			rightLowerLeg.setColor(new Point3d(255,0,0));
			rightKnee.add(rightLowerLeg);
			
			BallJoint rightAnkle = new BallJoint("rightAnkle", -40,90,-45,20,-20,20);
			rightAnkle.setPosition(new Point3d(0,-3.8,0));
			rightLowerLeg.add(rightAnkle);
			
			BodyBox rightFoot = new BodyBox("rightFoot",0,-0.5,0.2,1.8,1,3);
			rightFoot.setColor(new Point3d(255,0,0));
			rightAnkle.add(rightFoot);
			
			BallJoint leftHipbone = new BallJoint("leftHipbone", -130,70,-90,90,-45,60);
			leftHipbone.setPosition(new Point3d(1.1,-2,0));
			leftHipbone.setAxis(new Point3d(0,0,30));
			lowerBody.add(leftHipbone);
			
			BodyBox leftUpperLeg = new BodyBox("leftUpperLeg",0,-0.5,0,1.8,3.7,1.8);
			leftUpperLeg.setColor(new Point3d(255,0,0));
			leftHipbone.add(leftUpperLeg);
			
			HingeJoint leftKnee = new HingeJoint("leftKnee",0,150);
			leftKnee.setPosition(new Point3d(0,-3.7,0));
			leftUpperLeg.add(leftKnee);
			
			BodyBox leftLowerLeg = new BodyBox("leftLowerLeg",0,-0.5,0,1.8,3.8,1.8);
			leftLowerLeg.setColor(new Point3d(255,0,0));
			leftKnee.add(leftLowerLeg);
			
			BallJoint leftAnkle = new BallJoint("leftAnkle", -40,90,-20,45,-20,20);
			leftAnkle.setPosition(new Point3d(0,-3.8,0));
			leftLowerLeg.add(leftAnkle);
			
			BodyBox leftFoot = new BodyBox("leftFoot",0,-0.5,0.2,1.8,1,3);
			leftFoot.setColor(new Point3d(255,0,0));
			leftAnkle.add(leftFoot);
			
			return myCharacter;
		}
	}
}
