/**
 * Title: Distributed multiplayer racing game.
 * <p>Description: Display a set of multi-angled car images in both a continuous loop
 *  * and operated by key press.</p>
 * &#064;date  21/04/2023
 * @author 2018481
 * @version 1.1
 */
package CarSpin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main frame creates JFrame and keylisteners.
 */
public class MainframeCS extends JFrame implements KeyListener{
    private JButton m_go, m_stop;
    private GamepanelCS m_gp;

    /**
     * Create GUI to display car images with two buttons to stop and start
     * the car spinning animation.
     * @param title
     */
    public MainframeCS(String title) {
        super(title);
        setSize(500, 500);

        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout());

        m_go = new JButton("GO");
        m_stop = new JButton("STOP");
        m_go.setEnabled(false);
        m_stop.setEnabled(true);
        m_go.addActionListener(new ButtonWatcher());
        m_stop.addActionListener(new ButtonWatcher());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(m_go);
        buttonPanel.add(m_stop);

        cp.add(buttonPanel, BorderLayout.SOUTH);

        m_gp = new GamepanelCS();
        cp.add(m_gp, BorderLayout.CENTER);

        m_gp.startAnimation(); //starts on open even if go button not pressed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }

    /**
     * Action event controller for start and stop animation buttons.
     */
    private class ButtonWatcher implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent a) {
            Object buttonPressed =	a.getSource();
            if (buttonPressed.equals(m_go))
            {
                m_gp.startAnimation();
                m_go.setEnabled(false);
                m_stop.setEnabled(true);

            }
            else if (buttonPressed.equals(m_stop))
            {
                m_gp.stopAnimation();
                m_go.setEnabled(true);
                m_stop.setEnabled(false);

            }
        }
    }

    /**
     * Key press controller to select a car image from the array
     * of images.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        int step = 1;
        int keyCode = e.getKeyCode();

        switch(keyCode){
            case KeyEvent.VK_LEFT:
                //handle left
                System.out.println("LEFT Key pressed: " + e.getKeyCode());
                m_gp.turnImg( -step);
                break;
            case KeyEvent.VK_RIGHT:
                //handle right
                System.out.println("RIGHT Key pressed: " + e.getKeyCode());
                m_gp.turnImg( step);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
}
