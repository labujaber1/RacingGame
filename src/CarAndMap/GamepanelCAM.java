/**
 * Title: Distributed multiplayer racing game.
 * <p>Description: Local two player car race game operating from the same keyboard.</p>
 * Date: 21/04/2023
 * @author labuj 2018481
 * @version 1.2
 */
package CarAndMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Game panel extends JPanel contains methods for to display and race cars on chosen racetrack
 * for two players.
 */
public class GamepanelCAM extends JPanel {

    private BufferedImage m_imageG, m_imageP,m_crowd,m_cup,m_tree,m_wall,m_bush;
    private Timer animationTimer;
    private ImageIcon greenCarArr[], policeCarArr[];
    private CarCAM m_greenCar,m_policeCar;
    private TrackCAM m_track;
    private int m_trackChoice;
    private SoundCAM sound;
    private Boolean go = false;
    private String m_crashGreen, m_crashPolice, m_crashCars, m_cheer, m_countdown;

    /**
     * Read in two sets of car images into arrays.
     */
    public GamepanelCAM() {
        // instantiate cars and image arrays
        sound = new SoundCAM();
        m_trackChoice = chooseTrack();
        int totalImages = 16;
        greenCarArr = new ImageIcon[totalImages];
        policeCarArr = new ImageIcon[totalImages];
        m_crashGreen = ("Sounds/fast-collision.wav");
        m_crashPolice = ("Sounds/clank-car-crash.wav");
        m_crashCars = ("Sounds/squish.wav");
        m_cheer = ("Sounds/cheer.wav");
        m_countdown = ("Sounds/countdown.wav");

        // get images of cars and save to array
        try {
            for (int i = 0; i < greenCarArr.length; i++) {
                int imageIndex = i + 1;
                m_imageG = getImage("carGreen/carGreen" + imageIndex + ".png");
                greenCarArr[i] = new ImageIcon(m_imageG);

                m_imageP = getImage("carPolice/carPolice" + imageIndex + ".png");
                policeCarArr[i] = new ImageIcon(m_imageP);
            }
            m_crowd = getImage("/crowd2.jpg");
            m_cup = getImage("/cup.jpg");
            m_tree = getImage("/tree1.jpg");
            m_wall = getImage("/wall1.jpg");
            m_bush = getImage("/bush1.jpg");
            m_greenCar = new CarCAM(4,365,30,0,0);
            m_policeCar = new CarCAM(4,365,80,0,0);
        } catch (Exception e) {
            System.out.println("car list error: " + e.getMessage());
        }
    }

