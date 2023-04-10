/**
 * Car race for two players using client server.
 */
package DualRace;

import java.io.*;
import java.net.*;

/**
 * Separate client server class now deprecated.
 * @deprecated Class merged into GamepanelDR class
 */
public class ClientDR extends Thread{
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
                //System.out.println("ClientDr sendReceive");
                Thread.sleep(200);
            } while (m_connected );
        }catch(InterruptedException e)
        {
            System.out.println("Error with client server run -> "+e);
        }
        finally
        {
            close();
            System.out.println("Closing client run");
        }
        System.out.println("Exiting..");

    }


    /**
     * Create new socket connection.
     * @return connected true or false
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
            System.out.println("Connected to server: "+m_serverName+", port: "+m_port);
        }
        catch( UnknownHostException e)
        {
            System.out.println("Sock error in connect method clientDR: " + e);
        }
        catch( IOException e)
        {
            System.out.println("IO error in connect method clientDR: " + e);
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
            System.out.println("Error closing connection: "+e.getMessage());
        }
   }

    /**
     * Format outgoing string message to send to server via the data output stream
     *
     */
    public void sendReceive() {
        if (!m_connected) {
            System.out.println("Not connected to server.");
            return;
        }
        try {
            String filterIn = m_in.readUTF();
            System.out.println("Sending to server");
        }
        catch(EOFException e)
        {
            System.out.println("EOF sendData error: "+e);
        }
        catch (IOException e )
        {
            System.out.println("IO sendData error: "+e);
        }
        catch(Exception e)
        {
            System.out.println("Error sending to server: " + e.getMessage());
        }
    }
}



