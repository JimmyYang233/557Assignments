package comp557.a2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.ControlFrame;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FlatMatrix4d;
import mintools.viewer.FlatMatrix4f;
import mintools.viewer.Interactor;
import mintools.viewer.TrackBallCamera;

/**
 * Assignment 2 - depth of field blur, and anaglyphys
 * 
 * For additional information, see the following paper, which covers
 * more on quality rendering, but does not cover anaglyphs.
 * 
 * The Accumulation Buffer: Hardware Support for High-Quality Rendering
 * Paul Haeberli and Kurt Akeley
 * SIGGRAPH 1990
 * 
 * http://http.developer.nvidia.com/GPUGems/gpugems_ch23.html
 * GPU Gems [2007] has a slightly more recent survey of techniques.
 *
 * @author YOUR NAME HERE
 */
public class A2App implements GLEventListener, Interactor {

	/** TODO: Put your name in the window title */
	private String name = "Comp 557 Assignment 2 - Dan Ning Yang 260743330";
	
    /** Viewing mode as specified in the assignment */
    int viewingMode = 1;
        
    /** eye Z position in world coordinates */
    private DoubleParameter eyeZPosition = new DoubleParameter( "eye z", 0.5, 0.25, 3 ); 
    /** near plane Z position in world coordinates */
    private DoubleParameter nearZPosition = new DoubleParameter( "near z", 0.25, -0.2, 0.5 ); 
    /** far plane Z position in world coordinates */
    private DoubleParameter farZPosition  = new DoubleParameter( "far z", -0.5, -2, -0.25 ); 
    /** focal plane Z position in world coordinates */
    private DoubleParameter focalPlaneZPosition = new DoubleParameter( "focal z", 0, -1.5, 0.4 );     

    /** Samples for drawing depth of field blur */    
    private IntParameter samples = new IntParameter( "samples", 5, 1, 100 );   
    
    /** 
     * Aperture size for drawing depth of field blur
     * In the human eye, pupil diameter ranges between approximately 2 and 8 mm
     */
    private DoubleParameter aperture = new DoubleParameter( "aperture size", 0.003, 0, 0.01 );
    
    /** x eye offsets for testing (see objective 4) */         
    private DoubleParameter eyeXOffset = new DoubleParameter("eye offset in x", 0.0, -0.3, 0.3);
    /** y eye offsets for testing (see objective 4) */
    private DoubleParameter eyeYOffset = new DoubleParameter("eye offset in y", 0.0, -0.3, 0.3);
    
    private BooleanParameter drawCenterEyeFrustum = new BooleanParameter( "draw center eye frustum", true );    
    
    private BooleanParameter drawEyeFrustums = new BooleanParameter( "draw left and right eye frustums", true );
    
	/**
	 * The eye disparity should be constant, but can be adjusted to test the
	 * creation of left and right eye frustums or likewise, can be adjusted for
	 * your own eyes!! Note that 63 mm is a good inter occular distance for the
	 * average human, but you may likewise want to lower this to reduce the
	 * depth effect (images may be hard to fuse with cheap 3D colour filter
	 * glasses). Setting the disparity negative should help you check if you
	 * have your left and right eyes reversed!
	 */
    private DoubleParameter eyeDisparity = new DoubleParameter("eye disparity", 0.063, -0.1, 0.1 );

    private GLUT glut = new GLUT();
    
    private Scene scene = new Scene();
    
	 FastPoissonDisk disk = new FastPoissonDisk();
    
   

    /**
     * Launches the application
     * @param args
     */
    public static void main(String[] args) {
        new A2App();
    }
    
    GLCanvas glCanvas;
    
    /** Main trackball for viewing the world and the two eye frustums */
    TrackBallCamera tbc = new TrackBallCamera();
    /** Second trackball for rotating the scene */
    TrackBallCamera tbc2 = new TrackBallCamera();
    
