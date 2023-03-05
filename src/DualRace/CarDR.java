/**
 * Title:
 * Date:
 * Author: 2018481
 * Description:
 */
package DualRace;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game using client server setup.
 */
public class CarDR {

    private double m_x, m_y,m_lap;
    private int m_speed,maxSpeed = 20,m_currentImage,m_maxImage=16;
    public CarDR(int currentImage, double x, double y, int speed, double lap) {
        m_x = x;
        m_y = y;
        m_speed = speed;
        m_lap = lap;
        m_currentImage = currentImage;
    }

    public int getCurrentImage() { return m_currentImage; }
    public void setCurrentImage(int value) { m_currentImage = value; }
    public double getX() {
        return m_x;
    }
    public double getY() {
        return m_y;
    }
    public void setY(double value){ m_y = value; }
    public int getSpeed() {
        return m_speed;
    }
    public void setSpeed(int value) {
        if(m_speed+value != maxSpeed){m_speed = m_speed + value;}
    }
    public double getLap() { return m_lap; }
    public void setLap(double value) { m_lap = m_lap+value; }

    /**
     * Create a boundary for each car and rotate according the image displayed.
     * Called by the checkCollision method in Gamepanel.
     * @return rotated shape
     */
    public Shape getCarBoundary()
    {
        Rectangle2D r = new Rectangle2D.Double((m_x)+8,(m_y)+15,33,17);
        // calc angle from 0 degrees not from previous position and rotate
        double angle = (m_currentImage-4) * 22.5;
        AffineTransform af = new AffineTransform();
        af.rotate(Math.toRadians(angle),r.getCenterX(),r.getCenterY());
        //System.out.println("Bound CentreX: "+r.getCenterX()+", CentreY: "+r.getCenterY());
        return  af.createTransformedShape(r);
    }

    /**
     * Change image index by minus 1
     */
    public void rotateLeft(){
        if (--m_currentImage < 0)
        {
            m_currentImage = m_maxImage-1;
        }
    }

    /**
     * Change image index by positive 1
     */
    public void rotateRight(){
        if (++m_currentImage >= m_maxImage)
        {
            m_currentImage = 0;
        }
    }
    /**
     * Calculate the direction movement of a car in any of the 16 directions at an
     * interval of 22.5 degrees. Calculation uses the index of the current
     * displayed image
     */
    public void moveImg()
    {
        double angle = (m_currentImage - 4) * 22.5;
        double x=0,y=0;
        double angleToRadians = Math.toRadians(angle);
        x += Math.cos(angleToRadians);
        y += Math.sin(angleToRadians);
        m_x = m_x+(x * m_speed);
        m_y = m_y+(y * m_speed);
        //System.out.println("MoveImg x: "+m_x+", y: "+m_y);
        //System.out.println("Speed: "+m_speed);
    }

    /**
     * Reset car start position to the racetrack start line.
     */
    public void resetCars()
    {
        m_x = 365;
        m_y = 30;
        m_speed = 0;
        m_currentImage = 4;
    }
}


