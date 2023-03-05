/**
 *  Title: Distributed multi-player racing game.
 *  Date: 21/04/2023
 *  @author 2018481
 *  Description: Display a set of multi-angled car images in both a continuous loop
 *  and operated by key press.
 */
package CarSpin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamepanelCS extends JPanel {

    private BufferedImage m_imageR, m_imageP;
    private Timer animationTimer;
    private ImageIcon greenCar[], policeCar[];
    private final int totalImages = 16;
    private int currentImage = 0;
    private int spinImage = 0;
    private final int animation_delay = 100;

    /**
     * Read in two sets of car images into arrays.
     */
    public GamepanelCS() {
        greenCar = new ImageIcon[totalImages];
        policeCar = new ImageIcon[totalImages];

        // get images and save to array
        try {
            for (int i = 0; i < greenCar.length; i++) {
                int imageIndex = i + 1;

                m_imageR = getImage("/carGreen/carGreen" + imageIndex + ".png");
                greenCar[i] = new ImageIcon(m_imageR);

                m_imageP = getImage("/carPolice/carPolice" + imageIndex + ".png");
                policeCar[i] = new ImageIcon(m_imageP);
            }

        } catch (Exception e) {
            //throw new RuntimeException("Car list error: "+e);
            System.out.println("car list error: " + e.getMessage());
        }
    }

    /**
     * Paint car images and increment one set to appear spinning if timer is active.
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        greenCar[spinImage].paintIcon(this, g, 100, 200);
        policeCar[currentImage].paintIcon(this, g, 300, 200);
        if (animationTimer.isRunning()) {
            spinImage = (spinImage + 1) % totalImages;
        }
    }

    /**
     * Start timer, restart if not active.
     */
    public void startAnimation() {
        if (animationTimer == null) {
            spinImage = 0;
            animationTimer = new Timer(animation_delay, new TimerHandler());
            animationTimer.start();
        } else {
            if (!animationTimer.isRunning()) {
                animationTimer.restart();
            }
        }
    }

    /**
     * Stop timer
     */
    public void stopAnimation() {
        animationTimer.stop();
    }

    /**
     * Refresh screen using repaint method after each execution of the timer.
     * Timer delay set globally for 100 milliseconds.
     */
    private class TimerHandler implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            repaint();
        }
    }

    /**
     * Monitor index of currently displayed image for key press controlled
     * car image.
     * @param imageTurn - select image from array without going out of bounds.
     */
    public void turnImg(int imageTurn)
    {

        if((currentImage+imageTurn) == -1)
        {
            currentImage = 15;
        }
        else if ((currentImage+imageTurn) == 16)
        {
            currentImage = 0;
        }
        else
        {
            currentImage = currentImage + imageTurn;
        }

    }

    /**
     * Using URL set file path for car images for adding to arrays.
     * @param name - file name of image to return
     * @return - URL image file name and folder
     */
    private BufferedImage getImage(String name) {
        BufferedImage img = null;    // The image
        URL url;                    // URL of image
        // Get full path
        name = "/Images/" + name;
        // Get resource URL of image
        url = this.getClass().getResource(name);
        if (url == null) {
            System.err.println("Failed to load Image");
        } else {
            // Read the image
            try {
                img = ImageIO.read(url);
            } catch (Exception ex) {
                System.err.println("Error loading image\n" + ex.getMessage());
            }
        }
        return img;
    }

}