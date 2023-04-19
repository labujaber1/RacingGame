/**
 * Title: Distributed multi-player racing game.
 * <p>Description: Two player car race game using client server setup.</p>
 * Date: 21/04/2023
 * @author labuj 2018481
 * @version 1.3
 */
package DualRace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;


/**
 * Main JFrame with buttons and key presses.
 */
public class MainframeDR extends JFrame implements KeyListener{
    private static JButton m_goBut;
    private final JButton m_exitBut;
    private final JButton m_connect;
    private final JButton m_disconnect;
    private final GamepanelDR m_gp;
    private static JTextArea m_comms;
    private static Boolean setGoBut = false;
    private static JTextArea m_textarea;

    /**
     * Create JFrame containing JPanels to display a racetrack and cars for users
     * to race each other from a single keyboard.
     * Contains button and key press listeners.
     * @param title Title provided in run method
     */
    public MainframeDR(String title) {
        super(title);
        setSize(1150, 650);
        setResizable(false);
        m_gp = new GamepanelDR();
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());
        // buttons and text area set
        m_goBut = new JButton("GO");
        m_connect = new JButton("Connect");
        m_disconnect = new JButton("Disconnect");
        m_exitBut = new JButton("EXIT");
        m_goBut.setEnabled(true);
        m_goBut.setBackground(Color.GREEN);
        m_connect.setEnabled(true);
        m_disconnect.setEnabled(false);
        m_exitBut.setBackground(Color.gray);
        m_goBut.addActionListener(new ButtonWatcher());
        m_connect.addActionListener(new ButtonWatcher());
        m_disconnect.addActionListener(new ButtonWatcher());
        m_exitBut.addActionListener(new ButtonWatcher());

        m_textarea = new JTextArea("Hi and welcome to this super duper racing game -> Use directional keys to operate the green car and" +
                " \nWADZ keys for the police car. Practice first, select car button when ready and press GO to start the race");
        m_textarea.setLineWrap(false);
        Font font = new Font("Sans Serif",Font.BOLD,14);
        m_textarea.setFont(font);
        m_textarea.setBackground(Color.lightGray);

        JPanel headingText = new JPanel();
        headingText.setBackground(Color.lightGray);
        headingText.add(m_textarea,0);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(m_goBut);
        buttonPanel.add(m_connect);
        buttonPanel.add(m_disconnect);
        buttonPanel.add(m_exitBut);

        JPanel textPanel = new JPanel(new BorderLayout(20,20));
        textPanel.setPreferredSize(new Dimension(300,440));
        textPanel.setBackground(Color.ORANGE);
        m_comms = new JTextArea();
        m_comms.append("Client server output display: \n");
        m_comms.getPreferredScrollableViewportSize();
        m_comms.setBackground(Color.lightGray);
        m_comms.setLineWrap(false);
        m_comms.setEditable(false);

        JScrollPane m_sPane = new JScrollPane(m_comms);
        m_sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        m_sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textPanel.add(m_sPane,BorderLayout.CENTER);

        m_gp.setBackground(Color.lightGray);
        m_gp.setVisible(true);

        cp.add(buttonPanel, BorderLayout.SOUTH);
        cp.add(headingText,BorderLayout.NORTH);
        cp.add(m_gp, BorderLayout.CENTER);
        cp.add(textPanel,BorderLayout.EAST);
        m_goBut.setFocusable(false);
        m_gp.startAnimation();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }

    public static void passToTextArea(String mes)
    {
        m_comms.append("\n"+mes);
        // blank out go button when other player starts race
        if(Objects.equals(mes, "hideGoButton"))
            m_goBut.setEnabled(false);

    }
    /**
     * Button listeners to start race, connect and disconnect to server, and exit game.
     */
    private class ButtonWatcher implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent a) {
            Object buttonPressed =	a.getSource();
            if (buttonPressed.equals(m_goBut)&& m_gp.canStartGame().equals(true))
            {
                m_gp.sendGoMessage();
                m_goBut.setEnabled(false);
                m_gp.resetAllCars();
                requestFocus(true);
            }
            if (buttonPressed.equals(m_goBut)&& m_gp.canStartGame().equals(false))
            {
                JOptionPane.showMessageDialog(null, "Waiting for player 2 to join");
                requestFocus();
            }
            if(buttonPressed.equals(m_connect))
            {
                m_connect.setEnabled(false);
                m_disconnect.setEnabled(true);
                m_gp.startClientServer();
                requestFocus(true);
            }
            if(buttonPressed.equals(m_disconnect))
            {
                m_connect.setEnabled(true);
                m_disconnect.setEnabled(false);
                m_gp.stopClientServer();
                requestFocus(true);
            }
            if(buttonPressed.equals(m_exitBut))
            {
               System.exit(0);
            }
        }
    }

    /**
     * Key press events for calling methods from the gamepanel class
     * to turn each car and alter their speed.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        // set speed from method in gamepanel
        int speed = 2,greenC=1,policeC=2,player=m_gp.getPLayerNumber();
        // allow online and offline two player
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP -> {
                //P2 handle up move
                m_comms.append("\nP2 UP Key pressed: " + e.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.setCarSpeed(speed,greenC);
                else
                    m_gp.setCarSpeed(speed,player);
            }
            case KeyEvent.VK_DOWN -> {
                //P2 handle down
                m_comms.append("\nP2 DOWN Key pressed: " + e.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.setCarSpeed(-speed,greenC);
                else
                    m_gp.setCarSpeed(-speed,player);
            }
            case KeyEvent.VK_W -> {
                //P1 handle up move
                m_comms.append("\nP1 UP Key pressed: " + e.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.setCarSpeed(speed,policeC);
                else
                    m_gp.setCarSpeed(speed,player);
            }
            case KeyEvent.VK_Z-> {
                //P1 handle down
                m_comms.append("\nP1 DOWN Key pressed: " + e.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.setCarSpeed(-speed,policeC);
                else
                    m_gp.setCarSpeed(-speed,player);
            }
        }
    }

    /**
     * Using key press to turn a car slowly
     * @param arg0 the event to be processed
     */
    @Override
    public synchronized void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
        int keyCode = arg0.getKeyCode();
        // allow online and two layer offline function
        int player = m_gp.getPLayerNumber();
        boolean start = m_gp.canStartGame();
        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                //P2 handle left
                m_comms.append("\nP2 LEFT Key pressed: " + arg0.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.greenLeft();
                if(start && player == 1)
                    m_gp.greenLeft();
            }
            case KeyEvent.VK_RIGHT -> {
                //P2 handle right
                m_comms.append("\nP2 RIGHT Key pressed: " + arg0.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.greenRight();
                if(start && player == 1)
                    m_gp.greenRight();
            }
            case KeyEvent.VK_A -> {
                //P1 handle left
                m_comms.append("\nP1 LEFT Key pressed: " + arg0.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.policeLeft();
                if(start && player == 2)
                    m_gp.policeLeft();
            }
            case KeyEvent.VK_D -> {
                //P1 handle right
                m_comms.append("\nP1 RIGHT Key pressed: " + arg0.getKeyCode());
                if(!m_gp.canStartGame())
                    m_gp.policeRight();
                if(start && player == 2)
                    m_gp.policeRight();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
}
