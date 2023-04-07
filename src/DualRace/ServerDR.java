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

    private final ServerSocket m_listenSocket;
    private final ArrayList<Connection> m_connections;
    private double x = 365.0; // for testing
    public static JTextArea m_textArea;
    private JFrame m_frame;
    private JButton m_start,m_stop;
    private JScrollPane m_sPane;
    public static String m_portNum;
    /**
     * TCP server initialises JFrame to start, stop and display message output.
     */
    public ServerDR( int port ) throws IOException {
        this.setName("ServerThread");

        m_listenSocket = new ServerSocket(port);
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
        m_textArea.append("Server output display: \n");
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
     * ActionListener for start and stop server buttons.
     */
    private class ButtonWatcher implements ActionListener  {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object buttonPressed =	e.getSource();
            if (buttonPressed.equals(m_start))
            {
                m_textArea.append("\nServer running...\nAwaiting client communication..");
                // start server
                Thread updates = new Thread(ServerDR.this::start);
                updates.start();
                m_frame.requestFocus();
            }
            if(buttonPressed.equals(m_stop))
            {
                for(Connection con : m_connections) {
                    con.closeConnection();
                    m_textArea.append("\nClosing connection");
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
        m_textArea.append("\nrun: serverDR");
        try
        {
            //start server and wait for connection
            while (m_connections.size()<2)
            {
                if(m_connections.size()>0){checkConnections();}
                m_textArea.append("\nServer: Run: waiting for socket connection");
                Socket s = m_listenSocket.accept();
                m_textArea.append("\nServer: Run: new socket connection created");
                Connection c = new Connection( s ,this);
                m_connections.add(c);
                m_textArea.append("\nServer: Run: connection started");
                c.start();
                m_textArea.append("\nServer: Run: New connection made "+ c);
                m_textArea.append("\nServer: Run: Client " +m_connections.size()+ " connected: "+s);
                for (Connection con : m_connections){m_textArea.append("\nConn -> "+con);}
                if(m_connections.size()==1)
                {
                    m_connections.get(0).send("1");

                    m_textArea.append("\nWaiting for player 2 to join");
                }
                if(m_connections.size()==2)
                {
                    m_connections.get(1).send("2");
                    reply("GO");
                }
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,"Client server startup failed"+e.getMessage(),"Connection failure",JOptionPane.INFORMATION_MESSAGE);
            m_textArea.append("\nListen : + " + e);
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
        m_textArea.append("\nserver: check connection..");
        int i=0;
        for(Connection c : m_connections){
            if (!c.isAlive()){
                i= m_connections.indexOf(c);
                m_textArea.append("\nCheckConnection: Dead connection : "+c+"in connections list has been removed.");
            }
            if(c.isAlive()){
                i=m_connections.indexOf(c);
                m_textArea.append("\nCheckconnection: Is alive : " +c);
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
        //m_textArea.append("\nServer: reply: ");
        String test = player2TestData();
        for (Connection con : m_connections) {
            con.send(mes);
            //m_connections.get(0).send(player2TestData()); //// testing
            //con.send(test); ///////////////////////// testing
            //m_textArea.append("\nserver reply: "+test);
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
        String mes = "4,"+sx+",90,0,0";
        //m_textArea.append("\nserver: player2testdata: "+mes);
        return mes;
    }

    /**
     * Close server socket.
     */
    public void close()
    {
        try
        {
            m_textArea.append("\nServer: Close socket");
            m_listenSocket.close();
        }
        catch (IOException e)
        {
            m_textArea.append("\nFailed to close Server Socket : + " + e);
        }
    }





    /**
     * Main method started separately from client program.
     * @param args Main args
     */
    public static void main(String[] args) {

        try {
            m_portNum = JOptionPane.showInputDialog("Enter a port number","8887");

            ServerDR server = new ServerDR( Integer.parseInt(m_portNum) );

            m_textArea.append("\nPort number : "+m_portNum+"\nPress the button to start the server.");
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
     * Constructor.
     * @param sock Socket
     * @param s Server
     */
    public Connection(Socket sock,ServerDR s)
    {
        try
        {
            m_textArea.append("\nServer connection: ");
            this.setName("ConnectionThread");
            m_clientSock = sock;
            m_in = new DataInputStream( m_clientSock.getInputStream());
            m_out = new DataOutputStream(m_clientSock.getOutputStream());
            m_server = s;

        }
        catch (IOException e)
        {
            m_textArea.append("\nConnection : " + e);
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
            //m_textArea.append("\nServer connection: Send: mes");
            m_out.writeUTF(interp(mes));
        }
        catch (IOException e)
        {
            m_textArea.append("\nError at server sending to client: "+e.getMessage());
        }
    }

    /**
     * Close socket.
     */
    public void closeConnection() {
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
        m_textArea.append("\nServer: connection: Run");
        try
        {
            while(m_clientSock.isConnected()){
                String mes = m_in.readUTF();
                m_server.reply(interp(mes));
                //m_textArea.append("\nServer run: m_in message: " + mes);
            }
        }
        catch (EOFException e)
        {
            m_textArea.append("\nRun: EOF :" + e);
        }
        catch (IOException e)
        {
            m_textArea.append("\nRun: IO :" + e);
        }
     }

    /**
     * Filter message for close/exit connection command
     * @param mes String message to pass to output or input stream
     * @return Same message in params, no adjustment made
     */
    private String interp(String mes) {
        //m_textArea.append("\nMessage from client:> "+ mes); //added
        if (mes.equals("exit") || mes.equals("close"))
        {
            try {
                m_textArea.append("\nClosing client and server connections..");
                m_clientSock.close();
                m_server.close();
            }catch(Exception e){
                m_textArea.append("\nError closing socket: "+e.getMessage());
            }
        }
        /*
        if(mes.equals("Hello"))
        {
            send("Date: "+getDate() + ", Server IP address: " + getHostAddress());
        }
        */
        return mes;
    }
/*
    // add date to initial connection setup
    public String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.now();
        return dtf.format(ldt);
    }

    public String getHostAddress()
    {
        try {
            InetAddress host = InetAddress.getLocalHost();
            return host.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            m_textArea.append("\nUnable to resolve address: "+e);
            return "Error getting host address: " + e.getMessage();
        }
    }
*/
}
