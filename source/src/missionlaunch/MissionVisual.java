package missionlaunch;
import com.mhuss.AstroLib.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;

public class MissionVisual extends JPanel implements Visuals{
    
    private final static int P_HEIGHT = 1024;
    private final static int P_WIDTH = 1280;
    final static double MARS_APHELION = 249230000.0; 
    final static double JUPITER_APHELION = 816600000.0;
    final static double SATURN_APHELION = 1514500000.0; 
    final static double URANUS_APHELION = 3003600000.0; 
    final static double NEPTUNE_APHELION = 4545700000.0; 
    
    int scopeIndex = 0;
    
    double aphelion = MARS_APHELION;
    double px = aphelion/(P_HEIGHT/2.0);
    double centerX = P_WIDTH/2.0;
    double centerY = P_HEIGHT/2.0;
    double shuttleX = 0.0;
    double shuttleY = 0.0;
    
    BufferedImage board;
    BufferedImage shuttle;
    BufferedImage animateShuttle = null;
    BufferedImage[] orbits = new BufferedImage[5];
    
    Graphics2D g2d;
    
    ABody sun = ABody.SUN;
    ABody mercury = ABody.MERCURY;
    ABody venus = ABody.VENUS;
    ABody earth = ABody.EARTH;
    ABody mars = ABody.MARS;
    ABody jupiter = ABody.JUPITER;
    ABody saturn = ABody.SATURN;
    ABody uranus = ABody.URANUS;
    ABody neptune = ABody.NEPTUNE;
    
    Ellipse2D.Double sunShape = new Ellipse2D.Double(600,472,80,80);
    Ellipse2D.Double earthShape = new Ellipse2D.Double(-500,-500,40,40);
    Ellipse2D.Double mercuryShape = new Ellipse2D.Double(-500,-500,20,20);
    Ellipse2D.Double venusShape = new Ellipse2D.Double(-500,-500,40,40);
    Ellipse2D.Double marsShape = new Ellipse2D.Double(-500,-500,30,30);
    Ellipse2D.Double jupiterShape = new Ellipse2D.Double(-500,-500,40,40);
    Ellipse2D.Double saturnShape = new Ellipse2D.Double(-500,-500,40,40);
    Ellipse2D.Double uranusShape = new Ellipse2D.Double(-500,-500,40,40);
    Ellipse2D.Double neptuneShape = new Ellipse2D.Double(-500,-500,40,40);
    
    GregorianCalendar visualDate = new GregorianCalendar();
    ObsInfo observerInfo = new ObsInfo();
    
    VisualViewPort visualViewPort;
    