    /**
     * Creates the application
     */
    public A2App() {      
        Dimension controlSize = new Dimension(640, 640);
        Dimension size = new Dimension(640, 480);
        ControlFrame controlFrame = new ControlFrame("Controls");
        controlFrame.add("Camera", tbc.getControls());
        controlFrame.add("Scene TrackBall", tbc2.getControls());
        controlFrame.add("Scene", getControls());
        controlFrame.setSelectedTab("Scene");
        controlFrame.setSize(controlSize.width, controlSize.height);
        controlFrame.setLocation(size.width + 20, 0);
        controlFrame.setVisible(true);    
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities glc = new GLCapabilities(glp);
        glCanvas = new GLCanvas( glc );
        glCanvas.setSize( size.width, size.height );
        glCanvas.setIgnoreRepaint( true );
        glCanvas.addGLEventListener( this );
        glCanvas.requestFocus();
        FPSAnimator animator = new FPSAnimator( glCanvas, 60 );
        animator.start();        
        tbc.attach( glCanvas );
        tbc2.attach( glCanvas );
        // initially disable second trackball, and improve default parameters given our intended use
        tbc2.enable(false);
        tbc2.setFocalDistance( 0 );
        tbc2.panRate.setValue(5e-5);
        tbc2.advanceRate.setValue(0.005);
        this.attach( glCanvas );        
        JFrame frame = new JFrame( name );
        frame.getContentPane().setLayout( new BorderLayout() );
        frame.getContentPane().add( glCanvas, BorderLayout.CENTER );
        frame.setLocation(0,0);        
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible( true );        
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    	// nothing to do
    }
        
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // do nothing
    }
    
    @Override
    public void attach(Component component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_7) {
                    viewingMode = e.getKeyCode() - KeyEvent.VK_1 + 1;
                }
                // only use the tbc trackball camera when in view mode 1 to see the world from
                // first person view, while leave it disabled and use tbc2 ONLY FOR ROTATION when
                // viewing in all other modes
                if ( viewingMode == 1 ) {
                	tbc.enable(true);
                	tbc2.enable(false);
	            } else {
                	tbc.enable(false);
                	tbc2.enable(true);
	            }
            }
        });
    }
    
    /**
     * @return a control panel
     */
    public JPanel getControls() {     
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        
        VerticalFlowPanel vfp2 = new VerticalFlowPanel();
        vfp2.setBorder(new TitledBorder("Z Positions in WORLD") );
        vfp2.add( eyeZPosition.getSliderControls(false));        
        vfp2.add( nearZPosition.getSliderControls(false));
        vfp2.add( farZPosition.getSliderControls(false));        
        vfp2.add( focalPlaneZPosition.getSliderControls(false));     
        vfp.add( vfp2.getPanel() );
        
        vfp.add ( drawCenterEyeFrustum.getControls() );
        vfp.add ( drawEyeFrustums.getControls() );        
        vfp.add( eyeXOffset.getSliderControls(false ) );
        vfp.add( eyeYOffset.getSliderControls(false ) );        
        vfp.add ( aperture.getSliderControls(false) );
        vfp.add ( samples.getSliderControls() );        
        vfp.add( eyeDisparity.getSliderControls(false) );
        VerticalFlowPanel vfp3 = new VerticalFlowPanel();
        vfp3.setBorder( new TitledBorder("Scene size and position" ));
        vfp3.add( scene.getControls() );
        vfp.add( vfp3.getPanel() );        
        return vfp.getPanel();
    }
             
    public void init( GLAutoDrawable drawable ) {
    	drawable.setGL( new DebugGL2( drawable.getGL().getGL2() ) );
        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);             // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glEnable(GL2.GL_NORMALIZE );
        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do 
        gl.glLineWidth( 2 );                        // slightly fatter lines by default!
    }   

	// TODO: Objective 1 - adjust for your screen resolution and dimension to something reasonable.
	double screenWidthPixels = 1920;
	double screenWidthMeters = 0.60;
	double metersPerPixel = screenWidthMeters / screenWidthPixels;
    
    @Override
    public void display(GLAutoDrawable drawable) {        
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);            

        double w = drawable.getSurfaceWidth() * metersPerPixel;
        double h = drawable.getSurfaceHeight() * metersPerPixel;
        
        //draw rectangle
        
        double nearVar = -nearZPosition.getValue()+eyeZPosition.getValue();
    	double farVar = -farZPosition.getValue()+eyeZPosition.getValue();
    	double eyeP = eyeZPosition.getValue();
    	double FRD = focalPlaneZPosition.getValue();
		double planeP = eyeZPosition.getValue()-focalPlaneZPosition.getValue();
		double wl = -w*planeP/(2*eyeP);
		double wr = w*planeP/(2*eyeP);
		double hb = -h*planeP/(2*eyeP);
		double ht = h*planeP/(2*eyeP);
		
		double wlOffset = wl-eyeXOffset.getValue();
		//System.out.println(wlOffset);
		double wrOffset = wr-eyeXOffset.getValue();
		double hbOffset = hb-eyeYOffset.getValue();
		double htOffset = ht-eyeYOffset.getValue();
		
		double nearwl = (-w*nearVar/(2*eyeP))*wlOffset/wl;
        double nearwr = (w*nearVar/(2*eyeP))*wrOffset/wr;
        double nearhb = (-h*nearVar/(2*eyeP))*hbOffset/hb;
        double nearht= (h*nearVar/(2*eyeP))*htOffset/ht;
        
        
        //prepare frustum
        gl.glMatrixMode(GL2.GL_PROJECTION);
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	gl.glFrustum(nearwl, nearwr, nearhb, nearht, nearVar, farVar);
    	//System.out.println(nearwl+", " + nearwr + ", " + nearhb+ "," + nearht + "," + nearVar+ "," + farVar);
    	//gl.glFrustum(-w/2, w/2, -h/2, h/2, (-farZPosition.getValue()+eyeZPosition.getValue())*0.1/(-nearZPosition.getValue()+eyeZPosition.getValue()), -farZPosition.getValue()+eyeZPosition.getValue());
    	FlatMatrix4d matrix = new FlatMatrix4d();
    	FlatMatrix4d invert = new FlatMatrix4d();
    	gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, matrix.asArray(), 0);
    	gl.glLoadIdentity();
    	gl.glPopMatrix();
    	matrix.reconstitute();
    	invert.getBackingMatrix().invert(matrix.getBackingMatrix());
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
    	
    	
    	//focal rectangle values
    	
        if ( viewingMode == 1 ) {
        	// We will use a trackball camera, but also apply an 
        	// arbitrary scale to make the scene and frustums a bit easier to see
        	// (note the extra scale could have been part of the initializaiton of
        	// the tbc track ball camera, but this is eaiser)      
        	
        	tbc.prepareForDisplay(drawable);
            gl.glScaled(15,15,15);  
            
            
            //draw yellow rectangle
            gl.glPushMatrix();
            gl.glColor3d(1, 1, 0);
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(-w/2, -h/2);
            gl.glVertex2d(w/2, -h/2);
            gl.glVertex2d(w/2, h/2);
            gl.glVertex2d(-w/2, h/2);
            gl.glEnd();
            gl.glPopMatrix();

            
            gl.glPushMatrix();
    		gl.glTranslated(eyeXOffset.getValue(), eyeYOffset.getValue(), eyeZPosition.getValue());
    		gl.glColor3d(1, 1, 1);
    		glut.glutSolidSphere(0.0125, 300, 300);
    		gl.glPopMatrix();
    		
    		//draw focal rectangle
    		gl.glPushMatrix();
    		gl.glTranslated(0, 0, FRD);
    		gl.glColor3d(200.0/255.0, 200.0/255.0, 200.0/255.0);
    		gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(wl, hb);
            gl.glVertex2d(wr, hb);
            gl.glVertex2d(wr, ht);
            gl.glVertex2d(wl, ht);
            gl.glEnd();
            gl.glPopMatrix();
            
            // TODO: Objective 2 - draw camera frustum if drawCenterEyeFrustum is true
            if(drawCenterEyeFrustum.getValue()) {
            	
            	//converter.reconstitute();
            	gl.glPushMatrix();
            	gl.glTranslated(eyeXOffset.getValue(), eyeYOffset.getValue(), eyeZPosition.getValue());
            	//gl.glFrustum(-1,1,-1,1,-nearZPosition.getValue()+eyeZPosition.getValue(), -farZPosition.getValue()+eyeZPosition.getValue());
            	gl.glColor3d(1,1,1);
            	gl.glMultMatrixd(invert.asArray(), 0);           	
            	glut.glutWireCube(2);
            	gl.glPopMatrix();
            	
            	
            }
            
            
            tbc2.applyViewTransformation(drawable); // only the view transformation
            scene.display( drawable );
            // TODO: Objective 6 - draw left and right eye frustums if drawEyeFrustums is true
            
        } else if ( viewingMode == 2 ) {
        	
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadMatrixd(matrix.asArray(),0);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslated(-eyeXOffset.getValue(), -eyeYOffset.getValue(), -eyeZPosition.getValue());
            
            
            
        	//gl.glScaled(15, 15, 15);     
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glColor3d(1, 1, 0);
            gl.glVertex2d(-w/2, -h/2);
            gl.glVertex2d(w/2, -h/2);
            gl.glVertex2d(w/2, h/2);
            gl.glVertex2d(-w/2, h/2);
            gl.glEnd();
            
          //draw focal rectangle
    		gl.glPushMatrix();
    		gl.glTranslated(0, 0, FRD);
    		gl.glColor3d(200.0/255.0, 200.0/255.0, 200.0/255.0);
    		gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(wl, hb);
            gl.glVertex2d(wr, hb);
            gl.glVertex2d(wr, ht);
            gl.glVertex2d(wl, ht);
            gl.glEnd();
            gl.glPopMatrix();
            
            // TODO: Objective 2 - draw camera frustum if drawCenterEyeFrustum is true
            if(drawCenterEyeFrustum.getValue()) {
            	
            	gl.glPushMatrix();
            	gl.glTranslated(eyeXOffset.getValue(), eyeYOffset.getValue(), eyeZPosition.getValue());
            	gl.glMultMatrixd(invert.asArray(), 0);   
            	gl.glColor3d(1, 1, 1);
            	glut.glutWireCube(2);
            	gl.glPopMatrix();
            	
            }
            tbc2.applyViewTransformation(drawable); // only the view transformation
            scene.display( drawable );
            
            
            
        	
        } else if ( viewingMode == 3 ) {            
        	
        	int numSamples = samples.getValue();
        	double apertureSize = aperture.getValue();
        	//draw yellow rectangle
            gl.glPushMatrix();
            gl.glColor3d(1, 1, 0);
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(-w/2, -h/2);
            gl.glVertex2d(w/2, -h/2);
            gl.glVertex2d(w/2, h/2);
            gl.glVertex2d(-w/2, h/2);
            gl.glEnd();
            gl.glPopMatrix();
        	
        	//draw focal rectangle
    		gl.glPushMatrix();
    		gl.glTranslated(0, 0, FRD);
    		gl.glColor3d(200.0/255.0, 200.0/255.0, 200.0/255.0);
    		gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2d(wl, hb);
            gl.glVertex2d(wr, hb);
            gl.glVertex2d(wr, ht);
            gl.glVertex2d(wl, ht);
            gl.glEnd();
            gl.glPopMatrix();
            
            //Objective 2 - draw camera frustum if drawCenterEyeFrustum is true
          
            
            // TODO: Objective 6 - draw left and right eye frustums if drawEyeFrustums is true
        	
        	
        	//System.out.println(apertureSize);
        	for(int i  = 0; i<numSamples; i++) {
        		Point2d p = new Point2d();
        		disk.get(p, i, numSamples);
        		double valueX = p.x*apertureSize;
        		double valueY = p.y*apertureSize;
        		//System.out.println(valueX + "," + valueY);
        		double newWlOffset = wl-valueX;
        		//System.out.println(wlOffset);
        		double newWrOffset = wr-valueX;
        		double newHbOffset = hb-valueY;
        		double newHtOffset = ht-valueY;
        		
        		double newNearwl = (-w*nearVar/(2*eyeP))*newWlOffset/wl;
                double newNearwr = (w*nearVar/(2*eyeP))*newWrOffset/wr;
                double newNearhb = (-h*nearVar/(2*eyeP))*newHbOffset/hb;
                double newNearht= (h*nearVar/(2*eyeP))*newHtOffset/ht;
                
                
                //prepare frustum
                gl.glMatrixMode(GL2.GL_PROJECTION);
            	gl.glPushMatrix();
            	gl.glLoadIdentity();
            	gl.glFrustum(newNearwl, newNearwr, newNearhb, newNearht, nearVar, farVar);
            	//System.out.println(nearwl+", " + nearwr + ", " + nearhb+ "," + nearht + "," + nearVar+ "," + farVar);
            	//gl.glFrustum(-w/2, w/2, -h/2, h/2, (-farZPosition.getValue()+eyeZPosition.getValue())*0.1/(-nearZPosition.getValue()+eyeZPosition.getValue()), -farZPosition.getValue()+eyeZPosition.getValue());
            	FlatMatrix4d newMatrix = new FlatMatrix4d();
            	FlatMatrix4d newInvert = new FlatMatrix4d();
            	gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, newMatrix.asArray(), 0);
            	gl.glPopMatrix();
            	newMatrix.reconstitute();
            	newInvert.getBackingMatrix().invert(newMatrix.getBackingMatrix());
            	gl.glMatrixMode(GL2.GL_MODELVIEW);
            	
            	if(drawCenterEyeFrustum.getValue()) {
                	
                	//converter.reconstitute();
                	gl.glPushMatrix();
                	gl.glTranslated(valueX, valueY, eyeZPosition.getValue());
                	//gl.glFrustum(-1,1,-1,1,-nearZPosition.getValue()+eyeZPosition.getValue(), -farZPosition.getValue()+eyeZPosition.getValue());
                	gl.glColor3d(1,1,1);
                	gl.glMultMatrixd(newInvert.asArray(), 0);           	
                	glut.glutWireCube(2);
                	gl.glPopMatrix();
                	
                	
                }
            	
            	gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);
	        	gl.glMatrixMode(GL2.GL_PROJECTION);
	            gl.glLoadMatrixd(newMatrix.asArray(),0);
	            gl.glMatrixMode(GL2.GL_MODELVIEW);
	            gl.glLoadIdentity();
	            gl.glTranslated(-valueX, -valueY, -eyeZPosition.getValue()); 
	            tbc2.applyViewTransformation(drawable); // only the view transformation
	            scene.display( drawable );
                if(i==0) {
                	gl.glAccum(GL2.GL_LOAD,(float)1.0/(float)numSamples);
                	//System.out.println((float)1.0/(float)numSamples);
                }
                else {
                	gl.glAccum(GL2.GL_ACCUM, (float)1.0/(float)numSamples);
                }
        	}
        	gl.glAccum(GL2.GL_RETURN, 1);
            
        	
        	
            
        } else if ( viewingMode == 4 ) {
        	
            // TODO: Objective 6 - draw the left eye view
        	
        } else if ( viewingMode == 5 ) {  
        	
        	// TODO: Objective 6 - draw the right eye view
        	                               
        } else if ( viewingMode == 6 ) {            
        	
        	// TODO: Objective 7 - draw the anaglyph view using glColouMask
        	
        } else if ( viewingMode == 7 ) {            
        	
        	// TODO: Bonus Ojbective 8 - draw the anaglyph view with depth of field blur
        	
        }        
    }
    
}
