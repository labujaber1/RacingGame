/**
 * Title:
 * Date:
 * Author: 2018481
 * Description:
 */
package DualRace;

import java.awt.*;
import java.awt.geom.*;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game using client server setup.
 */
public class CarDR {

    private double m_x, m_y,m_lap;
    private int m_speed,m_currentImageIndex,maxSpeed = 20,maxNegSpeed = -20,maxImage=16;
    public CarDR(int currentImageIndex, double x, double y, int speed, double lap) {
        m_currentImageIndex = currentImageIndex;
        m_x = x;
        m_y = y;
        m_speed = speed;
        m_lap = lap;
    }

    public int getCurrentImage() { return m_currentImageIndex; }
    public void setCurrentImage(int value) { m_currentImageIndex = value; }
    public double getX() {
        return m_x;
    }
    public double getY() {
        return m_y;
    }
    public void setX(double value){ m_x = value; }
    public void setY(double value){ m_y = value; }
    public int getSpeed() {
        return m_speed;
    }
    // set speed only within a range of -20 to 20 else no return.
    public void setSpeed(int value) {
        if(m_speed+value < maxSpeed && m_speed+value > maxNegSpeed){m_speed = m_speed + value;}
    }
    public double getLap() { return m_lap; }
    public void setLap(double value) { m_lap = m_lap+value; }

    /**
     * Create a boundary for each car and rotate according the image displayed.
     * Called by the checkCollision method in Gamepanel.
     *
     * @return rotated affineTransformed shape
     */
    public Rectangle2D getCarBoundary(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        //Rectangle2D r = new Rectangle2D.Double((m_x)+7,(m_y)+17,34,15);
        Rectangle2D r = new Rectangle2D.Double(m_x+13,m_y+13,22,22);
        // calc angle from 0 degrees not from previous position and rotate
        double angle = (m_currentImageIndex-4) * 22.5;
        AffineTransform af = new AffineTransform();

        af.rotate(Math.toRadians(angle),r.getCenterX(),r.getCenterY());
        // Test check where boundary is by drawing and compare with collision getBounds2D method.
        g2d.draw(af.createTransformedShape(r));
        //return af.createTransformedShape(r);

        return r;
     }


    /**
     * Change image index by minus 1
     */
    public void rotateLeft(){
        if (--m_currentImageIndex < 0)
        {
            m_currentImageIndex = maxImage-1;
        }
    }

    /**
     * Change image index by positive 1
     */
    public void rotateRight(){
        if (++m_currentImageIndex >= maxImage)
        {
            m_currentImageIndex = 0;
        }
    }
    /**
     * Calculate the direction movement of a car in any of the 16 directions at an
     * interval of 22.5 degrees. Calculation uses the index of the current
     * displayed image
     */
    public void moveImg()
    {
        double angle = (m_currentImageIndex - 4) * 22.5;
        double x=0,y=0;
        double angleToRadians = Math.toRadians(angle);
        x += Math.cos(angleToRadians);
        y += Math.sin(angleToRadians);
        m_x = m_x+(x * m_speed);
        m_y = m_y+(y * m_speed);
    }

    /**
     * Reset car start position to the racetrack start line.
     */
    public void resetCars()
    {
        m_x = 365;
        m_y = 30;
        m_speed = 0;
        m_currentImageIndex = 4;
    }



    public void setCarData(String data)
    {
        String[] carData = data.split(",");
        setCurrentImage(Integer.parseInt(carData[0]));
        setX(Double.parseDouble(carData[1]));
        setY(Double.parseDouble(carData[2]));
        setSpeed(Integer.parseInt(carData[3]));
        setLap(Double.parseDouble(carData[4]));
    }
}