    /**
     * Paint the racetrack of choice, allow car to move and
     * checkCollision if animation is running.
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        m_track = new TrackCAM(m_trackChoice,800,600);
        m_track.drawTrack(m_trackChoice,g);
        if (m_trackChoice == 0) {
            g2d.drawImage(m_crowd, 150, 140, null);
            g2d.drawImage(m_crowd, 382, 140, null);
            g2d.drawImage(m_crowd, 150, 350, null);
            g2d.drawImage(m_crowd, 382, 350, null);
            m_track.drawObstacle(g,m_bush,80,100,0,0);
        } else if (m_trackChoice == 1) {
            g2d.drawImage(m_tree,300,180,null);
            g2d.drawImage(m_tree,500,180,null);
            m_track.drawObstacle(g,m_bush,120,300,0,0);
            m_track.drawObstacle(g,m_wall,590,460,0,0);
        } else {System.exit(0);}
        // centre lap and speed display
        g2d.setColor(Color.white);
        g2d.setFont(new Font("MV Boli", Font.BOLD, 20));
        if(go) {
            g2d.drawString("GET READY", 450, 100);
            stopAnimation();
            resetGo();
        }
        // Draw lap update to centre of the map
        g2d.drawString("Green car laps: " + m_greenCar.getLap() / 10 + " speed: " + m_greenCar.getSpeed() * 10 + "mph", 180, 240);
        g2d.drawString("Police car laps: " + m_policeCar.getLap() / 10 + " speed: " + m_policeCar.getSpeed() * 10 + "mph", 180, 290);
        g2d.setColor(Color.yellow);
        // if car completes 3 laps announce the winner
        if (m_greenCar.getLap() == 30 || m_policeCar.getLap() == 30) {
            if (m_greenCar.getLap() == 30) {
                g2d.drawString("The winner is the green car well done", 190, 330);
            }
            if (m_policeCar.getLap() == 30) {
                g2d.drawString("The winner is the police car well done", 190, 330);
            }
            g2d.drawImage(m_cup, 580, 240, null);
            sound.cheer();
            stopAnimation();
        }

        try {
            if (animationTimer.isRunning()) {
                // move image and calc turn of car
                moveImage();
                greenCarArr[m_greenCar.getCurrentImage()].paintIcon(this, g2d, (int) m_greenCar.getX(), (int) m_greenCar.getY());
                policeCarArr[m_policeCar.getCurrentImage()].paintIcon(this, g2d, (int) m_policeCar.getX(), (int) m_policeCar.getY());
                // check if cars collide with boundary or each other
                checkCarCollision(g,m_track);
                // update lap count
                lapCount(g);
            }
        } catch (Exception e) {
            System.out.println("Error checking animation timer: " + e.getMessage());
        }
    }

    /**
     * User select track on program open. Return 0 for rectangle track 1 and 1 for oval track 2
     */
    public int chooseTrack()
    {
        String[] options = {"Track 1", "Track 2"};
        int trackChoice = JOptionPane.showOptionDialog(null,"Select a track", "Choose track",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        return trackChoice;
    }

    /**
     * Start animation, restart if not active.
     */
    public void startAnimation()
    {
        int animation_delay = 100;
        if (animationTimer == null) {
            animationTimer = new Timer(animation_delay, new TimerHandler());
            animationTimer.start();
        } else {
            if (!animationTimer.isRunning()) {
                animationTimer.restart();
            }
        }
    }

    /**
     * Stop animation to use in a car collision and end the game.
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
     * Used in mainframe to add 1 to speed on each key press
     * @param carSpeed - speed of car in integer
     * @param carId - id of car in integer
     */
    public void setCarSpeed(int carSpeed,int carId){
        if(carId == 1) {
            m_greenCar.setSpeed(carSpeed);
        }
        else {
            m_policeCar.setSpeed(carSpeed);
        }
    }

    /**
     * Change green car current image by positive one
     */
    public void greenRight(){
        m_greenCar.rotateRight();
    }

    /**
     * Change green car current image by negative one.
     */
    public void greenLeft(){
        m_greenCar.rotateLeft();
    }

    /**
     * Change police car current image by positive one.
     */
    public void policeRight(){
        m_policeCar.rotateRight();
    }

    /**
     * Change police car current image by negative one.
     */
    public void policeLeft(){
        m_policeCar.rotateLeft();
    }

    /**
     * Move each car according to current image instigated by speed control
     * key press
     */
    public void moveImage()
    {
        m_greenCar.moveImg();
        m_policeCar.moveImg();
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

    /**
     * Checks for collision between cars and track boundaries.
     * Returns opposite speed direction (for fun) on collision with boundary (bounce) or
     * end game for collisions between cars.
     * @param g - graphics interface
     */
    public void checkCarCollision(Graphics g,TrackCAM track)
    {
        Shape green =  m_greenCar.getCarBoundary();
        Shape police =  m_policeCar.getCarBoundary();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.yellow);
        g2d.draw(police);
        g2d.draw(green);
        // hit map boundaries
        if(track.getInnerBounds().intersects(green.getBounds2D()) || !track.getOuterBounds().intersects(green.getBounds2D())
                || track.getObstacleBounds().intersects(green.getBounds2D()))
        {
            sound.crashGreen();
            if(m_greenCar.getSpeed() > 0) {
                m_greenCar.setSpeed(-m_greenCar.getSpeed() - m_greenCar.getSpeed());
            }
            else {
                m_greenCar.setSpeed(-m_greenCar.getSpeed() - m_greenCar.getSpeed());
            }
            System.out.println("Green car crashed into boundary");
        }
        if(track.getInnerBounds().intersects( police.getBounds2D()) || !track.getOuterBounds().intersects(police.getBounds2D())
                || track.getObstacleBounds().intersects(police.getBounds2D()))
       {
            sound.crashPolice();
            if(m_policeCar.getSpeed() > 0) {
                m_policeCar.setSpeed(-m_policeCar.getSpeed() - m_policeCar.getSpeed());
            }
            else {
                m_policeCar.setSpeed(-m_policeCar.getSpeed() - m_policeCar.getSpeed());
            }
            System.out.println("Police car crashed into boundary");
        }
        // hit other car and end the game
        if(police.intersects(green.getBounds2D()) || green.intersects(police.getBounds2D()))
        {
            sound.crashCars();
            m_policeCar.setSpeed(-m_policeCar.getSpeed());
            m_greenCar.setSpeed(-m_greenCar.getSpeed());
            stopAnimation();
            // end game message
            String infoMess = "You have crashed the cars.\n The game is over";
            JOptionPane.showMessageDialog(null,infoMess,"Racing game warning",JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Cars have collided");
            System.exit(0);
        }
    }

    /**
     * Calculate and update lap count according to passing rectangles at start and at halfway point.
     * @param g - graphics interface
     */
    public void lapCount(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Rectangle r1 = new Rectangle( 417, 30, 12, 100); // at start line
        Rectangle r2 = new Rectangle(418,410,4,100);  // at halfway point

        Shape green =  m_greenCar.getCarBoundary();
        Shape police =  m_policeCar.getCarBoundary();

        g2d.draw(r1);
        g2d.draw(r2);
        g2d.dispose();
        if (green.intersects(r1) && m_greenCar.getLap()%2==1) {
            m_greenCar.setLap(3);
        }
        if (green.intersects(r2) && m_greenCar.getLap()%2==0) {
            m_greenCar.setLap(7);
        }
        if (police.intersects(r1) && m_policeCar.getLap()%2==1) {
            m_policeCar.setLap(3);
        }
        if (police.intersects(r2) && m_policeCar.getLap()%2==0) {
            m_policeCar.setLap(7);
        }
    }

     /**
     * Plays a 3,2,1,go countdown sound file while display ready text,
      * restarts animation and stops displaying text on countdown completion.
     */
    public void resetGo()
    {
        //Plays a 3,2,1,go countdown sound file while display ready text
        sound.countdown();
        int delayT = 5000;
        ActionListener taskPerformer = evt -> go = false;
        ActionListener task = evt -> startAnimation ();
        new Timer(delayT, taskPerformer).start();
        new Timer(delayT,task).start();

    }

    /**
     * Move cars back to start line and set boolean go to true
     * to display ready text while verbal countdown plays.
     */
    public void resetAllCars()
    {
        m_greenCar.resetCars();
        m_policeCar.resetCars();
        m_policeCar.setY(80);
        go = true;
    }

}