package DualRace;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game using client server setup.
 */
public class GamepanelDR extends JPanel {

    private BufferedImage m_crowd,m_cup,m_tree,m_wall,m_bush;
    private Timer animationTimer;
    private ClientDR m_cdr ;
    private BufferedImage greenCarArr[], policeCarArr[];
    private CarDR m_greenCar,m_policeCar;
    private int m_trackChoice,m_player;
    private final SoundDR sound;
    private Boolean m_go = false, m_canStart = false, m_connect=false;
    private String m_crashGreen, m_crashPolice, m_crashCars, m_cheer, m_countdown, m_ipAddress;
    


    /**
     * Read in two sets of car images into arrays.
     */
    public GamepanelDR() {
        int totalImages = 16;
        sound = new SoundDR();
        // select track
        m_trackChoice = chooseTrack();

        
        // instantiate cars and image arrays
        greenCarArr = new BufferedImage[totalImages];
        policeCarArr = new BufferedImage[totalImages];

        m_crashGreen = ("Sounds/fast-collision.wav");
        m_crashPolice = ("Sounds/clank-car-crash.wav");
        m_crashCars = ("Sounds/squish.wav");
        m_cheer = ("Sounds/cheer.wav");
        m_countdown = ("Sounds/countdown.wav");

        // get images of cars and save to array
        try {
            for (int i = 0; i < greenCarArr.length; i++) {
                int imageIndex = i + 1;
                BufferedImage m_imageG = getImage("/carGreen/carGreen" + imageIndex + ".png");
                greenCarArr[i] =  m_imageG;
                BufferedImage m_imageP = getImage("/carPolice/carPolice" + imageIndex + ".png");
                policeCarArr[i] = m_imageP;
            }
            m_crowd = getImage("/crowd2.jpg");
            m_cup = getImage("/cup.jpg");
            m_tree = getImage("/tree1.jpg");
            m_wall = getImage("/wall1.jpg");
            m_bush = getImage("/bush1.jpg");
            m_greenCar = new CarDR(0,4,365,30,0,0);
            m_policeCar = new CarDR(0,4,365,80,0,0);
        } catch (Exception e) {
            sendToTextArea("\ncar list error: " + e.getMessage());
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
        TrackDR m_track = new TrackDR(m_trackChoice, 800, 600);
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
        // centre lap, speed display and start countdown
        g2d.setColor(Color.white);
        g2d.setFont(new Font("MV Boli", Font.BOLD, 20));
        if(m_go && m_canStart) {
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
            sound.Play(m_cheer);
            stopAnimation();
        }
        try {
            if (animationTimer.isRunning()) {
                // move image and calc turn of car
                moveImage();
                g2d.drawImage(greenCarArr[m_greenCar.getCurrentImage()],(int) m_greenCar.getX(), (int) m_greenCar.getY(),50,50,this);
                g2d.drawImage(policeCarArr[m_policeCar.getCurrentImage()],(int) m_policeCar.getX(), (int) m_policeCar.getY(),50,50,this);
                // check if cars collide with boundary or each other
                checkCarCollision(g, m_track);
                // update lap count
                lapCount(g);
            }

        } catch (Exception e) {
            startAnimation();
            sendToTextArea("\nError with animation timer when moving and checking collision with cars: " + e);
        }
    }

    /**
     * Main function to start client server and process data called by connect button press.
     *
     */
    public void startClientServer() {
        sendToTextArea("\nstartClientServer()");
        m_cdr = new ClientDR(getPortNumber(), serverIpAddress());
        //Thread updates = new Thread(() -> {
        // start client server
        m_cdr.start();
        //});
        //updates.start();

        sendToTextArea("\nThis client is player " + m_player);
        if (m_player == 1)
        {
            m_greenCar.setPlayerNum(1);
            m_policeCar.setPlayerNum(2);
        }
        if (m_player == 2)
        {
            m_policeCar.setPlayerNum(1);
            m_greenCar.setPlayerNum(2);
        }
   }

    public void checkConnect(boolean ans)
    {
        m_connect = ans;
    }

    /**
     * Read incoming data to either set player number, start game, or
     * update non controller car data
     */
    public void handleIncomingClientTraffic(String incoming) {
        //sendToTextArea("\nhandleClientTraffic");
        if(incoming!=null) {
            switch (incoming) {
                case "1", "2" -> {
                    setPlayer(incoming);
                    // update player car status and player number
                    if (m_player == 1) {
                        sendToTextArea("\nYou are Player 1 controlling the green car");
                        break;
                    }
                    if (m_player == 2) {
                        sendToTextArea("\nYou are player 2 controlling the police car");
                        break;
                    }
                    break;
                }
                case "Socket closed" -> {
                    try {
                        stopClientServer();
                        break;
                    } catch (Exception e) {
                        sendToTextArea("\nGamepanelDR received Socket closed message from server. Error closing clientServer: " + e);
                    }
                }
                case "GO" -> {
                    m_canStart = true;
                    break;
                }
                case "" -> {
                    sendToTextArea("\nEmpty incoming message");
                    break;
                }
                default -> {
                    unpackNonController(incoming);
                    break;
                }
            }
        }
        else
        {
            sendToTextArea("\nhandleClientTraffic finished empty incoming message");
        }
    }

    /**
     * Get ip address from the user via JOption pane
     */
    public String serverIpAddress()
    {
        String res = JOptionPane.showInputDialog("Enter an IP address","localhost");
        if(res == null || res.equals("2"))
        {
            System.exit(0);
        }
        return res;
    }

    /**
     * Retrieve port number from user for connection to server via JOption pane
     * @return port number
     */
    public int getPortNumber()
    {
        int ret = 0;
        while(ret == 0)
        {
            String res = JOptionPane.showInputDialog("Enter a port number","8887");
            if (res.isBlank() || res.equals("2")) {
                System.exit(0);
            }
            try
            {
                ret = Integer.parseInt(res);
            } catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "This is not a number try again");
            }
        }
        return ret;
    }

    /**
     * Set player number by first connection
     * @param ply player number
     */
    public void setPlayer(String ply)
    {
        sendToTextArea("\nsetPlayer");
        m_player= Integer.parseInt(ply);
    }

    /**
     * Get player number for game operator
     * @return m_player
     */
    public int getPLayerNumber()
    {
        return m_player;
    }

    /**
     * Close client server
     */
    public void stopClientServer()
    {
        sendToTextArea("\nstopClientServer");
        m_cdr.close();
        m_go = false;
        m_connect = false;
    }

    /**
     * Checks if start game set to true or false. Can only start race if 2 players are connected.
     * @return m_canStart true or false
     */
    public Boolean canStartGame()
    {
        //sendToTextArea("\ncanStartGame");
        return m_canStart;
    }

    /**
     * Assign car data from other player to non controlled car
     * @param receivedCarData data received from the server
     */
    public void unpackNonController(String receivedCarData)  {
        //sendToTextArea("\nUpdating non controller: "+receivedCarData);
        try{
            if(m_player==1)
                m_policeCar.setCarData(receivedCarData);

            if(m_player==2)
                m_greenCar.setCarData(receivedCarData);
        }
        catch (Exception e)
        {
            sendToTextArea("\nCannot unpack non controller data: "+receivedCarData+".\n Error: "+e);
        }
        //sendToTextArea("\nUnpacking car data");
    }

    /**
     * Create string with controller car data to send to server and then other player
     * @return controller car data
     */
    public String packController()
    {
        var carDt="";
        if(m_player==1)
            carDt = m_greenCar.getPlayerNum()+","+m_greenCar.getCurrentImage()+","+m_greenCar.getX()+","+m_greenCar.getY()+","+m_greenCar.getSpeed()+","+m_greenCar.getLap();
        if(m_player==2)
            carDt = m_policeCar.getPlayerNum()+","+m_policeCar.getCurrentImage()+","+m_policeCar.getX()+","+m_policeCar.getY()+","+m_policeCar.getSpeed()+","+m_policeCar.getLap();
        //sendToTextArea("\nSending to server: car data : "+carDt);
        return carDt;
    }

    /**
     * User select track on program open. Return 0 for rectangle track 1 and 1 for oval track 2
     */
    public int chooseTrack()
    {
        String[] options = {"Track 1", "Track 2"};
        int trackChoice;
        trackChoice = JOptionPane.showOptionDialog(null,"Select a track", "Choose track",
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
        //System.out.println(this.getClass().getResource(name));
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
    public void checkCarCollision(Graphics g,TrackDR track)
    {
        Rectangle2D green =  m_greenCar.getCarBoundary(g);
        Rectangle2D police =  m_policeCar.getCarBoundary(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);

        // hit map boundaries with different message and sound played for each car
        if(track.getInnerBounds().intersects(green.getBounds2D()) || !track.getOuterBounds().intersects(green.getBounds2D())
         || track.getObstacleBounds().intersects(green.getBounds2D()))
        {

            sound.Play(m_crashGreen);

            m_greenCar.setSpeed(-m_greenCar.getSpeed() - m_greenCar.getSpeed());
            sendToTextArea("\nGreen car collided with the boundary");
        }
        if(track.getInnerBounds().intersects( police.getBounds2D()) || !track.getOuterBounds().intersects(police.getBounds2D())
                || track.getObstacleBounds().intersects(police.getBounds2D()))
        {

            sound.Play(m_crashPolice);
            m_policeCar.setSpeed(-m_policeCar.getSpeed() - m_policeCar.getSpeed());
            sendToTextArea("\nPolice car collided with the boundary");
        }

        if(green.intersects(police) ||
                police.intersects(green))
        // if statement to check if pixel visible or not within getbounds rectangle not affinetransformed shape
        {
            sound.Play(m_crashCars);
            m_greenCar.setSpeed(-m_greenCar.getSpeed() - m_greenCar.getSpeed());
            m_policeCar.setSpeed(-m_policeCar.getSpeed() - m_policeCar.getSpeed());
            sendToTextArea("\nCars have collided.");
        }
    }

    /**
     * Calculate and update lap count according to passing rectangles at start and at halfway point.
     * @param g - graphics interface
     */
    public void lapCount(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle r1 = new Rectangle( 417, 30, 12, 100); // at start line
        Rectangle r2 = new Rectangle(418,410,4,100);  // at halfway point

        Shape green =  m_greenCar.getCarBoundary(g);
        Shape police =  m_policeCar.getCarBoundary(g);

        //g2d.draw(r1); //testing
        //g2d.draw(r2); //testing
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
        sound.Play(m_countdown);
        int delayT = 5000;
        ActionListener taskPerformer = evt -> m_go = false;
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
        m_go = true;
    }
    
    public static void sendToTextArea(String mes)
    {
        MainframeDR.passToTextArea(mes);
    }

    /**
     * Internal class for client server
     */
    private class ClientDR extends Thread{
        private final int m_port;
        private final String m_serverName;
        private Socket m_socket;
        private boolean m_connected;
        private DataOutputStream m_out;
        private DataInputStream m_in;

        /**
         * New client constructor.
         */
        public ClientDR(int port, String serverName)
        {
            m_port = port;
            m_serverName = serverName;
            m_socket = null;
            m_connected= false;
        }

        /**
         * Run connection: receiving and sending data while its open.
         */
        @Override
        public void run(){
            connect();
            try {
              do {
                    sendReceive();
                } while (m_connected );
            }catch (Exception e)
            {
                sendToTextArea("\nError with client server run -> "+e);
            }
            finally
            {
                close();
                sendToTextArea("\nClosing client run");
            }
            sendToTextArea("\nExiting..");
        }


        /**
         * Create new client socket connection.
         * @return new socket connected tag
         */
        public boolean connect()
        {
            if (m_connected)
                return true;
            try
            {
                m_socket = new Socket(m_serverName, m_port);
                m_out = new DataOutputStream(m_socket.getOutputStream());
                m_in = new DataInputStream(m_socket.getInputStream());
                m_connected = true;
                sendToTextArea("\nConnected to server: "+m_serverName+", port: "+m_port);
            }
            catch( UnknownHostException e)
            {
                sendToTextArea("\nSock error in connect method clientDR: " + e);
            }
            catch( IOException e)
            {
                sendToTextArea("\nIO error in connect method clientDR: " + e);
            }
            return m_connected;
        }


        /**
         * Close socket connection.
         */
        public void close()
        {
            try {
                if (m_socket != null)
                {
                    m_socket.close();
                    m_out.close();
                    m_in.close();
                    m_connected = false;
                }
            }
            catch (IOException e)
            {
                sendToTextArea("\nError closing connection: "+e.getMessage());
            }
        }

        /**
         * Format outgoing string message to send to server via the data output stream
         *
         */
        public void sendReceive() {
            if (!m_connected) {
                sendToTextArea("\nNot connected to server.");
                return;
            }
            try {
                String incom = m_in.readUTF();
                handleIncomingClientTraffic(incom);
                m_out.writeUTF(packController());
                //sendToTextArea("\nSending to server");
            }
            catch(EOFException e)
            {
                sendToTextArea("\nEOF sendData error: "+e);
            }
            catch (IOException e )
            {
                sendToTextArea("\nIO sendData error: "+e);
            }
            catch(Exception e)
            {
                sendToTextArea("\nError sending to server: " + e.getMessage());
            }
        }

    }

}