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
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static DualRace.ServerDR.m_textArea;

/**
 * Server class
 */
public class ServerDR extends Thread {

    private ServerSocket m_listenSocket;
    private final ArrayList<Connection> m_connections;
    private double x = 365.0; // for testing
    public static JTextArea m_textArea;
    private JFrame m_frame;
    private JButton m_start,m_stop;
    private JScrollPane m_sPane;
    public static int m_portNum;
    /**
     * TCP server initialises JFrame to start, stop and display message output.
     */
    public ServerDR( int port ) throws IOException {
        this.setName("ServerThread");

        m_portNum = port;
        m_connections = new ArrayList<Connection>();

        m_frame = new JFrame("Car game server");
        m_frame.setSize(500,500);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.getContentPane();
        m_frame.setResizable(false);
        Container c = new Container();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.ORANGE);
        m_frame.add(c);

        // Text panel
        JPanel textPanel = new JPanel(new BorderLayout(20,20));
        textPanel.setPreferredSize(new Dimension(470,440));
        textPanel.setBackground(Color.ORANGE);
        m_textArea = new JTextArea();
        m_textArea.setBackground(Color.lightGray);
        sendToTextArea("Server output display: \n");
        m_textArea.getPreferredScrollableViewportSize();
        m_textArea.setLineWrap(false);
        m_textArea.setEditable(false);

        m_sPane = new JScrollPane(m_textArea);
        m_sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        m_sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textPanel.add(m_sPane,BorderLayout.CENTER);

        m_start = new JButton("START SERVER");
        m_start.setEnabled(true);
        m_start.addActionListener(new ButtonWatcher());
        m_stop = new JButton("STOP SERVER");
        m_stop.setEnabled(true);
        m_stop.addActionListener(new ButtonWatcher());
        textPanel.add(m_start,BorderLayout.NORTH);
        textPanel.add(m_stop,BorderLayout.SOUTH);

