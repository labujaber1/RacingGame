package DualRace;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.net.*;
import java.io.*;


public class ServerDR extends Thread{

    private final ServerSocket m_listenSocket;
    private final ArrayList<Connection> m_connections;


    // UDP server
    public ServerDR( int port ) throws IOException {
        this.setName("ServerThread");
        m_listenSocket = new ServerSocket(port);
        m_connections = new ArrayList<>();
    }
    @Override
    public void run()
    {
        System.out.println("run serverDR");
        try
        {
            //start server and wait for connection
            while (m_connections.size()<2) // change for two player
            {

                System.out.println("waiting for socket connection");
                Socket s = m_listenSocket.accept();
                System.out.println("new socket connected");
                Connection c = new Connection( s ,this);
                m_connections.add(c);
                c.start();
                System.out.println("New connection made");
                System.out.println("Client " +m_connections.size()+ " connected: "+s);
                if(m_connections.size()==1)
                {
                    m_connections.get(0).send("1");
                    System.out.println("Waiting for player 2 to join");
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
            System.out.println("Listen : + " + e);
        }
   }


    public void reply(String mes)
    {
        System.out.println("reply");
        for (Connection con : m_connections) {
            con.send(mes);
        }
   }
    public void close()
    {
        try
        {
            System.out.println("Closing socket");
            m_listenSocket.close();
        }
        catch (IOException e)
        {
            System.out.println("Failed to close Server Socket : + " + e);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            ServerDR server = new ServerDR( 8888 );
            server.start();
            System.out.println("Server running...\nAwaiting client communication..");
        }
        catch (IOException e)
        {
            System.out.println("Cant create server : + " + e);
        }
    }

}

class Connection extends Thread
{
    private DataInputStream m_in;
    private DataOutputStream m_out;
    private Socket m_clientSock;
    private ServerDR m_server;


    public Connection(Socket sock,ServerDR s)
    {
        try
        {
            System.out.println("connection");
            this.setName("ConnectionThread");
            m_clientSock = sock;
            m_in = new DataInputStream( m_clientSock.getInputStream());
            m_out = new DataOutputStream(m_clientSock.getOutputStream());
            m_server = s;
        }
        catch (IOException e)
        {
            System.out.println("Connection : " + e);
        }
    }

    // extra
    public void send(String mes)
    {
        try {
            System.out.println("Send");
            m_out.writeUTF(interp(mes));

        }
        catch (IOException e)
        {
            System.out.println("Error at server sending to client: "+e.getMessage());
        }
    }

    @Override
    public void run()
    {
        System.out.println("run connection serverDR");
        try
        {
            while (true)
            {
                String mes = m_in.readUTF();

                m_server.reply(interp(mes));
            }
        }
        catch (EOFException e)
        {
            System.out.println("Run() EOF :" + e);
        }
        catch (IOException e)
        {
            System.out.println("Run() IO :" + e);
        }

        finally
        {
            try
            {
                m_clientSock.close();
                System.out.println("Server closing client socket");
            }
            catch( IOException e )
            {
                System.out.println("Close failed on client socket : " + e);
            }
        }
     }



    /**
     * Filter message for close/exit connection command
     * @param mes String message to pass to output or input stream
     * @return Same message as in params no adjustment made
     */
    private String interp(String mes)
    {
        System.out.println("Message from client:> "+ mes); //added
        if (mes.equals("exit") || mes.equals("Bye"))
        {
            System.out.println("Closing client and server connections..");
            m_server.close();
        }
        if(mes.equals("Hello"))
        {
            send("Date: "+getDate() + ", Server IP address: " + getHostAddress());
        }
        return mes;
    }

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
            System.out.println("Unable to resolve address: "+e);
            return "Error getting host address: " + e.getMessage();
        }
    }

}
