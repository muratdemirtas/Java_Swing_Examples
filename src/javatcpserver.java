/*
 * Created by Murat DEMİRTAŞ on 9.04.2017.
 * Simple Java TCP Server Example For Socket Programming
*/

/* Importing requirements java classes
   Java data stream classes, Random integer generator and networking classes
*/

import java.io.*;           //Java Input/Output Classes
import java.net.*;          //Java Networking Operation Classes
import java.util.Random;    //Java Random Utility Classes

//main class for java tcp server
public class javatcpserver {

    //Enum status types defined for server events
    public enum SERVER_STATUS {
        SERVER_IS_NOT_OPEN,
        SERVER_CREATED,
        SERVER_IS_GOING_DOWN,
        SERVER_UNKNOW_ERROR,
        SERVER_HAS_NEW_CONNECTION,
        SERVER_RECEIVED_NEW_DATA,
        SERVER_CANNOT_BIND,
    }

    //static variables for this example
    static final int PORT_NUMBER = 5555;    //port number of listening port number..
    static final int generator_gain = 1000; //generator gain for random integer

    //main method of tcpserver class.
    public static void main(String argv[]) throws Exception {

        //variables for this method
        String received_Message = ""; // for receiving data from tcp clients
        SERVER_STATUS m_serverStatus; // server status.

        System.out.println("Creating TCP Server..");  //print message to console
        m_serverStatus = SERVER_STATUS.SERVER_IS_NOT_OPEN;

        //create new TCP/IP Server object.
        ServerSocket tcpServer = new ServerSocket(PORT_NUMBER);

        //check if another service is not running on this port number
        if(tcpServer.isBound())
            System.out.println("Created TCP Server On Port: "+ PORT_NUMBER);

        else {
            m_serverStatus = SERVER_STATUS.SERVER_IS_NOT_OPEN;
            System.out.println("Cannot Server Bind On Port: " + PORT_NUMBER + "is Port Closed or Running?");
            System.exit(1);
        }

        //if we are here, then our server must be online.
        m_serverStatus = SERVER_STATUS.SERVER_CREATED;

        //main method forever loop function
        while (true)

        {
            //listen server and check if new connection is coming..
            Socket incomingConnection = tcpServer.accept();
            m_serverStatus = SERVER_STATUS.SERVER_HAS_NEW_CONNECTION;

            //Create a new reader with buffered for this tcp socket id descriptor.
            //with getInputStream()
            BufferedReader readFromClient = new BufferedReader(
                    new InputStreamReader(incomingConnection.getInputStream()));

            //receive all data from client until over /r/n specific character
            received_Message = readFromClient.readLine();

            //create dataOutputStream for sending data to tcp client.
            DataOutputStream sendToClient = new DataOutputStream(incomingConnection.getOutputStream());
            m_serverStatus = SERVER_STATUS.SERVER_RECEIVED_NEW_DATA;
            System.out.println("Received From TCP Client: "+ received_Message);

            //calculate of this received data.
            int received_checksum = checkChecksumBytes(received_Message);

            //create new checksum data with random generator
            Random generator = new Random();
            int new_checksum = (generator.nextInt(10)* generator_gain) % 256;

            //send this strings to tcp client over TCP
            sendToClient.writeBytes("Hello My Friend, I Received Your String...\r\n");
            sendToClient.writeBytes("I Received Checksum From You: "+ received_checksum + "\r\n");
            sendToClient.writeBytes("My Sending Data Checksum Is: "+ new_checksum + "\r\n");
            sendToClient.writeBytes("I'm Closing The Connection, Bye Bye...\r\n");

            //after sending data. Now we can close this server with success(0);
            m_serverStatus = SERVER_STATUS.SERVER_IS_GOING_DOWN;
            received_Message =  "";

            //drop TCP Client Connections and stop listening on port number.
            tcpServer.close();

            //exit 0, means success, because we have done all job.
            System.exit(0);
        }
    }

        //static method for calculation of strings bytes as int
        public static int checkChecksumBytes(String message) {

            //variable for holding sum of checksum bytes
            int sum_of_recv_bytes = 0;
            int checksum = 0;

            //calculates all chars in the string as integer value
            for (int i = 0; i < message.length(); i++)
                sum_of_recv_bytes = sum_of_recv_bytes + (int)message.charAt(i);

            checksum = sum_of_recv_bytes % 256;
            System.out.println("Checksum Of This String Is :" + sum_of_recv_bytes);
            return checksum;
        }
    }