        c.add(textPanel);
        m_stop.setFocusable(true);
        m_start.setFocusable(true);
        m_frame.setFocusable(true);
        m_frame.setVisible(true);
    }

    /**
     * Send messages to text area for user to view.
     * @param mes string message
     */
    public static void sendToTextArea(String mes)
    {
        m_textArea.append("\n"+mes);
    }
    /**
     * ActionListener for start and stop server buttons.
     */
    private class ButtonWatcher implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object buttonPressed =	e.getSource();
            if (buttonPressed.equals(m_start))
            {
                sendToTextArea("Server running...\nAwaiting client communication..");
                // start server
                Thread thread = new Thread(ServerDR.this::start);
                thread.start();
            }
            if(buttonPressed.equals(m_stop))
            {
                for(Connection con : m_connections) {
                    con.closeConnection();
                    sendToTextArea("Closing all connections");
                }
                close();
            }
        }
    }

    /**
     * Create client connections and add to connection arraylist.
     * Send player status to client when connected.
     * Waits for 2 client before client can start game.
     */
    @Override
    public void run()
    {
        sendToTextArea("run: serverDR");
        try
        {
            m_listenSocket = new ServerSocket(m_portNum);
            m_listenSocket.setReuseAddress(true);
            //start server and wait for connection
            while (m_connections.size()<2)
            {
                if(m_connections.size()>2){checkConnections();}
                sendToTextArea("Server: Run: waiting for socket connection");
                Socket s = m_listenSocket.accept();
                sendToTextArea("Server: Run: new socket connection created");
                Connection c = new Connection( s ,this);
                m_connections.add(c);
                sendToTextArea("Server: Run: connection started");
                //new Thread(c).start();
                c.start();
                sendToTextArea("Server: Run: New connection made "+ c);
                sendToTextArea("Server: Run: Client " +m_connections.size()+ " connected: "+s);
                for (Connection con : m_connections){sendToTextArea("Conn -> "+con);}
                if(m_connections.size()==1)
                {
                    m_connections.get(0).send("1");

                    sendToTextArea("Waiting for player 2 to join");
                }
                if(m_connections.size()==2)
                {
                    m_connections.get(1).send("2");
                    reply("CAN RACE");
                }
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,"Client server startup failed"+e.getMessage(),"Connection failure",JOptionPane.INFORMATION_MESSAGE);
            sendToTextArea("Listen : + " + e);
        }
   }

    /**
     * Check connections list for dead connections and remove if found.
     * Closed sockets due to error will reside in list and give incorrect
     * connection results when main method run is assigning player status.
     * Closed sockets cannot be reconnected to.
     */
    public void checkConnections()
    {
        sendToTextArea("server: check connection..");
        int i=0;
        for(Connection c : m_connections){
            if (!c.isAlive())
            {
                i= m_connections.indexOf(c);
                sendToTextArea("CheckConnection: Dead connection : "+c+"in connections list has been removed.");
            }
            if(c.isAlive())
            {
                i=m_connections.indexOf(c);
                sendToTextArea("Checkconnection: Is alive : " +c);
            }
        }
        m_connections.remove(i);
    }

    /**
     * Takes in a string message to send to all clients in connections arraylist.
     * @param mes string message
     */
    public void reply(String mes)
    {
        //String test = player2TestData();
        //sendToTextArea("Car data: "+mes);
        // passing on received message, if data from player1 send conn 2: arraylist index 1
        if (mes.startsWith("1") && mes.length()>1)
        {
            m_connections.get(1).send(mes);
            //sendToTextArea("Car data: "+mes);
        }
        // passing on received message, if data from player2 send comm 1: arraylist index 0
        else if (mes.startsWith("2") && mes.length()>1)
        {
            m_connections.get(0).send(mes);
            //sendToTextArea("Car data: "+mes);
        }
        // drop packet if player status not assigned to client by server otherwise send
        else if (!mes.startsWith("0"))
        {
            for (Connection con : m_connections) {
                con.send(mes);
                //sendToTextArea("!0 data: "+mes);
                //con.send(test); ///////////////////////// for testing
                //sendToTextArea("server reply: "+test);
            }
        }
    }

    /**
     * Data generated to simulate other player for testing client processing.
     * @return car data to simulate movement
     */
    public String player2TestData()
    {
        x = x+0.0002;
        String sx = Double.toString(x);
        String mes = "2,4,"+sx+",90,0,0";
        //sendToTextArea("server: player2testdata: "+mes);
        return mes;
    }

    /**
     * Close server socket.
     */
    public void close()
    {
        try
        {
            sendToTextArea("Server: Close socket");
            m_listenSocket.close();
        }
        catch (IOException e)
        {
            sendToTextArea("Failed to close Server Socket : + " + e);
        }
    }

    /**
     * Main method started separately from client program.
     * @param args Main args
     */
    public static void main(String[] args) {

        try {
            String _portNum = JOptionPane.showInputDialog("Enter a port number","8887");
            m_portNum = Integer.parseInt(_portNum);
            ServerDR server = new ServerDR( m_portNum );
            server.setName("Server DR");
            sendToTextArea("Port number : "+m_portNum+"\nPress the button to start the server.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * Connection class for a single client socket connection.
 */
class Connection extends Thread
{
    private DataInputStream m_in;
    private DataOutputStream m_out;
    private Socket m_clientSock;
    private ServerDR m_server;

    /**
     * Send messages to text area for user to view.
     * @param mes string message
     */
    public static void sendToTextArea(String mes)
    {
        m_textArea.append("\n"+mes);
    }
    /**
     * Constructor.
     * @param sock Socket
     * @param s Server
     */
    // clientHandler
    public Connection(Socket sock,ServerDR s)
    {
        try
        {
            this.setName("ConnectionThread");
            m_clientSock = sock;
            m_in = new DataInputStream( m_clientSock.getInputStream());
            m_out = new DataOutputStream(m_clientSock.getOutputStream());
            m_server = s;
        }
        catch (IOException e)
        {
            sendToTextArea("Connection : " + e);
        }
    }

    /**
     * Write string message to output stream passing through filter first.
     * Another method to send.
     * @param mes string message
     */
    public void send(String mes)
    {
        try {
            m_out.writeUTF(interp(mes));
        }
        catch (IOException e)
        {
            sendToTextArea("Error at server sending to client: "+e.getMessage());
        }
    }

    /**
     * Close socket.
     */
    public void closeConnection()
    {
        try {
            m_clientSock.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Override start method with run to begin reading and writing to input and output stream.
     * In a continuous loop while connection exists.
     */
    @Override
    public void run()
    {
        try
        {
            while(m_clientSock.isConnected()){
                String mes = m_in.readUTF();
                m_server.reply(interp(mes));
            }
            try {
                m_out.writeUTF("Socket closed : "+m_clientSock);
            } catch (IOException ex) {
                sendToTextArea(""+ex);
            }
        }
        catch (EOFException e)
        {
            sendToTextArea("Run: EOF : client socket closed, " + e);
        }
        catch (IOException e)
        {
            sendToTextArea("Run: IO :" + e);
        }
     }

    /**
     * Filter message for close/exit connection command
     * @param mes String message to pass to output or input stream
     * @return Same message in params, no adjustment made
     */
    private String interp(String mes)
    {
        if (mes.equals("exit") || mes.equals("close"))
        {
            try {
                sendToTextArea("Closing client and server connections..");
                m_clientSock.close();
            }catch(Exception e){
                sendToTextArea("Error closing socket: "+e.getMessage());
            }
        }
        return mes;
    }
}