    public MissionVisual(JViewport jvp){
        board = new BufferedImage(P_WIDTH, P_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = board.createGraphics();
        g2d.setColor(Color.RED);
        Dimension size = new Dimension(P_WIDTH, P_HEIGHT);
        setBackground(Color.BLACK);
        setForeground(Color.RED);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLayout(null);
        visualViewPort = (VisualViewPort)jvp;
        loadImages();
    }
    
    public void calculatePlanetPositions(GregorianCalendar date){
        if(visualDate != date){
            visualDate = date;
        }
        //int second = date.get(GregorianCalendar.SECOND);
        int minute = date.get(GregorianCalendar.MINUTE);
        int hour = date.get(GregorianCalendar.HOUR_OF_DAY);
        int day = date.get(GregorianCalendar.DAY_OF_MONTH);
        int year = date.get(GregorianCalendar.YEAR);
        int month = date.get(GregorianCalendar.MONTH)+1;
        AstroDate jd = new AstroDate(day,month,year,hour,0,0);
        mercury.calculateCoordinates(jd, px, centerX, centerY);
        venus.calculateCoordinates(jd, px, centerX, centerY);
        earth.calculateCoordinates(jd, px, centerX, centerY);
        mars.calculateCoordinates(jd, px, centerX, centerY);
        jupiter.calculateCoordinates(jd, px, centerX, centerY);
        saturn.calculateCoordinates(jd, px, centerX, centerY);
        uranus.calculateCoordinates(jd, px, centerX, centerY);
        neptune.calculateCoordinates(jd, px, centerX, centerY);
        
        mercuryShape.setFrame(mercury.getX()-mercuryShape.getWidth()/2,
                mercury.getY()-mercuryShape.getHeight()/2, 
                mercuryShape.getWidth(), mercuryShape.getHeight());
        
        venusShape.setFrame(venus.getX()-venusShape.getWidth()/2,
                venus.getY()-venusShape.getHeight()/2, 
                venusShape.getWidth(), venusShape.getHeight());
        
        earthShape.setFrame(earth.getX()-earthShape.getWidth()/2,
                earth.getY()-earthShape.getHeight()/2, 
                earthShape.getWidth(), earthShape.getHeight());
        
        marsShape.setFrame(mars.getX()-marsShape.getWidth()/2,
                mars.getY()-marsShape.getHeight()/2, 
                marsShape.getWidth(), marsShape.getHeight());
        
        jupiterShape.setFrame(jupiter.getX()-jupiterShape.getWidth()/2,
                jupiter.getY()-jupiterShape.getHeight()/2, 
                jupiterShape.getWidth(), jupiterShape.getHeight());
        
        saturnShape.setFrame(saturn.getX()-saturnShape.getWidth()/2,
                saturn.getY()-saturnShape.getHeight()/2, 
                saturnShape.getWidth(), saturnShape.getHeight());
        
        uranusShape.setFrame(uranus.getX()-uranusShape.getWidth()/2,
                uranus.getY()-uranusShape.getHeight()/2, 
                uranusShape.getWidth(), uranusShape.getHeight());
        
        neptuneShape.setFrame(neptune.getX()-neptuneShape.getWidth()/2,
                neptune.getY()-neptuneShape.getHeight()/2, 
                neptuneShape.getWidth(), neptuneShape.getHeight());
        
        repaint();
        String minutes = minute+"";
        if(minute < 10){
            minutes = "0"+minutes;
        }
        visualViewPort.setDate(year+"-"+month+"-"+day+" "+hour+":"+minutes+" UT");
    }
    
    public void changeScope(double aphel){   
        aphelion = aphel;
        px = aphelion/(P_HEIGHT/2.0);
        centerX = P_WIDTH/2.0;
        centerY = P_HEIGHT/2.0;
        
        if(aphel == MARS_APHELION){
            scopeIndex= 0;
            sunShape.setFrame(600,472,80,80);
            earthShape.setFrame(-500,-500,40,40);
            mercuryShape.setFrame(-500,-500,20,20);
            venusShape.setFrame(-500,-500,40,40);
            marsShape.setFrame(-500,-500,30,30);
            jupiterShape.setFrame(-500,-500,60,60);
            saturnShape.setFrame(-500,-500,60,60);
            uranusShape.setFrame(-500,-500,50,50);
            neptuneShape.setFrame(-500,-500,50,50);
        }
        else if(aphel == JUPITER_APHELION){
            scopeIndex= 1;
            sunShape.setFrame(628,500,24,24);
            earthShape.setFrame(-500,-500,16,16);
            mercuryShape.setFrame(-500,-500,10,10);
            venusShape.setFrame(-500,-500,16,16);
            marsShape.setFrame(-500,-500,14,14);
            jupiterShape.setFrame(-500,-500,20,20);
            saturnShape.setFrame(-500,-500,20,20);
            uranusShape.setFrame(-500,-500,18,18);
            neptuneShape.setFrame(-500,-500,18,18);
        }
        else if(aphel == SATURN_APHELION){
            scopeIndex= 2;
            sunShape.setFrame(632,504,16,16);
            earthShape.setFrame(-500,-500,10,10);
            mercuryShape.setFrame(-500,-500,6,6);
            venusShape.setFrame(-500,-500,10,10);
            marsShape.setFrame(-500,-500,8,8);
            jupiterShape.setFrame(-500,-500,14,14);
            saturnShape.setFrame(-500,-500,14,14);
            uranusShape.setFrame(-500,-500,12,12);
            neptuneShape.setFrame(-500,-500,12,12);
        }
        else if(aphel == URANUS_APHELION){
            scopeIndex= 3;
            sunShape.setFrame(636,508,8,8);
            earthShape.setFrame(-500,-500,5,5);
            mercuryShape.setFrame(-500,-500,4,4);
            venusShape.setFrame(-500,-500,5,5);
            marsShape.setFrame(-500,-500,5,5);
            jupiterShape.setFrame(-500,-500,10,10);
            saturnShape.setFrame(-500,-500,10,10);
            uranusShape.setFrame(-500,-500,10,10);
            neptuneShape.setFrame(-500,-500,10,10);
        }
        else{
            scopeIndex= 4;
            sunShape.setFrame(637,509,6,6);
            earthShape.setFrame(-500,-500,4,4);
            mercuryShape.setFrame(-500,-500,3,3);
            venusShape.setFrame(-500,-500,4,4);
            marsShape.setFrame(-500,-500,4,4);
            jupiterShape.setFrame(-500,-500,8,8);
            saturnShape.setFrame(-500,-500,10,10);
            uranusShape.setFrame(-500,-500,10,10);
            neptuneShape.setFrame(-500,-500,10,10);
        }
        calculatePlanetPositions(visualDate);
    }
    
    private void loadImages(){
        try{
            for(int i = 0; i < orbits.length; i++){
                BufferedImage img = ImageIO.read(new File("images/board"+i+".png"));
                orbits[i] = img;
            }
            BufferedImage img = ImageIO.read(new File("images/spaceshuttle.png"));
            shuttle = img;
        }
        catch (IOException e) {}
    }
    
    public void plot(TimeStep t){
        Location start = t.getStartLocation();
        Location end = t.getEndLocation();
        double stX = start.getX();
        double stY = start.getY();
        double endX = end.getX();
        double endY = end.getY();
        double x1 = stX/px+centerX;
        double y1 = -stY/px+centerY;
        double x2 = endX/px+centerX;
        double y2 = -endY/px+centerY;
        g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                                getDefaultConfiguration();
        BufferedImage fimg = gfx_config.createCompatibleImage(
                                        shuttle.getWidth(),
                                        shuttle.getHeight(),
                                        Transparency.TRANSLUCENT);
        Graphics2D gtemp = fimg.createGraphics();
        AffineTransform tx = AffineTransform.getRotateInstance(
                                -t.getAngle(), 
                                shuttle.getWidth()/2,
                                shuttle.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        gtemp.drawImage(op.filter(shuttle, null), 0, 0, null);
        gtemp.dispose();
        shuttleX = x2-shuttle.getWidth()/2;
        shuttleY = y2-shuttle.getHeight()/2;
        animateShuttle = fimg;
    }
    
    /*
    public void plot(TimeStep t){
        Location start = t.getStartLocation();
        Location end = t.getEndLocation();
        double stDist = start.getDistance();
        double stLon = start.getLongitude();
        double endDist = end.getDistance();
        double endLon = end.getLongitude();
        stDist = stDist/px;
        endDist = endDist/px;
        double x1 = stDist*Math.cos(stLon)+centerX;
        double y1 = -stDist*Math.sin(stLon)+centerY;
        double x2 = endDist*Math.cos(endLon)+centerX;
        double y2 = -endDist*Math.sin(endLon)+centerY;
        g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }
    */
    
    public void resetDrawingBoard(){
        board = new BufferedImage(P_WIDTH, P_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        animateShuttle = null;
        g2d = board.createGraphics();
        g2d.setColor(Color.RED);
    }
    
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(getBackground());
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.drawImage(orbits[scopeIndex],0,0,null);
        g2.setColor(Color.YELLOW);
        g2.fill(sunShape);
        g2.setColor(Color.GRAY);
        g2.fill(mercuryShape);
        g2.setColor(Color.PINK);
        g2.fill(venusShape);
        g2.setColor(Color.GREEN);
        g2.fill(earthShape);
        g2.setColor(Color.RED);
        g2.fill(marsShape);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(jupiterShape);
        g2.setColor(Color.ORANGE);
        g2.fill(saturnShape);
        g2.setColor(Color.CYAN);
        g2.fill(uranusShape);
        g2.setColor(Color.BLUE);
        g2.fill(neptuneShape);
        g2.drawImage(board,0,0,null);
        if(animateShuttle != null)
            g2.drawImage(animateShuttle, (int)shuttleX, (int)shuttleY, null);
    }
}
