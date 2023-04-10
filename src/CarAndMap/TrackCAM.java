/**
 * Title: Distributed multiplayer racing game.
 * <p>Description: Local two player car race game operating from the same keyboard.</p>
 * Date: 21/04/2023
 * @author labuj 2018481
 * @version 1.2
 */
package CarAndMap;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Draws 2 different tracks, track boundaries, and obstacles.
 */
public class TrackCAM {

    private int m_trackId, m_trackWidth, m_trackHeight;

    private RoundRectangle2D.Double outerBoundsRect;
    private RoundRectangle2D.Double innerBoundsRect;
    private Ellipse2D.Double outerBoundsEllip;
    private Ellipse2D.Double innerBoundsEllip;
    private Rectangle2D.Double obstacleBound;

    /**
     * Racetrack constructor
     * @param trackId - id of a racetrack
     * @param width - racetrack preferred width
     * @param height - racetrack preferred height
     */
    public TrackCAM(int trackId,int width, int height){
        m_trackId = trackId;
        m_trackWidth = width;
        m_trackHeight = height;
    }

    /**
     *
     * @param trackId - user choice of racetrack to use
     * @param g - graphics interface
     */
    public void drawTrack(int trackId,Graphics g){
        if (trackId == 0)
            drawTrack1(g);
        else
            drawTrack2(g);
    }
    /**
     * Simple rectangle racetrack containing central images of crowds based on map2
     * @param g - graphics interface
     */
    public void drawTrack1(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        innerBoundsRect = new RoundRectangle2D.Double(150, 140, 520, 260, 20, 20);
        outerBoundsRect = new RoundRectangle2D.Double(30, 20, 770, 500, 20, 20);

        //Outer area of the racetrack
        g2d.setColor(Color.gray);
        g2d.fillRoundRect(10, 5, 810, 525, 20, 20);

        //Outer bounds of racetrack
        g2d.setColor(Color.red);
        g2d.fill(outerBoundsRect);
        g2d.setColor(Color.black);
        g2d.draw(outerBoundsRect);

        //Racing track
        g2d.setColor(Color.lightGray);
        g2d.fillRoundRect(40, 30, 750, 480, 20, 20);

        //Central area
        g2d.setColor(Color.white);
        g2d.fillRoundRect(135, 130, 550, 280, 20, 20);

        //Inner area of racetrack
        g2d.setColor(Color.blue);
        g2d.fill(innerBoundsRect);
        g2d.setColor(Color.black);
        g2d.draw(innerBoundsRect);
        //Starting line
        g2d.setColor(Color.black);
        g2d.drawLine(418, 30, 418, 130);
        g2d.setColor(Color.white);
        g2d.drawLine(420, 30, 420, 130);
        g2d.drawLine(422, 30, 422, 130);
        g2d.drawLine(424, 30, 424, 130);
        g2d.drawLine(426, 30, 426, 130);
        g2d.setColor(Color.black);
        g2d.drawLine(428, 30, 428, 130);

        // half lap marker
        g2d.drawLine(418,410,418,510);
                
    }

    /**
     * Returns inner boundary for track chosen using track ID
     * @return inner bounds of either a rectangle or ellipse shaped racetrack
     */
    public Shape getInnerBounds()
    {
        if(m_trackId == 0)
            return innerBoundsRect;
        else
            return innerBoundsEllip;
    }

    /**
     * Returns outer boundary for track chosen using track ID
     * @return outer bounds of either a rectangle or ellipse shaped racetrack
     */
    public Shape getOuterBounds()
    {
        if(m_trackId == 0)
            return outerBoundsRect;
        else
            return outerBoundsEllip;
    }
    /**
     * Base racetrack taken from the assignment brief and adapted to
     * produce map1 with colour and rounded edges.
     * @param g - graphics interface
     */
    public void drawTrack2(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        innerBoundsEllip = new Ellipse2D.Double( 150, 130,550, 280);
        outerBoundsEllip = new Ellipse2D.Double( 20, 30,790, 480);

        Color c1 = Color.green;
        g2d.setColor( c1 );
        g2d.fillOval( 150, 130, 550, 280); // grass
        Color c2 = Color.black;
        g2d.setColor( c2 );
        g2d.draw( outerBoundsEllip); // outer edge
        g2d.draw( innerBoundsEllip); // inner edge
        Color c3 = Color.yellow;
        g2d.setColor( c3 );
        g2d.drawOval( 80, 80, 680, 380 ); // mid-lane marker
        Color c4 = Color.white;
        g2d.setColor( c4 );
        g2d.drawLine( 425, 410, 425, 510 ); // start line
        g2d.drawLine(418, 30, 418, 130); // halfway point
    }

    /**
     * Create and draw obstacle from image param including outer bounds for collision detection.
     * @param g - graphics interface
     * @param im - buffered image
     * @param x - x-axis image position
     * @param y - y-axis image position
     * @param w - width of image to create shape
     * @param h - height of image to create shape
     */
    public void drawObstacle(Graphics g,BufferedImage im,double x, double y,double w, double h)
    {
        Graphics2D g2d = (Graphics2D) g;
        w = im.getWidth();
        h = im.getHeight();
        obstacleBound = new Rectangle2D.Double(x,y,w,h);
        g2d.drawImage(im, (int)x,(int)y,null);
    }

    /**
     * Return obstacle bounds for car collision detection.
     * @return - outer bounds of the obstacle
     */
    public Rectangle2D getObstacleBounds()
    {
        return obstacleBound;
    }

}
