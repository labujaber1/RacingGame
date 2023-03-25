package DualRace;


import java.io.*;
import java.net.*;


public class ClientDR extends Thread{
    private int m_port;
    private String m_serverName, m_receiveData,m_sendData;
    private Socket m_socket;
    private boolean m_connected;
    private DataOutputStream m_out;
    private DataInputStream m_in;



    /**
     * New client.
     */
    public ClientDR(int port, String serverName)
    {
        m_port = port;
        //m_port = 8888;
        m_serverName = serverName;
        m_socket = null;
        m_connected= false;
        //m_timer = new Timer();
    }

    public void run(){
        ClientDR tcpClient = new ClientDR(m_port,m_serverName);
        //if(m_serverName.equals(null)){m_serverName= m_ipaddress.getHostAddress();}
        tcpClient.connect();
        try {

            do {
                receiveData();
                wait(200);
                sendData(m_sendData);

             } while (m_connected);
        }catch(OutOfMemoryError e)
        {
            System.out.println("Error with client server run -> "+e);
        }
        catch(Exception e){
            System.out.println("Error with client server run -> "+e);
        }
        System.out.println("Exiting..");
        tcpClient.close();
    }
    /**
     * Connect to server.
     * @return
     */
    public boolean connect()
    {
        if (m_connected)
            return true;
        try
        {
            m_socket = new Socket( m_serverName, m_port ); //error no socket
            m_connected = true;
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
     * Close connection.
     */
    public void close()
    {
        try {
            if (m_socket != null) {
                m_socket.close();
                m_connected = false;
            }
        }
        catch (IOException e)
        {
            System.out.println("Error closing connection: "+e.getMessage());
        }
   }

    public void sendData(String outgoing) {

        try {
            // send to server
            if(outgoing != null) {
                m_out = new DataOutputStream(m_socket.getOutputStream());
                System.out.println("clientDR sendData 1");
                m_out.writeUTF(outgoing);
                System.out.println("clientDR sendData 2");
                m_sendData = outgoing;
                System.out.println("Sending to server = " + outgoing);
            }
        }
        catch(Exception e)
        {
            System.out.println("Error updating player: " + e.getMessage());
        }
    }
    public void receiveData()
    {
        try {
            // receive data from server about other player status
            m_in = new DataInputStream(m_socket.getInputStream());
            String incoming = m_in.readUTF();
            m_receiveData = incoming;
            System.out.println("Receiving from server = " + incoming);
        }
        catch(Exception e)
        {
            System.out.println("Error updating other player: " + e.getMessage());
        }
    }
    public String getRecDat()
    {
        return m_receiveData;
    }

}
