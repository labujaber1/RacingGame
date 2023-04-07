package CarAndMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game operating from the same keyboard.
 */
public class MainframeCAM extends JFrame implements KeyListener{
    private final JButton m_goBut, m_exitBut;
    private final GamepanelCAM m_gp;
    private JTextArea m_textarea;



    /**
     * Create JFrame containing JPanels to display a racetrack and cars for users
     * to race each other from a single keyboard.
     * Contains button and key press listeners.
     * @param title
     */
    public MainframeCAM(String title) {
        super(title);
        setSize(850, 650);
        m_gp = new GamepanelCAM();
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());
        // buttons and text area set
        m_goBut = new JButton("GO");
        m_exitBut = new JButton("EXIT");
        m_goBut.setEnabled(true);
        m_goBut.setBackground(Color.GREEN);
        m_exitBut.setBackground(Color.gray);
        m_goBut.addActionListener(new ButtonWatcher());
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
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(m_goBut);
       buttonPanel.add(m_exitBut);

        m_gp.setBackground(Color.lightGray);
        m_gp.setVisible(true);

        cp.add(buttonPanel, BorderLayout.SOUTH);
        cp.add(headingText,BorderLayout.NORTH);
        cp.add(m_gp, BorderLayout.CENTER);
        m_goBut.setFocusable(false);
        m_gp.startAnimation(); //starts on open even if the go button not pressed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }

    /**
     * Button listeners to start, select a car and exit game.
     */
    private class ButtonWatcher implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent a) {
            Object buttonPressed =	a.getSource();
            if (buttonPressed.equals(m_goBut))
            {
                m_goBut.setEnabled(false);
                m_gp.resetAllCars();
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
        int speed = 2,greenC=1,policeC=2;
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP -> {
                //P2 handle up move
                System.out.println("P2 UP Key pressed: " + e.getKeyCode());
                m_gp.setCarSpeed(speed,greenC);
            }
            case KeyEvent.VK_DOWN -> {
                //P2 handle down
                System.out.println("P2 DOWN Key pressed: " + e.getKeyCode());
                m_gp.setCarSpeed(-speed,greenC);
            }
            case KeyEvent.VK_W -> {
                //P1 handle up move
                System.out.println("P1 UP Key pressed: " + e.getKeyCode());
                m_gp.setCarSpeed(speed,policeC);
            }
            case KeyEvent.VK_Z-> {
                //P1 handle down
                System.out.println("P1 DOWN Key pressed: " + e.getKeyCode());
                m_gp.setCarSpeed(-speed,policeC);
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
        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                //P2 handle left
                System.out.println("P2 LEFT Key pressed: " + arg0.getKeyCode());
                m_gp.greenLeft();
            }
            case KeyEvent.VK_RIGHT -> {
                //P2 handle right
                System.out.println("P2 RIGHT Key pressed: " + arg0.getKeyCode());
                m_gp.greenRight();
            }
            case KeyEvent.VK_A -> {
                //P1 handle left
                System.out.println("P1 LEFT Key pressed: " + arg0.getKeyCode());
                m_gp.policeLeft();
            }
            case KeyEvent.VK_D -> {
                //P1 handle right
                System.out.println("P1 RIGHT Key pressed: " + arg0.getKeyCode());
                m_gp.policeRight();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
}
